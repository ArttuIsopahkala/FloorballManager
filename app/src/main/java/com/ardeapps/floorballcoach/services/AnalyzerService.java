package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.PlayerChemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    /**
     * Database path: goalsTeamGame
     * @param playerId player to compare against
     * @param compareId player to compare
     * @param goals goals saved for team
     * @return count of chemistry points
     */
    public static int getChemistryPoints(String playerId, String compareId, ArrayList<Goal> goals) {

        int chemistryPoints = 0;

        // +1 = if compareId and playerId has been on the field when goal happened
        // +2 = if playerId has assisted and compareId scored
        // +3 = if playerId has scored and compareId assisted
        for(Goal goal : goals) {
            if(goal.isOpponentGoal() && goal.getPlayerIds() != null && goal.getPlayerIds().contains(playerId) && goal.getPlayerIds().contains(compareId)) {
                chemistryPoints--;
            } else if(!goal.isOpponentGoal() && playerId.equals(goal.getScorerId()) && compareId.equals(goal.getAssistantId())) {
                chemistryPoints += 3;
            } else if(!goal.isOpponentGoal() && compareId.equals(goal.getScorerId()) && playerId.equals(goal.getAssistantId())) {
                chemistryPoints += 2;
            } else if(!goal.isOpponentGoal() && goal.getPlayerIds() != null && goal.getPlayerIds().contains(playerId) && goal.getPlayerIds().contains(compareId)) {
                chemistryPoints++;
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
     * @param playerIdMap players which chemistries are calculated
     * @param goals team goals where chemistry is calculated
     * @return list of player chemistries
     */
    public static ArrayList<PlayerChemistry> getPlayerChemistries(Map<String, String> playerIdMap, ArrayList<Goal> goals) {
        // TODO tee loppuun ja testaa
        // playerIdMap:
        // key = position(Player.Position), value = playerId
        ArrayList<String> playerIds = new ArrayList<>(playerIdMap.values());
        for(String playerId : playerIds) {
            PlayerChemistry playerChemistry = new PlayerChemistry();
            playerChemistry.setPlayerId(playerId);
            for(String comparePlayerId : playerIds) {
                int testChemistry = AnalyzerService.getChemistryPoints(playerId, comparePlayerId, goals);
                playerChemistry.getComparePlayers().put(comparePlayerId, testChemistry);
            }
        }
        return null;
    }

    /**
     * Database path: goalsTeamGame
     * @param playerIdMap players which chemistries are calculated
     * @param goals team goals where chemistry is calculated
     * @return list of lines playerIdMap is separated
     */
    public static Map<Integer, Line> getBestPlayerChemistries(Map<String, String> playerIdMap, ArrayList<Goal> goals) {
        // TODO tee loppuun ja testaa
        // playerIdMap:
        // key = position(Player.Position), value = playerId
        Map<Integer, Line> lines = new HashMap<>();

        return null;
    }
}
