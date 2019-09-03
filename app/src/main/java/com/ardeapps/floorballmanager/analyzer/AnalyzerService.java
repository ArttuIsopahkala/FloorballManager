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
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
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
    // Käytä aina PLAYERS_OWN_POSITION paitsi parhaiden kenttien haussa
    private static AllowedPlayerPosition currentAllowedPlayerPosition;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if (!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getActivePlayersMap();
        }
        if(chemistryPercentAnalyzerInitialized) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
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

    private static class GetBestLinesTask extends AsyncTask <Void, Integer, Void> {
        private ProgressDialog dialog;
        BestLineType bestLineType;
        AnalyzeListener listener;

        public GetBestLinesTask(Activity activity, BestLineType bestLineType, AnalyzeListener listener) {
            this.listener = listener;
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

        protected void onProgressUpdate(Integer... progress){
            dialog.setProgress(progress[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Position> positions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW, Position.LD, Position.RD));
            // Collect player by position to every place
            Map<Position, ArrayList<Player>> playersInPositions = new HashMap<>();
            for(Position position : positions) {
                playersInPositions.put(position, ChemistryPercentAnalyzer.getAllowedComparePlayers(position));
            }

            ArrayList<Pair<Position, ArrayList<Player>>> playerLists = new ArrayList<>();
            for(Position position : positions) {
                ArrayList<Player> playersInPosition = playersInPositions.get(position);
                if(playersInPosition != null) {
                    playerLists.add(new Pair<>(position, playersInPosition));
                }
            }
            // Sort by longest position list
            Collections.sort(playerLists, (o1, o2) -> Integer.compare(o2.second.size(), o1.second.size()));

            int combinationsCount = 0;
            for(int i = 0; i < playerLists.size(); i++) {
                int currentSize = playerLists.get(i).second.size();
                if(i == 0) {
                    combinationsCount = currentSize;
                } else {
                    combinationsCount = combinationsCount * currentSize;
                }
            }
            Logger.log("combinationsCount " + combinationsCount);
            int currentIndex = 0;
            ArrayList<Pair<Map<String, String>, Integer>> playersInLines = new ArrayList<>();
            // Loop all possible line combinations
            int positionsListsFound = playerLists.size();
            if(positionsListsFound > 0) {
                Pair<Position, ArrayList<Player>> list1 = playerLists.get(0);
                Position position1 = list1.first;
                for (Player player1 : list1.second) {
                    if (playerLists.size() > 1) {
                        Pair<Position, ArrayList<Player>> list2 = playerLists.get(1);
                        Position position2 = list2.first;
                        for (Player player2 : list2.second) {
                            if (playerLists.size() > 2) {
                                Pair<Position, ArrayList<Player>> list3 = playerLists.get(2);
                                Position position3 = list3.first;
                                for (Player player3 : list3.second) {
                                    if (playerLists.size() > 3) {
                                        Pair<Position, ArrayList<Player>> list4 = playerLists.get(3);
                                        Position position4 = list4.first;
                                        for (Player player4 : list4.second) {
                                            if (playerLists.size() > 4) {
                                                Pair<Position, ArrayList<Player>> list5 = playerLists.get(4);
                                                Position position5 = list5.first;
                                                for (Player player5 : list5.second) {
                                                    Map<String, String> playersInLine = new HashMap<>();
                                                    playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                                    playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                                    playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                                    playersInLine.put(position4.toDatabaseName(), player4.getPlayerId());
                                                    playersInLine.put(position5.toDatabaseName(), player5.getPlayerId());
                                                    AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                                    int percent = (int)((double)currentIndex / (double)combinationsCount * 100);
                                                    publishProgress(percent);
                                                    currentIndex++;
                                                }
                                            } else {
                                                Map<String, String> playersInLine = new HashMap<>();
                                                playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                                playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                                playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                                playersInLine.put(position4.toDatabaseName(), player4.getPlayerId());
                                                AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                                int percent = (int)((double)currentIndex / (double)combinationsCount * 100);
                                                publishProgress(percent);
                                                currentIndex++;
                                            }
                                        }
                                    } else {
                                        Map<String, String> playersInLine = new HashMap<>();
                                        playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                        playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                        playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                        AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                        int percent = (int)((double)currentIndex / (double)combinationsCount * 100);
                                        publishProgress(percent);
                                        currentIndex++;
                                    }
                                }
                            } else {
                                Map<String, String> playersInLine = new HashMap<>();
                                playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                int percent = (int)((double)currentIndex / (double)combinationsCount * 100);
                                publishProgress(percent);
                                currentIndex++;
                            }
                        }
                    } else {
                        Map<String, String> playersInLine = new HashMap<>();
                        playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                        AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                        int percent = (int)((double)currentIndex / (double)combinationsCount * 100);
                        publishProgress(percent);
                        currentIndex++;
                    }
                }
            }

            // TODO Täytä puuttuvat pelipaikat?

            // LOGGING
            int index = 1;
            Collections.sort(playersInLines, (o1, o2) -> Double.compare(o2.second, o1.second));
            for (Pair<Map<String, String>, Integer> lineCombination : playersInLines) {
                Map<String, String> playersMap = lineCombination.first;
                Integer chemistryPercent = lineCombination.second;
                Logger.log(index + " KENTTÄ " + playersMap + ": " + chemistryPercent);
                index++;
            }

            // floor = round down to nearest integer
            int linesToCreate = (int)Math.floor(playersInTeam.size() / 5.0);
            if(linesToCreate > 4) {
                linesToCreate = 4;
            }

            Map<Integer, Line> lines = new HashMap<>();
            ArrayList<Map<String, String>> playersInBestLines = new ArrayList<>();

            // TASAISIMMAT KENTÄLLISET
            if(bestLineType == BestLineType.BEST_TEAM_CHEMISTRY) {
                // Hae haluttujen kentällisten verran comboja ja laske kemiasumma
                ArrayList<Pair<ArrayList<Map<String, String>>, Integer>> bestSortedLineCombinations = new ArrayList<>();
                if (linesToCreate > 0) {
                    for (Pair<Map<String, String>, Integer> lineCombination1 : playersInLines) {
                        if (linesToCreate > 1) {
                            for (Pair<Map<String, String>, Integer> lineCombination2 : playersInLines) {
                                if (linesToCreate > 2) {
                                    for (Pair<Map<String, String>, Integer> lineCombination3 : playersInLines) {
                                        if (linesToCreate > 3) {
                                            for (Pair<Map<String, String>, Integer> lineCombination4 : playersInLines) {
                                                ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                                lineCombinations.add(lineCombination1);
                                                lineCombinations.add(lineCombination2);
                                                lineCombinations.add(lineCombination3);
                                                lineCombinations.add(lineCombination4);
                                                AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                            }
                                        } else {
                                            ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                            lineCombinations.add(lineCombination1);
                                            lineCombinations.add(lineCombination2);
                                            lineCombinations.add(lineCombination3);
                                            AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                        }
                                    }
                                } else {
                                    ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                    lineCombinations.add(lineCombination1);
                                    lineCombinations.add(lineCombination2);
                                    AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                }
                            }
                        } else {
                            ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                            lineCombinations.add(lineCombination1);
                            AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                        }
                    }
                }

                // Sort by chemistry sum
                Collections.sort(bestSortedLineCombinations, (o1, o2) -> Integer.compare(o2.second, o1.second));
                for (Pair<ArrayList<Map<String, String>>, Integer> lineCombination : bestSortedLineCombinations) {
                    ArrayList<Map<String, String>> playerInLines = lineCombination.first;
                    Integer chemistrySumInLines = lineCombination.second;
                    Logger.log("KENTTÄ SUM " + chemistrySumInLines);
                    for (Map<String, String> playersInLine : playerInLines) {
                        //Logger.log("KENTTÄ PELAAJAT " + playersInLine);
                    }
                }

                if (bestSortedLineCombinations.size() > 0) {
                    Pair<ArrayList<Map<String, String>>, Integer> bestLines = bestSortedLineCombinations.get(0);
                    playersInBestLines = bestLines.first;
                }
            } else if(bestLineType == BestLineType.BEST_LINE_CHEMISTRY) {
                // Collect lines
                ArrayList<Map<String, String>> bestLinesAdded = new ArrayList<>();
                ArrayList<String> addedPlayerIds = new ArrayList<>();
                for (Pair<Map<String, String>, Integer> lineCombination : playersInLines) {
                    Map<String, String> playersMap = lineCombination.first;
                    boolean lineContainsAddedPlayer = false;
                    for (String playerId : playersMap.values()) {
                        if (addedPlayerIds.contains(playerId)) {
                            lineContainsAddedPlayer = true;
                        }
                    }
                    if (!lineContainsAddedPlayer) {
                        bestLinesAdded.add(playersMap);
                        addedPlayerIds.addAll(playersMap.values());
                    }
                }
                playersInBestLines = bestLinesAdded;
            }

            // Finally create lines
            int lineNumber = 1;
            for (Map<String, String> playersInLine : playersInBestLines) {
                Line line = new Line();
                line.setLineNumber(lineNumber);
                line.setPlayerIdMap(playersInLine);
                lines.put(lineNumber, line);
                lineNumber++;
            }
            listener.onAnalyzeCompleted(lines);
            return null;
        }

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
        // TODO use gameCount
        currentAllowedPlayerPosition = allowedPlayerPosition;
        ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);

        GetBestLinesTask task = new GetBestLinesTask(AppRes.getActivity(), bestLineType, listener::onGetBestLines);
        task.execute();
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
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
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
     * Get average percent for line. Average is calculated from closest positions.
     *
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercent(Line line) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
        int percent = 0;
        if(line != null && line.getPlayerIdMap() != null) {
            Map<Position, Player> playersInLine = new HashMap<>();
            for (Map.Entry<String, String> entry : line.getPlayerIdMap().entrySet()) {
                Position position = Position.fromDatabaseName(entry.getKey());
                Player player = playersInTeam.get(entry.getValue());
                if(player != null) {
                    playersInLine.put(position, player);
                }
            }
            if(playersInLine.size() > 0) {
                int playerCount = playersInLine.size();
                double percentSum = 0;
                for (Map.Entry<Position, Player> entry : playersInLine.entrySet()) {
                    Position position = entry.getKey();
                    Player player = entry.getValue();
                    percentSum += AnalyzerWrapper.getPlayerChemistryPercent(position, player);
                }
                percent = (int) Math.round(percentSum / playerCount);
            }
        }
        return percent;
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
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
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

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     *
     * @param playersMap which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<Connection, Integer> getChemistryConnectionPercents(Map<String, String> playersMap) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
        Map<Connection, Integer> chemistryMap = new HashMap<>();
        if(playersMap != null) {
            for (Map.Entry<String, String> playerEntry : playersMap.entrySet()) {
                final Position position = Position.fromDatabaseName(playerEntry.getKey());
                String playerId = playerEntry.getValue();
                Player player = AnalyzerService.playersInTeam.get(playerId);
                if (player != null) {
                    for (Map.Entry<String, String> comparePlayerEntry : playersMap.entrySet()) {
                        Position comparePosition = Position.fromDatabaseName(comparePlayerEntry.getKey());
                        String comparePlayerId = comparePlayerEntry.getValue();
                        Player comparePlayer = AnalyzerService.playersInTeam.get(comparePlayerId);
                        if (comparePlayer != null) {
                            if (!playerId.equals(comparePlayerId)) {
                                Connection connection = Connection.getChemistryConnection(position, comparePosition);
                                if (connection != null) {
                                    double chemistryPercent = ChemistryPercentAnalyzer.getConnectionChemistryPercentOneToOne(position, player, comparePosition, comparePlayer);
                                    Logger.log("percent " + chemistryPercent);
                                    chemistryMap.put(connection, (int) Math.round(chemistryPercent));
                                }
                            }
                        }
                    }
                }
            }
        }
        return chemistryMap;
    }
}
