package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;
import com.ardeapps.floorballcoach.services.AnalyzerCore;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AnalyzerServiceTests extends JSONService {

    // Some base data
    String teamId; // "O2 jyväskylä"
    String lineId; // "1. kenttä"
    private final static String DATABASE_ROOT = "RELEASE";

    @Before
    public void initAnalyzerServiceData() {
        if(DATABASE_ROOT.equals(DEBUG)) {
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
        int percent = AnalyzerService.getInstance().getLineChemistryPercent(line);
        System.out.println("Percent: " + percent);
    }

    @Test
    public void isChemistryPointsCorrect() {
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

    }

    @Test
    public void testGetLineChemistry() {
        Line line = getLine(teamId, lineId);
        Map<Position, ArrayList<Chemistry>> lineChemistry = AnalyzerCore.getChemistriesInLineForPositions(line);
        for (Map.Entry<Position, ArrayList<Chemistry>> chemistry : lineChemistry.entrySet()) {
            Position position = chemistry.getKey();
            ArrayList<Chemistry> chemistries = chemistry.getValue();
            for(Chemistry chem : chemistries) {
                System.out.println(position.toDatabaseName() + " -> " + chem.getComparePosition());
            }
        }
    }

    @Test
    public void testGetChemistryPointsPercent() {
        String playerId = "-LZf45PcYqU3sb7p5GFr";
        String comparePlayerId = "-LZf4BuzjpwdhZFWX1G5";

        double percent = AnalyzerCore.getChemistryPercent(Position.C, playerId, Position.LD, comparePlayerId);
        System.out.println("percent: " + percent);
    }

    @Test
    public void getMaxAndMinChemistryPoints() {
        int minPoints = AnalyzerCore.getMinChemistryPoints();
        int maxPoints = AnalyzerCore.getMaxChemistryPoints();
        System.out.println("maxPoints: " + maxPoints);
        System.out.println("minPoints: " + minPoints);
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
        int newChemistry = AnalyzerCore.getChemistryPoints(position, testPlayer.getPlayerId(), comparePosition, comparePlayer.getPlayerId(), getTeamGoals(teamId));
        System.out.println(newChemistry);

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

