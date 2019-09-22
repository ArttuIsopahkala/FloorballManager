package com.ardeapps.floorballmanager.analyzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Pair;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Connection;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    // These must be set before calling methods. GoalsInGames is not loaded automatically.
    // See LinesFragment and analyze chemistry button.
    static Map<String, ArrayList<Goal>> goalsInGames = new HashMap<>();
    static Map<String, ArrayList<Line>> linesInGames = new HashMap<>();
    static Map<String, Player> playersInTeam = new HashMap<>();
    private static AnalyzerService instance;
    private static boolean isJsonDatabase = false;
    private static boolean chemistryPercentAnalyzerInitialized = false;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if (!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getActivePlayersMap(false);
        }
        if (!chemistryPercentAnalyzerInitialized) {
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()));
            chemistryPercentAnalyzerInitialized = true;
        }
        return instance;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setGoalsInGames(Map<String, ArrayList<Goal>> goals) {
        goalsInGames = goals;
        isJsonDatabase = true;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setLinesInGames(Map<String, ArrayList<Line>> lines) {
        linesInGames = lines;
        isJsonDatabase = true;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setPlayersInTeam(Map<String, Player> players) {
        playersInTeam = players;
        isJsonDatabase = true;
    }

    public interface AnalyzeListener {
        void onAnalyzeCompleted(Map<Integer, Line> lines);
    }

    private static boolean lineContainsAlreadyAddedPlayer(Line line, ArrayList<Line> lineCombinations) {
        for (Line lineCombination : lineCombinations) {
            Map<String, String> playersInLine = lineCombination.getPlayerIdMap();
            for (String playerId : playersInLine.values()) {
                if (line.getPlayerIdMap().values().contains(playerId)) {
                    // Return if lines contains duplicated playerId
                    return true;
                }
            }
        }
        return false;
    }

    public interface GetBestLinesListener {
        void onGetBestLines(Map<Integer, Line> lines);
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get best lines
     *
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     */
    public void getBestLines(AllowedPlayerPosition allowedPlayerPosition, BestLineType bestLineType, Integer gameCount, final GetBestLinesListener listener) {
        ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()));
        GetBestLinesTask task = new GetBestLinesTask(AppRes.getActivity(), allowedPlayerPosition, bestLineType, listener::onGetBestLines);
        task.execute();
    }

    /**
     * MAIN METHOD (called from UI)
     * <p>
     * Get average percent for line. Average is calculated from closest positions.
     *
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercent(Line line) {
        int percent = 0;
        if (line != null && line.getPlayerIdMap() != null) {
            Map<Connection, Integer> chemistryConnectionPercents = getChemistryConnectionPercents(line.getPlayerIdMap());
            int chemistryConnectionCount = Connection.values().length;
            double chemistryConnectionSum = 0;
            for (Integer connectionPercent : chemistryConnectionPercents.values()) {
                chemistryConnectionSum += connectionPercent;
            }
            if (chemistryConnectionSum > 0) {
                percent = (int) Math.round(chemistryConnectionSum / chemistryConnectionCount);
            }
        }
        return percent;
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get average percent for team. Percent is average of line chemistry percentages.
     *
     * @param lines team lines
     * @return chemistry percent for team
     */
    public int getTeamChemistryPercent(Map<Integer, Line> lines) {
        int percent = 0;
        double lineCount = lines.size();
        if (lineCount > 0) {
            double percentSum = 0;
            for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
                Line line = entry.getValue();
                percentSum += getLineChemistryPercent(line);
            }
            percent = (int) Math.round(percentSum / lineCount);
        }
        return percent;
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     *
     * @param playersMap which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<Connection, Integer> getChemistryConnectionPercents(Map<String, String> playersMap) {
        Map<Connection, Integer> chemistryMap = new HashMap<>();
        if (playersMap != null) {
            // Convert map to get player easier
            Map<Position, Player> playersInLine = new HashMap<>();
            for (Map.Entry<String, String> playerEntry : playersMap.entrySet()) {
                Position position = Position.fromDatabaseName(playerEntry.getKey());
                String playerId = playerEntry.getValue();
                Player player = AnalyzerService.playersInTeam.get(playerId);
                if (player != null) {
                    playersInLine.put(position, player);
                }
            }

            // Get percent for found connections
            for (Connection connection : Connection.values()) {
                Pair<Position, Position> positions = Connection.getPositionsInConnection(connection);
                if (positions != null) {
                    Position position1 = positions.first;
                    Position position2 = positions.second;
                    Player player1 = playersInLine.get(position1);
                    Player player2 = playersInLine.get(position2);
                    if (player1 != null && player2 != null) {
                        double percent = ChemistryPercentAnalyzer.getConnectionPercent(player1, player2, connection);
                        chemistryMap.put(connection, (int) Math.round(percent));
                    }
                }
            }
        }

        return chemistryMap;
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get chemistry percents to closest players in line.
     * This is how percent is calculated:
     * 1. Get closest players for every line position.
     * 2. Get chemistry percents between one position and closest positions.
     * 3. Calculate average of those chemistry percents.
     *
     * @param playersMap which players are calculated
     * @return chemistry percents to closest players indexed by position
     */
    public Map<Position, Integer> getAverageChemistryPercentForPositions(Map<String, String> playersMap) {
        Map<Position, Integer> averagePercentForPositions = new HashMap<>();

        if(playersMap != null) {
            Map<Connection, Integer> chemistryConnectionPercents = getChemistryConnectionPercents(playersMap);
            for (Map.Entry<String, String> playerEntry : playersMap.entrySet()) {
                final Position position = Position.fromDatabaseName(playerEntry.getKey());
                ArrayList<Connection> connections = Connection.getClosestChemistryConnections(position);
                int connectionCount = connections.size();
                int percentSum = 0;
                for(Connection connection : connections) {
                    Integer percent = chemistryConnectionPercents.get(connection);
                    if(percent != null) {
                        percentSum += percent;
                    }
                }
                if(percentSum > 0) {
                    int averagePercent = percentSum / connectionCount;
                    averagePercentForPositions.put(position, averagePercent);
                } else {
                    averagePercentForPositions.put(position, 0);
                }
            }
        }
        return averagePercentForPositions;
    }

    private static class GetBestLinesTask extends AsyncTask<Void, Integer, Void> {
        AllowedPlayerPosition allowedPlayerPosition;
        BestLineType bestLineType;
        AnalyzeListener listener;
        private ProgressDialog dialog;

        GetBestLinesTask(Activity activity, AllowedPlayerPosition allowedPlayerPosition, BestLineType bestLineType, AnalyzeListener listener) {
            this.listener = listener;
            this.allowedPlayerPosition = allowedPlayerPosition;
            this.bestLineType = bestLineType;
            dialog = new ProgressDialog(activity);
            dialog.setCancelable(true);
            dialog.setIndeterminate(false);
            dialog.setMessage(AppRes.getContext().getString(R.string.lines_analyzing));
            // Progress dialog horizontal style
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get 6 best players for each position. These are compare players.
            // Compare those players in line every to every position
            // Collect and sort lines with player combinations
            // Choose by best line or best team chemistry

            // Get best players for each position. Can be empty if no marked players in selected position.
            int playersInOnePosition = 6;
            Map<Position, ArrayList<Player>> bestPlayersForPosition = ChemistryPercentAnalyzer.getBestPlayersForPosition(allowedPlayerPosition, playersInOnePosition);
            // FOR LOGGING
            /*ArrayList<String> uniquePlayers = new ArrayList<>();
            for(ArrayList<Player> players : bestPlayersForPosition.values()) {
                for(Player player : players) {
                    if(!uniquePlayers.contains(player.getPlayerId())) {
                        uniquePlayers.add(player.getPlayerId());
                    }
                }
            }
            Logger.log("UNIQUE PLAYERS: " + uniquePlayers.size());*/

            ArrayList<Player> LWs = bestPlayersForPosition.get(Position.LW);
            ArrayList<Player> Cs = bestPlayersForPosition.get(Position.C);
            ArrayList<Player> RWs = bestPlayersForPosition.get(Position.RW);
            ArrayList<Player> LDs = bestPlayersForPosition.get(Position.LD);
            ArrayList<Player> RDs = bestPlayersForPosition.get(Position.RD);
            if (LWs == null) LWs = new ArrayList<>();
            if (Cs == null) Cs = new ArrayList<>();
            if (RWs == null) RWs = new ArrayList<>();
            if (LDs == null) LDs = new ArrayList<>();
            if (RDs == null) RDs = new ArrayList<>();

            int combinationsCount = 1;
            for (Position position : Position.getPlayers()) {
                ArrayList<Player> playersInPosition = bestPlayersForPosition.get(position);
                if (playersInPosition != null) {
                    int currentSize = playersInPosition.size();
                    if (allowedPlayerPosition == AllowedPlayerPosition.BEST_POSITION && position != Position.LW) {
                        currentSize = currentSize - 1;
                    }
                    combinationsCount = combinationsCount * currentSize;
                }
            }
            int currentIndex = 0;

            ArrayList<Pair<Line, Integer>> foundLines = new ArrayList<>();
            for (Player LW : LWs) {
                for (Player C : Cs) {
                    ArrayList<String> playerIdsInLine = new ArrayList<>(Arrays.asList(LW.getPlayerId()));
                    if (playerIdsInLine.contains(C.getPlayerId())) continue;
                    for (Player RW : RWs) {
                        playerIdsInLine = new ArrayList<>(Arrays.asList(LW.getPlayerId(), C.getPlayerId()));
                        if (playerIdsInLine.contains(RW.getPlayerId())) continue;
                        for (Player LD : LDs) {
                            playerIdsInLine = new ArrayList<>(Arrays.asList(LW.getPlayerId(), C.getPlayerId(), RW.getPlayerId()));
                            if (playerIdsInLine.contains(LD.getPlayerId())) continue;
                            for (Player RD : RDs) {
                                playerIdsInLine = new ArrayList<>(Arrays.asList(LW.getPlayerId(), C.getPlayerId(), RW.getPlayerId(), LD.getPlayerId()));
                                if (playerIdsInLine.contains(RD.getPlayerId())) continue;

                                Map<String, String> playersInLine = new HashMap<>();
                                playersInLine.put(Position.LW.toDatabaseName(), LW.getPlayerId());
                                playersInLine.put(Position.C.toDatabaseName(), C.getPlayerId());
                                playersInLine.put(Position.RW.toDatabaseName(), RW.getPlayerId());
                                playersInLine.put(Position.LD.toDatabaseName(), LD.getPlayerId());
                                playersInLine.put(Position.RD.toDatabaseName(), RD.getPlayerId());

                                Line line = new Line();
                                line.setPlayerIdMap(playersInLine);
                                int lineChemistryPercent = getInstance().getLineChemistryPercent(line);
                                foundLines.add(new Pair<>(line, lineChemistryPercent));

                                // Publish porgress
                                int percent = (int) ((double) currentIndex / combinationsCount * 100);
                                //Logger.log(LW.getName().split(" ")[0] + " - " + C.getName().split(" ")[0] + " - " + RW.getName().split(" ")[0] + " - " + LD.getName().split(" ")[0] + " - " + RD.getName().split(" ")[0]);
                                publishProgress(percent);
                                currentIndex++;
                            }
                        }
                    }
                }
            }

            Collections.sort(foundLines, (o1, o2) -> Double.compare(o2.second, o1.second));

            // floor = round down to nearest integer
            int linesToCreate = (int) Math.floor(playersInTeam.size() / 5.0);
            if (linesToCreate > 4) {
                linesToCreate = 4;
            }

            Map<Integer, Line> lines = new HashMap<>();
            ArrayList<Pair<ArrayList<Line>, Integer>> lineCombinations = new ArrayList<>();

            // TASAISIMMAT KENTÄLLISET
            if (bestLineType == BestLineType.BEST_TEAM_CHEMISTRY) {
                // Hae haluttujen kentällisten verran comboja ja laske kemiasumma
                for (int startIndex = 0; startIndex < foundLines.size() - linesToCreate; startIndex++) {
                    ArrayList<Line> linesToCompare = new ArrayList<>();
                    int chemistrySumForLines = 0;

                    Pair<Line, Integer> linePair = foundLines.get(startIndex);
                    Line firstLine = linePair.first;
                    int firstPercent = linePair.second;
                    linesToCompare.add(firstLine);
                    chemistrySumForLines += firstPercent;

                    // Iterate over other lines to get lines to create
                    for (int j = startIndex + 1; j < foundLines.size(); j++) {
                        // Enough lines collected
                        if (linesToCompare.size() >= linesToCreate) {
                            break;
                        }

                        Pair<Line, Integer> compareLinePair = foundLines.get(j);
                        Line line = compareLinePair.first;
                        if (!lineContainsAlreadyAddedPlayer(line, linesToCompare)) {
                            int percent = compareLinePair.second;
                            chemistrySumForLines += percent;
                            linesToCompare.add(line);
                        }
                    }
                    lineCombinations.add(new Pair<>(linesToCompare, chemistrySumForLines));
                }
                // Sort by best combination
                Collections.sort(lineCombinations, (o1, o2) -> Integer.compare(o2.second, o1.second));
                // Now we have combinations sorted
                if (lineCombinations.size() > 0) {
                    Pair<ArrayList<Line>, Integer> lineCombination = lineCombinations.get(0);
                    ArrayList<Line> linesInSet = lineCombination.first;
                    int lineNumber = 1;
                    for (Line line : linesInSet) {
                        line.setLineNumber(lineNumber);
                        lines.put(lineNumber, line);
                        lineNumber++;
                    }
                }
            } else if (bestLineType == BestLineType.BEST_LINE_CHEMISTRY) {
                Collection<String> addedPlayerIds = new ArrayList<>();
                int lineNumber = 1;
                for (Pair<Line, Integer> linePair : foundLines) {
                    if (lineNumber > linesToCreate) {
                        break;
                    }

                    Line line = linePair.first;
                    Map<String, String> playerIdMap = line.getPlayerIdMap();
                    boolean playerAlreadyAdded = false;
                    for (String playerId : playerIdMap.values()) {
                        if (addedPlayerIds.contains(playerId)) {
                            playerAlreadyAdded = true;
                        }
                    }
                    if (!playerAlreadyAdded) {
                        line.setLineNumber(lineNumber);
                        lines.put(lineNumber, line);
                        lineNumber++;
                        addedPlayerIds.addAll(playerIdMap.values());
                    }
                }
            }

            listener.onAnalyzeCompleted(lines);
            return null;
        }

    }
}
