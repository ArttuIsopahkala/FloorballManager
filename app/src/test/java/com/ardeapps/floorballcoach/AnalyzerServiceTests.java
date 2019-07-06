package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.PlayerChemistry;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AnalyzerServiceTests extends JSONService {
    @Test
    public void isChemistryPointsCorrect() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";

        Player player = getPlayer(testPlayerId);
        if(player != null) {
            System.out.println("player: " + player.getName());
        }

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
        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));
        ArrayList<PlayerChemistry> playerChemistries =  AnalyzerService.getPlayerChemistries(players, getTeamGoals(teamId));

        String bestAssister = AnalyzerService.getBestScorerOrAssistant(false, "-LYDueLQnDlNS3iny3r2", getTeamGoals(teamId));
        System.out.println(bestAssister);

    }

    @Test
    public void testMethod() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";
        Player testPlayer = getPlayer(testPlayerId);
        Player comparePlayer = getPlayer("-LZf423Q01lgFW8yD5Dl");

        System.out.println("player1 = -LZf423Q01lgFW8yD5Dl");

        String player1Compare = AnalyzerService.getBestScorerOrAssistant(false, testPlayerId, getTeamGoals(teamId));
        System.out.println("player1Compare= " + player1Compare);

        System.out.println("player2 = -LZf4BuzjpwdhZFWX1G5");

        String player2Compare = AnalyzerService.getBestScorerOrAssistant(true, testPlayerId, getTeamGoals(teamId));
        System.out.println("player2Compare= " + player2Compare);

        int newChemistry = AnalyzerService.getChemistryPoints(testPlayer, comparePlayer, getTeamGoals(teamId));
        System.out.println(newChemistry);

        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));

        ArrayList<PlayerChemistry> chemistryList = AnalyzerService.getPlayerChemistries(players, getTeamGoals(teamId));
        boolean isEmpty = chemistryList.isEmpty();
        System.out.println(isEmpty);
    }

}

