package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.PlayerChemistry;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Test;

import java.util.ArrayList;

public class AnalyzerServiceTests extends JSONService {
    @Test
    public void isChemistryPointsCorrect() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";

        System.out.println("All goals by team:");
        ArrayList<Goal> teamGoals = getTeamGoals(teamId);
        for(Goal goal : teamGoals) {
            System.out.println(goal.getGoalId());
        }

        System.out.println("All goals by player:");
        ArrayList<Goal> playerGoals = getPlayerGoals(testPlayerId);
        for(Goal goal : playerGoals) {
            System.out.println(goal.getGoalId());
        }

        ArrayList<String> playerIds = getPlayersOfLine(teamId, lineId);
        for(String playerId : playerIds) {
            PlayerChemistry playerChemistry = new PlayerChemistry();
            playerChemistry.setPlayerId(playerId);
            for(String comparePlayerId : playerIds) {
                int testChemistry = AnalyzerService.getChemistryPoints(playerId, comparePlayerId, getTeamGoals(teamId));
                playerChemistry.getComparePlayers().put(comparePlayerId, testChemistry);
            }
        }

        String bestAssister = AnalyzerService.getBestAssistantForScorer("-LYDueLQnDlNS3iny3r2", getTeamGoals(teamId));
        System.out.println(bestAssister);

    }

}

