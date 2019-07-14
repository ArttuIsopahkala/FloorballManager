package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Before;
import org.junit.Test;

public class AnalyzerServiceUnitTests extends JSONService {

    // Some base data
    String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
    String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"

    @Before
    public void initAnalyzerService() {
        AnalyzerService.setGoalsInGames(getTeamGoalsByGameId(teamId));
        AnalyzerService.setLinesInGames(getLinesOfGames(teamId));
        AnalyzerService.setPlayersInTeam(getPlayers(teamId));
    }

    // TODO Write unit tests for analyzer service methods
    @Test
    public void testGetLineChemistryPercentIsCorrect() {
        Line line = getLine(teamId, lineId);
        int percent = AnalyzerService.getInstance().getLineChemistryPercent(line);
        System.out.println("Percent: " + percent);
    }

}

