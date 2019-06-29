package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    // TEAM STATS
    // goalsTeamGame
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

    // PLAYER STATS
    // statsPlayerGame
    public static String getBestAssistantForScorer(String playerId, ArrayList<Goal> goals) {

        HashMap<String, Integer> assistPlayers = new HashMap<String, Integer>();

        for(Goal goal : goals) {
            if(playerId.equals(goal.getScorerId())) {
                String assistantId = goal.getAssistantId();
                if(assistantId != null) {
                    Integer playerAssists = assistPlayers.get(assistantId);
                    if(playerAssists != null) {
                        playerAssists++;
                        assistPlayers.put(assistantId, playerAssists);
                    } else {
                        assistPlayers.put(assistantId, 1);
                    }
                }
            }
        }

        Map.Entry<String, Integer> highestEntry = null;

        for (Map.Entry<String, Integer> entry : assistPlayers.entrySet()) {
            String key = entry.getKey();
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

    // statsPlayerGame
    public static String getBestScorerForAssitant(String playerId, ArrayList<Goal> goals) {
        return null;
    }
}
