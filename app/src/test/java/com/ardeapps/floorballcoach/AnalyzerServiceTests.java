package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.PlayerChemistry;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

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

        Map<String, String> playerIdMap = getPlayersOfLine(teamId, lineId);
        // TODO testaa
        ArrayList<PlayerChemistry> playerChemistries =  AnalyzerService.getPlayerChemistries(playerIdMap, getTeamGoals(teamId));

        String bestAssister = AnalyzerService.getBestScorerOrAssistant(false, "-LYDueLQnDlNS3iny3r2", getTeamGoals(teamId));
        System.out.println(bestAssister);

    }

    @Test
    public void testMethod() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";

        System.out.println("player1 = -LZf423Q01lgFW8yD5Dl");

        String player1Compare = AnalyzerService.getBestScorerOrAssistant(false, testPlayerId, getTeamGoals(teamId));
        System.out.println("player1Compare= " + player1Compare);

        System.out.println("player2 = -LZf4BuzjpwdhZFWX1G5");

        String player2Compare = AnalyzerService.getBestScorerOrAssistant(true, testPlayerId, getTeamGoals(teamId));
        System.out.println("player2Compare= " + player2Compare);

    }

}

