package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Chemistry.ChemistryConnection;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerService {

    /**
     * Database path: goalsTeamGame
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param goals goals saved for team
     * @return count of chemistry points
     */
    public static int getChemistryPoints(String playerId, String comparedPlayerId, ArrayList<Goal> goals) {

        int chemistryPoints = 0;

        // +3 = if playerId has scored and compareId assisted
        // +2 = if playerId has assisted and compareId scored
        // +1 = if compareId and playerId has been on the field when goal happened
        // -1 = if playerId and compareId has been on the field when goal happened and isOpponentGoal == true
        for(Goal goal : goals) {
            List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
            List<String> scorerAndAssistant = Arrays.asList(goal.getScorerId(), goal.getAssistantId());

            boolean bothPlayersOnField = goal.getPlayerIds() != null && goal.getPlayerIds().containsAll(comparePlayers);
            if(bothPlayersOnField) {
                if(goal.isOpponentGoal()) {
                    chemistryPoints--;
                } else if(!goal.isOpponentGoal() && scorerAndAssistant.containsAll(comparePlayers)) {
                    chemistryPoints += 3;
                } else if(!goal.isOpponentGoal() && (scorerAndAssistant.contains(playerId) || scorerAndAssistant.contains(comparedPlayerId))) {
                    chemistryPoints += 2;
                } else if(!goal.isOpponentGoal()) {
                    chemistryPoints++;
                }
            }
        }
        return chemistryPoints;
    }

    /**
     * Database path: statsPlayerGame
     * @param lookingBestScorer if true looks for best scorer for playerId assists else best assistant for playerId scores
     * @param playerId assistant playerId
     * @param goals goals saved for player
     * @return playerId of the best scorer
     */
    public static String getBestScorerOrAssistant(boolean lookingBestScorer, String playerId, ArrayList<Goal> goals) {

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
     * @param goals Team goals where chemistry is calculated
     * @return List of player chemistries
     */
    public static ArrayList<Chemistry> getPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {

        ArrayList<Chemistry> playerChemistryList = new ArrayList<>();

        for(Player player : players) {
            Chemistry chemistry = new Chemistry();
            chemistry.setPlayerId(player.getPlayerId());
            for(Player comparePlayer : players) {
                if(!comparePlayer.getPlayerId().equals(player.getPlayerId())) {
                    int chemistryPoints = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
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
    public static Map<Integer, Line> getBestPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {
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
                int chemistryPoints = getChemistryPoints(center.getPlayerId(), player.getPlayerId(), goals);
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
     * MAIN METHOD (called from UI)
     *
     * Get average percent for line. Average is calculated from closest positions.
     * @param line which players are calculated
     * @param goals all team goals
     * @param lines lines in games
     * @param players all players in team
     * @return chemistry percent for line
     */
    public static int getLineChemistryPercent(Line line, Map<String, ArrayList<Goal>> goals, Map<String, ArrayList<Line>> lines, ArrayList<Player> players) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = getChemistriesInLineForPositions(line, goals, lines, players);
        Map<Position, ArrayList<Chemistry>> filteredMap = getFilteredChemistryMapToClosestPlayers(chemistryMap);
        double percentSize = 0;
        double percentCount = 0;
        for (Map.Entry<Position, ArrayList<Chemistry>> entry : filteredMap.entrySet()) {
            ArrayList<Chemistry> chemistries = entry.getValue();
            percentSize += chemistries.size();
            percentCount += getAverageChemistryPercent(chemistries);
        }

        return (int)Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     * @param line which players are calculated
     * @param goals all team goals
     * @param lines lines in games
     * @param players all players in team
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public static Map<ChemistryConnection, Integer> getChemistryConnections(Line line, Map<String, ArrayList<Goal>> goals, Map<String, ArrayList<Line>> lines, ArrayList<Player> players) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = getChemistriesInLineForPositions(line, goals, lines, players);

        Map<ChemistryConnection, Integer> chemistryConnections = new HashMap<>();
        // Center
        Map<Position, Integer> compareChemistryMap = getConvertCompareChemistryPercentsForPosition(Position.C, chemistryMap);
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
            chemistryConnections.put(ChemistryConnection.C_RD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_RD, chemistry);
        }
        // Left defender
        compareChemistryMap = getConvertCompareChemistryPercentsForPosition(Position.LD, chemistryMap);
        chemistry = compareChemistryMap.get(Position.RD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_RD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RD);
        if(chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_LW, chemistry);
        }
        // Right defender
        compareChemistryMap = getConvertCompareChemistryPercentsForPosition(Position.RD, chemistryMap);
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
     * @param goals all team goals
     * @param lines lines in games
     * @param players all players in team
     * @return chemistry percents to closest players indexed by position
     */
    public static Map<Position, Integer> getClosestChemistries(Line line, Map<String, ArrayList<Goal>> goals, Map<String, ArrayList<Line>> lines, ArrayList<Player> players) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = getChemistriesInLineForPositions(line, goals, lines, players);

        // Map contains only closest players, no others
        Map<Position, ArrayList<Chemistry>> filteredMap = getFilteredChemistryMapToClosestPlayers(chemistryMap);

        Map<Position, Integer> closestChemistries = new HashMap<>();

        // Calculate average points
        for (Map.Entry<Position, ArrayList<Chemistry>> entry : filteredMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            int percent = getAverageChemistryPercent(chemistries);
            closestChemistries.put(position, percent);
        }

        return closestChemistries;
    }

    /**
     * NOT IN USE
     *
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param gameGoals indexed by gameId
     */
    public static double getChemistryPointsAvg(String playerId, String comparedPlayerId, Map<String, ArrayList<Goal>> gameGoals) {
        Map<String, Integer> chemistryMap = getChemistryPointsOfGames(playerId, comparedPlayerId, gameGoals);

        int size = chemistryMap.size();
        if(size == 0) {
            return 0.0;
        }

        double sum = 0.0;
        for (Map.Entry<String, Integer> entry : chemistryMap.entrySet()) {
            int points = entry.getValue();
            sum += points;
        }

        return sum / size;
    }

    /**
     * NOT IN USE
     *
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param gameGoals indexed by gameId
     */
    public static Map<String, Integer> getChemistryPointsOfGames(String playerId, String comparedPlayerId, Map<String, ArrayList<Goal>> gameGoals) {
        Map<String, Integer> chemistryMap = new HashMap<>();

        for (Map.Entry<String, ArrayList<Goal>> entry : gameGoals.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Goal> goals = entry.getValue();
            int chemistryPoints = getChemistryPoints(playerId, comparedPlayerId, goals);
            chemistryMap.put(gameId, chemistryPoints);
        }

        return chemistryMap;
    }

    /**
     * HELPER METHOD
     *
     * Get average chemistry percents of given chemistries
     * @param chemistries list to calculate
     * @return line chemistry percent
     */
    public static int getAverageChemistryPercent(ArrayList<Chemistry> chemistries) {
        double chemistryCount = 0;
        double chemistrySize = chemistries.size();
        for(Chemistry chemistry : chemistries) {
            chemistryCount += chemistry.getChemistryPercent();
        }

        return (int)Math.round(chemistryCount / chemistrySize);
    }

    /**
     * HELPER METHOD
     *
     * Get chemistries of players in line indexed by position.
     * Chemistries are calculated from one line's position to other positions.
     * @param line player chemistries from this line are calculated
     * @param goals all team goals
     * @param lines lines in games
     * @param players all players in team
     * @return chemistries list indexed by playerId
     */
    public static Map<Player.Position, ArrayList<Chemistry>> getChemistriesInLineForPositions(Line line, Map<String, ArrayList<Goal>> goals, Map<String, ArrayList<Line>> lines, ArrayList<Player> players) {
        Map<Player.Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

        if(line != null && line.getPlayerIdMap() != null) {
            Map<String, String> playersMap = line.getPlayerIdMap();
            for (Map.Entry<String, String> player : playersMap.entrySet()) {
                final String position = player.getKey();
                final String playerId = player.getValue();
                ArrayList<Chemistry> chemistries = new ArrayList<>();

                for (Map.Entry<String, String> comparePlayer : playersMap.entrySet()) {
                    Position comparedPosition = Position.fromDatabaseName(comparePlayer.getKey());
                    String comparedPlayerId = comparePlayer.getValue();

                    if(!playerId.equals(comparedPlayerId)) {
                        Chemistry chemistry = new Chemistry();
                        chemistry.setPlayerId(playerId);
                        chemistry.setComparePlayerId(comparedPlayerId);
                        chemistry.setComparePosition(comparedPosition);
                        ArrayList<Goal> filteredGoals = getGoalsWherePlayersInSameLine(playerId, comparedPlayerId, goals, lines);
                        int points = getChemistryPoints(playerId, comparedPlayerId, filteredGoals);
                        chemistry.setChemistryPoints(points);
                        int percent = getChemistryPercent(playerId, comparedPlayerId, players, goals, lines);
                        chemistry.setChemistryPercent(percent);

                        chemistries.add(chemistry);
                    }
                }

                chemistryMap.put(Player.Position.fromDatabaseName(position), chemistries);
            }
        }

        return chemistryMap;
    }

    /**
     * HELPER METHOD
     *
     * Get filtered map to players' closest positions
     * @param chemistryMap map indexed by position in line
     * @return filtered map to closest positions
     */
    public static Map<Position, ArrayList<Chemistry>> getFilteredChemistryMapToClosestPlayers(Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, ArrayList<Chemistry>> filteredMap = new HashMap<>();

        for (Map.Entry<Position, ArrayList<Chemistry>> entry : chemistryMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            List<Position> closestPositions = new ArrayList<>();
            if(position == Position.LW) {
                closestPositions = Arrays.asList(Position.C, Position.LD);
            } else if(position == Position.C) {
                closestPositions = Arrays.asList(Position.LW, Position.RW, Position.LD, Position.RD);
            } else if(position == Position.RW) {
                closestPositions = Arrays.asList(Position.C, Position.RD);
            } else if(position == Position.LD) {
                closestPositions = Arrays.asList(Position.C, Position.LW);
            } else if(position == Position.RD) {
                closestPositions = Arrays.asList(Position.C, Position.RW);
            }

            ArrayList<Chemistry> filteredChemistries = new ArrayList<>();
            for(Chemistry chemistry : chemistries) {
                if(closestPositions.contains(chemistry.getComparePosition())) {
                    filteredChemistries.add(chemistry);
                }
            }
            filteredMap.put(position, filteredChemistries);
        }

        return filteredMap;
    }

    /**
     * HELPER METHOD
     *
     * Get chemistry percents between given position and other map's positions
     * @param position position to use
     * @param chemistryMap map of all positions
     * @return chemistry points indexed by other positions
     */
    private static Map<Position, Integer> getConvertCompareChemistryPercentsForPosition(Position position, Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, Integer> chemistryPoints = new HashMap<>();
        ArrayList<Chemistry> chemistries = chemistryMap.get(position);
        if(chemistries != null) {
            for(Chemistry chemistry : chemistries) {
                Position comparePosition = chemistry.getComparePosition();
                chemistryPoints.put(comparePosition, chemistry.getChemistryPercent());
            }
        }

        return chemistryPoints;
    }

    /**
     * MEDIUM HELPER METHOD, USES SMALL HELPER METHODS
     *
     * Calculates chemistry as percent between two players.
     * This is how percent is calculated:
     * 1. Get all chemistry points from all players.
     * 2. Get minimum and maximum chemistries from those.
     * 3. Get chemistry between two compared players.
     * 4. Calculate percent where that chemistry takes place between min and max points.
     * Note: Percent is calculated from games where both compared players have been set in the same line.
     *
     * @param playerId player to compare against
     * @param comparePlayerId player to compare
     * @param players all players in team
     * @param teamGoalsMap all team goals
     * @param teamLinesMap lines in games
     * @return average chemistry points as percent (0-100)
     */
    public static int getChemistryPercent(String playerId, String comparePlayerId, ArrayList<Player> players, Map<String, ArrayList<Goal>> teamGoalsMap, Map<String, ArrayList<Line>> teamLinesMap) {
        int maxPoints = getMaxChemistryPoints(players, teamGoalsMap, teamLinesMap);
        int minPoints = getMinChemistryPoints(players, teamGoalsMap, teamLinesMap);
        ArrayList<Goal> goals = getGoalsWherePlayersInSameLine(playerId, comparePlayerId, teamGoalsMap, teamLinesMap);
        int points = getChemistryPoints(playerId, comparePlayerId, goals);

        return (int)Math.round((double)points / (maxPoints - minPoints) * 100);
    }

    /**
     * HELPER METHOD
     *
     * Get maximum chemistry points from all players of team
     * @param players all players in team
     * @param gameGoals goal lists indexed by gameId
     * @param linesMap lines indexed by gameId
     * @return maximum chemistry points
     */
    public static int getMaxChemistryPoints(ArrayList<Player> players, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        int maxPoints = 0;
        for(Player player : players) {
            for(Player comparePlayer : players) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId(), linesMap);
                    ArrayList<Goal> goals = getGoalsOfGames(gameIds, gameGoals);
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(points > maxPoints) {
                        maxPoints = points;
                    }
                }
            }
        }
        return maxPoints;
    }

    /**
     * HELPER METHOD
     *
     * Get minimum chemistry points from all players of team
     * @param players all players in team
     * @param gameGoals goal lists indexed by gameId
     * @param linesMap lines indexed by gameId
     * @return minimum chemistry points
     */
    public static int getMinChemistryPoints(ArrayList<Player> players, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        Integer minPoints = null;
        for(Player player : players) {
            for(Player comparePlayer : players) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<Goal> goals = getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId(), gameGoals, linesMap);
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(minPoints == null || points < minPoints) {
                        minPoints = points;
                    }
                }
            }
        }
        return minPoints == null ? 0 : minPoints; // Default min is zero
    }

    /**
     * HELPER METHOD
     *
     * Get goals where given players have been in the same line.
     * @param playerId player 1
     * @param comparePlayerId player 2
     * @param gameGoals goal lists indexed by gameId
     * @param linesMap lines indexed by gameId
     * @return list of goals
     */
    public static ArrayList<Goal> getGoalsWherePlayersInSameLine(String playerId, String comparePlayerId, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(playerId, comparePlayerId, linesMap);
        return getGoalsOfGames(gameIds, gameGoals);
    }

    /**
     * HELPER METHOD
     *
     * Get gameIds where given players are in the same line.
     * @param playerId player 1
     * @param comparedPlayerId player 2
     * @param linesMap lines indexed by gameId
     * @return gameIds list
     */
    public static ArrayList<String> getGameIdsWherePlayersInSameLine(String playerId, String comparedPlayerId, Map<String, ArrayList<Line>> linesMap) {
        List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
        ArrayList<String> gameIds = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Line>> entry : linesMap.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Line> lines = entry.getValue();
            for(Line line : lines) {
                if(line.getPlayerIdMap() != null && line.getPlayerIdMap().values().containsAll(comparePlayers)) {
                    gameIds.add(gameId);
                }
            }
        }
        return gameIds;
    }

    /**
     * HELPER METHOD
     *
     * Get goals from given games by gameIds.
     * @param gameIds games
     * @param gameGoals goal lists indexed by gameId
     * @return goals from requested games
     */
    public static ArrayList<Goal> getGoalsOfGames(ArrayList<String> gameIds, Map<String, ArrayList<Goal>> gameGoals) {
        ArrayList<Goal> goals = new ArrayList<>();
        for(String gameId : gameIds) {
            ArrayList<Goal> foundGoals = gameGoals.get(gameId);
            if(foundGoals != null) {
                goals.addAll(foundGoals);
            }
        }
        return goals;
    }
}
