package com.ardeapps.floorballmanager.services;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Chemistry.ChemistryConnection;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerService {

    // These must be set before calling methods. GoalsInGames is not loaded automatically.
    // See LinesFragment and analyze chemistry button.
    protected static Map<String, ArrayList<Goal>> goalsInGames = new HashMap<>();
    protected static Map<String, ArrayList<Line>> linesInGames = new HashMap<>();
    protected static Map<String, Player> playersInTeam = new HashMap<>();

    private static AnalyzerService instance;
    private static boolean isJsonDatabase = false;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if (!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getPlayers();
        }

        return instance;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setGoalsInGames(Map<String, ArrayList<Goal>> goals) {
        goalsInGames = goals;
        isJsonDatabase = true;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setLinesInGames(Map<String, ArrayList<Line>> lines) {
        linesInGames = lines;
        isJsonDatabase = true;
    }

    /**
     * Use this when calling from TEST class.
     */
    public static void setPlayersInTeam(Map<String, Player> players) {
        playersInTeam = players;
        isJsonDatabase = true;
    }

    /**
     * Database path: goalsTeamGame
     * <p>
     * Get best lines using goals from 3 last games
     *
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     */
    public Map<Integer, Line> getHotLines() {
        Map<Integer, Line> listOfBestLines = new HashMap<>();
        return listOfBestLines;
    }

    /**
     * MAIN METHOD (called from UI)
     * <p>
     * Get average percent for line. Average is calculated from closest positions.
     *
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercent(Line line) {
        Map<ChemistryConnection, Integer> chemistryConnections = getChemistryConnections(line);
        double percentSize = chemistryConnections.size();
        if (percentSize == 0) {
            return 0;
        }
        double percentCount = 0;
        for (Map.Entry<ChemistryConnection, Integer> entry : chemistryConnections.entrySet()) {
            Integer percent = entry.getValue();
            percentCount += percent == null ? 0 : percent;
        }

        return (int) Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     * <p>
     * Get average percent for team. Percent is average of line chemistry percentages.
     *
     * @param lines team lines
     * @return chemistry percent for team
     */
    public int getTeamChemistryPercent(Map<Integer, Line> lines) {
        double percentSize = lines.size();
        if (percentSize == 0) {
            return 0;
        }
        double percentCount = 0;
        for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
            Line line = entry.getValue();
            percentCount += getLineChemistryPercent(line);
        }
        return (int) Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     * <p>
     * Get map of chemistry connections and chemistry percents to use in UI
     *
     * @param line which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<ChemistryConnection, Integer> getChemistryConnections(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerCore.getChemistriesInLineForPositions(line);

        Map<ChemistryConnection, Integer> chemistryConnections = new HashMap<>();
        // Center
        Map<Position, Integer> compareChemistryMap = AnalyzerCore.getConvertCompareChemistryPercentsForPosition(Position.C, chemistryMap);
        Integer chemistry = compareChemistryMap.get(Position.LW);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_LW, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RW);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_RW, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.LD);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_LD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.RD);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.C_RD, chemistry);
        }
        // Left defender
        compareChemistryMap = AnalyzerCore.getConvertCompareChemistryPercentsForPosition(Position.LD, chemistryMap);
        chemistry = compareChemistryMap.get(Position.RD);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_RD, chemistry);
        }
        chemistry = compareChemistryMap.get(Position.LW);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.LD_LW, chemistry);
        }
        // Right defender
        compareChemistryMap = AnalyzerCore.getConvertCompareChemistryPercentsForPosition(Position.RD, chemistryMap);
        chemistry = compareChemistryMap.get(Position.RW);
        if (chemistry != null) {
            chemistryConnections.put(ChemistryConnection.RD_RW, chemistry);
        }

        return chemistryConnections;
    }

    /**
     * MAIN METHOD (called from UI)
     * <p>
     * Get chemistry percents to closest players in line.
     * This is how percent is calculated:
     * 1. Get closest players for every line position.
     * 2. Get chemistry percents between one position and closest positions.
     * 3. Calculate average of those chemistry percents.
     *
     * @param line which players are calculated
     * @return chemistry percents to closest players indexed by position
     */
    public Map<Position, Integer> getClosestChemistryPercentsForPosition(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerCore.getChemistriesInLineForPositions(line);

        // Map contains only closest players, no others
        Map<Position, ArrayList<Chemistry>> filteredMap = AnalyzerCore.getFilteredChemistryMapToClosestPlayers(chemistryMap);

        Map<Position, Integer> closestChemistries = new HashMap<>();

        // Calculate average points
        for (Map.Entry<Position, ArrayList<Chemistry>> entry : filteredMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            int percent = AnalyzerCore.getAverageChemistryPercent(chemistries);
            closestChemistries.put(position, percent);
        }

        return closestChemistries;
    }

}
