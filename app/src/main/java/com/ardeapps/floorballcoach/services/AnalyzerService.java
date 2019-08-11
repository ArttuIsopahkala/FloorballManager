package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Chemistry.ChemistryConnection;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    // These must be se before calling methods. GoalsInGames is not loaded automatically.
    // See LinesFragment and analyze chemistry button.
    protected static Map<String, ArrayList<Goal>> goalsInGames = new HashMap<>();
    protected static Map<String, ArrayList<Line>> linesInGames = new HashMap<>();
    protected static ArrayList<Player> playersInTeam = new ArrayList<>();

    private static AnalyzerService instance;
    private static boolean isJsonDatabase = false;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if(!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getActivePlayers();
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
    public static void setPlayersInTeam(ArrayList<Player> players) {
        playersInTeam = players;
        isJsonDatabase = true;
    }

    /**
     * Database path: statsPlayerGame
     * @param lookingBestScorer if true looks for best scorer for playerId assists else best assistant for playerId scores
     * @param playerId assistant playerId
     * @param goals goals saved for player
     * @return playerId of the best scorer
     */
    public String getBestScorerOrAssistant(boolean lookingBestScorer, String playerId, ArrayList<Goal> goals) {

        HashMap<String, Integer> players = new HashMap<>();
        String newPlayerId;

        for (Goal goal : goals) {
            if(lookingBestScorer && playerId.equals(goal.getAssistantId())) {
                newPlayerId = goal.getScorerId();
            } else if(!lookingBestScorer && playerId.equals(goal.getScorerId())) {
                newPlayerId = goal.getAssistantId();
            } else {
                newPlayerId = null;
            }

            if(newPlayerId != null) {
                Integer playerScores = players.get(newPlayerId);
                if(playerScores != null) {
                    playerScores++;
                    players.put(newPlayerId, playerScores);
                } else {
                    players.put(newPlayerId, 1);
                }
            }
        }

        Map.Entry<String, Integer> highestEntry = null;

        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer value = entry.getValue();

            if(highestEntry == null || highestEntry.getValue() < value) {
                highestEntry = entry;
            }
        }

        if(highestEntry == null) {
            return null;
        }

        return highestEntry.getKey();
    }

    /**
     * Database path: statsPlayerGame
     * @param players List of Players which chemistries are calculated
     * @param goalss Team goals where chemistry is calculated
     * @return List of player chemistries
     */
    public ArrayList<Chemistry> getPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goalss) {

        ArrayList<Chemistry> playerChemistryList = new ArrayList<>();

        for(Player player : players) {
            Chemistry chemistry = new Chemistry();
            chemistry.setPlayerId(player.getPlayerId());
            for(Player comparePlayer : players) {
                if(!comparePlayer.getPlayerId().equals(player.getPlayerId())) {
                    ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId());
                    int chemistryPoints = ChemistryHelper.getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    chemistry.setChemistryPoints(chemistryPoints);
                    chemistry.setComparePlayerId(comparePlayer.getPlayerId());
                    chemistry.setComparePosition(Position.fromDatabaseName(comparePlayer.getPosition()));
                }
            }

            playerChemistryList.add(chemistry);
        }

        return playerChemistryList;
    }

    /**
     * Database path: goalsTeamGame
     * @param players All players in a team
     * @param goals team goals where chemistry is calculated
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     *
     * This method should get List of Players and return Lines where are separated players in the way that
     * best chemistries are taken in notice between players and their positions
     *
     * Something like this:
     *     bestChemistryPointsCalculation(List<Players> players, List<Goals> goals) {
     *
     *     List<Players> centers = players.getCenters();
     *
     *     List<PlayerChemistries> playerChemistries = getChemistries() (chemistries between every player)
     *
     *     take every center one by one, get 4 highest player chemistryPoints (notice correct positions (LW, RW, LD, RD))
     *     Calculate best average chemistry for a Line (all positions) from the previous calculation
     *
     *     List<Lines> bestLines = create best lines (1. line center and players with average best chemistries between every player
     *                                                2. second best etc)
     *
     *     Can be appended in the future to make best lines for defending (weight to defenders chemistry), goalmaking (weight to forwards chemsitry) etc
     *
     */
    public Map<Integer, Line> getBestPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {
        // TODO tee loppuun ja testaa
        // playerIdMap:
        // key = position(Player.Position), value = playerId
        ArrayList<Player> centers = new ArrayList<>();
        Map<String, ArrayList<Chemistry>> centerPlayerChemistriesMap = new HashMap<>();
        ArrayList<Chemistry> centerPlayerChemistryList = new ArrayList<>();
        ArrayList<Player> listOfPlayers = new ArrayList<>();
        Map<Integer, Line> listOfBestLines = new HashMap<>();

        ArrayList<Chemistry> playerChemistries = getPlayerChemistries(players, goals);

        // Set center to own ArrayList and rest of the players to another
        for (Player player : players) {
            Position position = Position.fromDatabaseName(player.getPosition());
            if(position == Position.C) {
                centers.add(player);
            } else {
                listOfPlayers.add(player);
            }
        }

        // Create Map which contains centers and chemistries between every center individually and every player in other positions
        for (Player center : centers) {
            for (Player player : listOfPlayers) {
                int chemistryPoints = ChemistryHelper.getChemistryPoints(center.getPlayerId(), player.getPlayerId(), goals);
                Chemistry newChemistry = new Chemistry();
                newChemistry.setPlayerId(center.getPlayerId());
                newChemistry.setComparePlayerId(player.getPlayerId());
                newChemistry.setComparePosition(Position.fromDatabaseName(player.getPosition()));
                newChemistry.setChemistryPoints(chemistryPoints);
                centerPlayerChemistryList.add(newChemistry);
            }

            centerPlayerChemistriesMap.put(center.getPlayerId(), centerPlayerChemistryList);
        }

        // TODO Continue from here probably save centerId + centerBestOverallChemistry to Map<String, Integer> or something and get best center from that
        Map<String, Integer> centersWithHighestChemistries = new HashMap<>();
        for (Player center : centers) {

            ArrayList<Chemistry> chemistries = centerPlayerChemistriesMap.get(center.getPlayerId());

            int centerBestOverallChemistry;

            int bestChemistryPointsLw = 0;
            int bestChemistryPointsRw = 0;
            int bestChemistryPointsLd = 0;
            int bestChemistryPointsRd = 0;

            for (Chemistry chemistry : chemistries) {

                Position position = chemistry.getComparePosition();
                int playerChemistryPoints = chemistry.getChemistryPoints();

                if(position.equals(Position.LW)) {
                    if(bestChemistryPointsLw < playerChemistryPoints) {
                        bestChemistryPointsLw = playerChemistryPoints;
                    }
                } else if(position == Position.RW) {
                    if(bestChemistryPointsRw < playerChemistryPoints) {
                        bestChemistryPointsRw = playerChemistryPoints;
                    }
                } else if(position == Position.LD) {
                    if(bestChemistryPointsLd < playerChemistryPoints) {
                        bestChemistryPointsLd = playerChemistryPoints;
                    }
                } else if(position == Position.RD) {
                    if(bestChemistryPointsRd < playerChemistryPoints) {
                        bestChemistryPointsRd = playerChemistryPoints;
                    }
                }
            }

            centerBestOverallChemistry = bestChemistryPointsLw + bestChemistryPointsRw + bestChemistryPointsLd + bestChemistryPointsRd;

            centersWithHighestChemistries.put(center.getPlayerId(), centerBestOverallChemistry);
        }

            // This shit is here just to be reminder and example
            /*
            Map<String, String> playerIdMap = new HashMap<>();
            playerIdMap.put(bestLeftWing.getPlayerId(), bestLeftWing.getPosition());
            playerIdMap.put(bestRightWing.getPlayerId(), bestRightWing.getPosition());
            playerIdMap.put(bestLeftDefender.getPlayerId(), bestLeftDefender.getPosition());
            playerIdMap.put(bestRightDefender.getPlayerId(), bestRightDefender.getPosition());

            Line newFirstLine = new Line();
            newFirstLine.setLineId("asd");
            newFirstLine.setLineNumber(1);
            newFirstLine.setPlayerIdMap(playerIdMap);

            listOfBestLines.put(1, newFirstLine);
            */

        return listOfBestLines;
    }

    /**
     * Database path: goalsTeamGame
     *
     * Get best lines using goals from 3 last games
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     *
     */
    public Map<Integer, Line> getHotLines() {
        Map<Integer, Line> listOfBestLines = new HashMap<>();
        return listOfBestLines;
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get average percent for line. Average is calculated from closest positions.
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercent(Line line) {
        Map<ChemistryConnection, Integer> chemistryConnections = getChemistryConnections(line);
        double percentSize = chemistryConnections.size();
        if(percentSize == 0) {
            return 0;
        }
        double percentCount = 0;
        for (Map.Entry<ChemistryConnection, Integer> entry : chemistryConnections.entrySet()) {
            Integer percent = entry.getValue();
            percentCount += percent == null ? 0 : percent;
        }

        return (int)Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get average percent for team. Percent is average of line chemistry percentages.
     * @param lines team lines
     * @return chemistry percent for team
     */
    public int getTeamChemistryPercent(Map<Integer, Line> lines) {
        double percentSize = lines.size();
        if(percentSize == 0) {
            return 0;
        }
        double percentCount = 0;
        for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
            Line line = entry.getValue();
            percentCount += getLineChemistryPercent(line);
        }
        return (int)Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     * @param line which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<ChemistryConnection, Integer> getChemistryConnections(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = ChemistryHelper.getChemistriesInLineForPositions(line);

        Map<ChemistryConnection, Integer> chemistryConnections = new HashMap<>();
        // Center
        Map<Position, Integer> compareChemistryMap = ChemistryHelper.getConvertCompareChemistryPercentsForPosition(Position.C, chemistryMap);
        Integer chemistry = compareChemistryMap.get(Position.LW);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_LW, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RW);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_RW, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.LD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_LD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_RD, chemistry);
        }
        // Left defender
        compareChemistryMap = ChemistryHelper.getConvertCompareChemistryPercentsForPosition(Position.LD, chemistryMap);
        chemistry = compareChemistryMap.get(Position.RD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_RD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.LW);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_LW, chemistry);
        }
        // Right defender
        compareChemistryMap = ChemistryHelper.getConvertCompareChemistryPercentsForPosition(Position.RD, chemistryMap);
        chemistry = compareChemistryMap.get(Position.RW);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.RD_RW, chemistry);
        }

        return chemistryConnections;
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
     * @param line which players are calculated
     * @return chemistry percents to closest players indexed by position
     */
    public Map<Position, Integer> getClosestChemistryPercentsForPosition(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = ChemistryHelper.getChemistriesInLineForPositions(line);

        // Map contains only closest players, no others
        Map<Position, ArrayList<Chemistry>> filteredMap = ChemistryHelper.getFilteredChemistryMapToClosestPlayers(chemistryMap);

        Map<Position, Integer> closestChemistries = new HashMap<>();

        // Calculate average points
        for (Map.Entry<Position, ArrayList<Chemistry>> entry : filteredMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            int percent = ChemistryHelper.getAverageChemistryPercent(chemistries);
            closestChemistries.put(position, percent);
        }

        return closestChemistries;
    }

}
