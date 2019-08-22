package com.ardeapps.floorballmanager.services;

import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnalyzerHelper extends AnalyzerService {

    /**
     * Get goals where given players have been in the same line.
     *
     * @param playerId        player 1
     * @param comparePlayerId player 2
     * @return list of goals
     */
    public static ArrayList<Goal> getGoalsWherePlayersInSameLine(String playerId, String comparePlayerId) {
        ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(playerId, comparePlayerId);
        return getGoalsOfGames(gameIds);
    }

    /**
     * Get gameIds where given players are in the same line.
     *
     * @param playerId         player 1
     * @param comparedPlayerId player 2
     * @return gameIds list
     */
    public static ArrayList<String> getGameIdsWherePlayersInSameLine(String playerId, String comparedPlayerId) {
        List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
        ArrayList<String> gameIds = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Line>> entry : linesInGames.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Line> lines = entry.getValue();
            for (Line line : lines) {
                if (line.getPlayerIdMap() != null && line.getPlayerIdMap().values().containsAll(comparePlayers)) {
                    gameIds.add(gameId);
                }
            }
        }
        return gameIds;
    }

    /**
     * Get goals where given player have been in the line.
     *
     * @param playerId player 1
     * @return list of goals
     */
    public static ArrayList<Goal> getGoalsWherePlayerInLine(String playerId) {
        ArrayList<String> gameIds = getGameIdsWherePlayerInLine(playerId);
        return getGoalsOfGames(gameIds);
    }

    /**
     * Get gameIds where given player are in the line.
     *
     * @param playerId player 1
     * @return gameIds list
     */
    public static ArrayList<String> getGameIdsWherePlayerInLine(String playerId) {
        ArrayList<String> gameIds = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Line>> entry : linesInGames.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Line> lines = entry.getValue();
            for (Line line : lines) {
                if (line.getPlayerIdMap() != null && line.getPlayerIdMap().values().contains(playerId)) {
                    gameIds.add(gameId);
                }
            }
        }
        return gameIds;
    }

    /**
     * Get goals from given games by gameIds.
     *
     * @param gameIds games
     * @return goals from requested games
     */
    public static ArrayList<Goal> getGoalsOfGames(ArrayList<String> gameIds) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        for (String gameId : gameIds) {
            ArrayList<Goal> foundGoals = goalsInGames.get(gameId);
            if (foundGoals != null) {
                goalsList.addAll(foundGoals);
            }
        }
        return goalsList;
    }
}
