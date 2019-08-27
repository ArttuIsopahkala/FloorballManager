package com.ardeapps.floorballmanager.analyzer;

import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AnalyzerDataCollector extends AnalyzerService {

    /**
     * Get game count where given players are in the same line.
     *
     * @param playerId         player 1
     * @param comparedPlayerId player 2
     * @return gameIds list
     */
    public static int getGameCountWherePlayersInSameLine(String playerId, String comparedPlayerId) {
        int gameCount = 0;
        List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
        for (ArrayList<Line> lines : linesInGames.values()) {
            for (Line line : lines) {
                if (line.getPlayerIdMap() != null && line.getPlayerIdMap().values().containsAll(comparePlayers)) {
                    gameCount++;
                }
            }
        }
        return gameCount;
    }

    /**
     * Get goals where both players have been on field.
     *
     * @param playerId1 player1
     * @param playerId2 player2
     * @return goals
     */
    public static ArrayList<Goal> getCommonGoals(String playerId1, String playerId2) {
        ArrayList<Goal> allGoals = new ArrayList<>();
        for(ArrayList<Goal> goalsInGame : goalsInGames.values()) {
            allGoals.addAll(goalsInGame);
        }
        ArrayList<Goal> commonGoals = new ArrayList<>();
        for (Goal goal : allGoals) {
            List<String> comparePlayers = Arrays.asList(playerId1, playerId2);
            boolean bothPlayersOnField = goal.getPlayerIds() != null && goal.getPlayerIds().containsAll(comparePlayers);
            if (bothPlayersOnField) {
                commonGoals.add(goal);
            }
        }
        return commonGoals;
    }

    public static Position getBestPositionFromGoals(Player player) {
        double maxGoalPercent = 0;
        Position bestPosition = null;
        Map<Position, ArrayList<String>> gameIdsInPositions = getGamesByPositionWherePlayerInLine(player);
        for (Map.Entry<Position, ArrayList<String>> entry : gameIdsInPositions.entrySet()) {
            Position position = entry.getKey();
            ArrayList<String> gameIds = entry.getValue();
            ArrayList<Goal> goals = getGoalsOfGames(gameIds);
            int goalPoints = ChemistryPointsAnalyzer.getGoalPointsForPlayer(player.getPlayerId(), goals);
            int goalPercent = 0;
            if(goalPoints > 0) {
                goalPercent = goalPoints / gameIds.size();
            }
            if(goalPoints > maxGoalPercent) {
                bestPosition = position;
                maxGoalPercent = goalPercent;
            }
        }
        if(bestPosition == null) {
            bestPosition = Position.fromDatabaseName(player.getPosition());
        }
        return bestPosition;
    }

    /**
     * Get goals from given games by gameIds.
     *
     * @param gameIds games
     * @return goals from requested games
     */
    private static ArrayList<Goal> getGoalsOfGames(ArrayList<String> gameIds) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        for (String gameId : gameIds) {
            ArrayList<Goal> foundGoals = goalsInGames.get(gameId);
            if (foundGoals != null) {
                goalsList.addAll(foundGoals);
            }
        }
        return goalsList;
    }

    private static Map<Position, ArrayList<String>> getGamesByPositionWherePlayerInLine(Player player) {
        Map<Position, ArrayList<String>> gamesByPosition = new HashMap<>();
        for (Map.Entry<String, ArrayList<Line>> entry : linesInGames.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Line> lines = entry.getValue();
            for (Line line : lines) {
                Map<String, String> playerIdMap = line.getPlayerIdMap();
                if (playerIdMap != null) {
                    for (Map.Entry<String, String> playerEntry : playerIdMap.entrySet()) {
                        Position position = Position.fromDatabaseName(playerEntry.getKey());
                        String playerId = playerEntry.getValue();
                        if(player.getPlayerId().equals(playerId)) {
                            ArrayList<String> gameIds = gamesByPosition.get(position);
                            if(gameIds == null) {
                                gameIds = new ArrayList<>();
                            }
                            gameIds.add(gameId);
                            gamesByPosition.put(position, gameIds);
                        }
                    }
                }
            }
        }
        return gamesByPosition;
    }
}
