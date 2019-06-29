package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.ArrayList;

public class AnalyzerService {

    // TEAM STATS
    // goalsTeamGame
    public static int getChemistryPoints(String playerId, String compareId, ArrayList<Goal> goals) {
        // +1 = jos compareId on ollut kentällä samaan aikaan
        // +2 = jos playerId on syöttänyt ja comparId on tehnyt maalin
        // +3 = jos compareId on syöttänyt ja playerId tehnyt maalin
        return 0;
    }

    // PLAYER STATS
    // statsPlayerGame
    public static String getBestAssistantForScorer(String playerId, ArrayList<Goal> goals) {
        return null;
    }
}
