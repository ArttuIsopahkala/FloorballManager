package com.ardeapps.floorballmanager;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;
import com.ardeapps.floorballmanager.analyzer.AnalyzerWrapper;
import com.ardeapps.floorballmanager.analyzer.AnalyzerService;
import com.ardeapps.floorballmanager.services.JSONService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AnalyzerServiceTests extends JSONService {

    private final static String DATABASE_ROOT = "RELEASE";
    // Some base data
    String teamId; // "O2 jyväskylä"
    String lineId; // "1. kenttä"

    @Before
    public void initAnalyzerServiceData() {
        if (DATABASE_ROOT.equals(DEBUG)) {
            teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
            lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        } else {
            teamId = "-LlgU6eym9E4OqITqG4X"; // "O2 jyväskylä"
            lineId = "-LmFqM7wNJNvxYxNXCek"; // "1. kenttä"
        }
        AnalyzerService.setGoalsInGames(getTeamGoalsByGameId(teamId));
        AnalyzerService.setLinesInGames(getLinesOfGames(teamId));
        AnalyzerService.setPlayersInTeam(getPlayers(teamId));
    }

    @Test
    public void testGetLineChemistryPercent() {
        Line line = getLine(teamId, lineId);
        int percent = AnalyzerService.getInstance().getLineChemistryPercentForLine(line);
        System.out.println("Percent: " + percent);
    }

    @Test
    public void isChemistryPointsCorrect() {
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";

        Player player = getPlayer(testPlayerId);
        if (player != null) {
            System.out.println("player: " + player.getName());
        }

        System.out.println("All goals by team:");
        ArrayList<Goal> teamGoals = getTeamGoals(teamId);
        for (Goal goal : teamGoals) {
            System.out.println(goal.getGoalId());
        }

        System.out.println("All goals by player:");
        ArrayList<Goal> playerGoals = getPlayerGoals(testPlayerId);
        for (Goal goal : playerGoals) {
            System.out.println(goal.getGoalId());
        }

        Map<String, String> playerIdMap = getPlayersOfLine(teamId, lineId);
        // TODO testaa
        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));

    }

    @Test
    public void testGetLineChemistry() {
        Line line = getLine(teamId, lineId);
        if(line != null && line.getPlayerIdMap() != null) {
            Map<Position, ArrayList<Chemistry>> lineChemistry = AnalyzerWrapper.getChemistriesInLineForPositions(line.getPlayerIdMap());
            for (Map.Entry<Position, ArrayList<Chemistry>> chemistry : lineChemistry.entrySet()) {
                Position position = chemistry.getKey();
                ArrayList<Chemistry> chemistries = chemistry.getValue();
                for (Chemistry chem : chemistries) {
                    System.out.println(position.toDatabaseName() + " -> " + chem.getComparePosition());
                }
            }
        }
    }

    @Test
    public void testGetChemistryPointsPercent() {
        String playerId = "-LZf45PcYqU3sb7p5GFr";
        String comparePlayerId = "-LZf4BuzjpwdhZFWX1G5";
        Pair<Position, String> player = new Pair<>(Position.C, playerId);
        Pair<Position, String> comparePlayer = new Pair<>(Position.LD, comparePlayerId);
        /*double percent = AnalyzerWrapper.getChemistryPercent(player, comparePlayer);
        System.out.println("percent: " + percent);*/
    }

    @Test
    public void testMethod() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";
        Player testPlayer = getPlayer(testPlayerId);
        Player comparePlayer = getPlayer("-LZf423Q01lgFW8yD5Dl");

        System.out.println("player1 = -LZf423Q01lgFW8yD5Dl");
        Position position = Position.fromDatabaseName(testPlayer.getPosition());
        Position comparePosition = Position.fromDatabaseName(comparePlayer.getPosition());
       /* double newChemistry = AnalyzerWrapper.getChemistryPoints(position, comparePosition, testPlayer.getPlayerId(), comparePlayer.getPlayerId());
        System.out.println(newChemistry);*/

        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));
    }

    @Test
    public void fastTesting() {

        String teamId = "-LYDu_zW16xskSIhIlOm";
        ArrayList<Player> players = new ArrayList<>();
        players.add(getPlayer("-LYDueLQnDlNS3iny3r2"));
        players.add(getPlayer("-LZf3phcktGRlKYCJYNo"));
        players.add(getPlayer("-LZf423Q01lgFW8yD5Dl"));
        players.add(getPlayer("-LZf45PcYqU3sb7p5GFr"));
        players.add(getPlayer("-LZf4BuzjpwdhZFWX1G5"));

        //AnalyzerService.getInstance().getBestPlayerChemistries(players, getTeamGoals(teamId));
    }

}

