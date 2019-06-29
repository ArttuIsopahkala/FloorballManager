package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    // TEAM STATS
    // goalsTeamGame
    public static int getChemistryPoints(String playerId, String compareId, ArrayList<Goal> goals) {

        int chemistryPoints = 0;

        // +1 = jos compareId on ollut kentällä samaan aikaan
        // +2 = jos playerId on syöttänyt ja comparId on tehnyt maalin
        // +3 = jos compareId on syöttänyt ja playerId tehnyt maalin
        for(Goal goal : goals) {
            if(goal.isOpponentGoal() == true && goal.getPlayerIds().contains(playerId) && goal.getPlayerIds().contains(compareId)) {
                chemistryPoints--;
            } else if(goal.isOpponentGoal() == false && goal.getScorerId() == playerId && goal.getAssistantId() == compareId) {
                chemistryPoints += 3;
            } else if(goal.isOpponentGoal() == false && goal.getScorerId() == compareId && goal.getAssistantId() == playerId) {
                chemistryPoints += 2;
            } else if(goal.isOpponentGoal() == false && goal.getPlayerIds().contains(playerId) && goal.getPlayerIds().contains(compareId)) {
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
            if(goal.getScorerId() == playerId) {
                String assistantId = goal.getAssistantId();
                if(assistPlayers.containsKey(assistantId)) {
                    int playerAssists = assistPlayers.get(assistantId);
                    playerAssists++;
                    assistPlayers.put(assistantId, playerAssists);
                } else {
                    assistPlayers.put(assistantId, 1);
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

        return highestEntry.getKey();
    }

    // Analytic methods
    //
}
