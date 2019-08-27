package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.ChemistryConnection;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnalyzerService {

    // These must be set before calling methods. GoalsInGames is not loaded automatically.
    // See LinesFragment and analyze chemistry button.
    protected static Map<String, ArrayList<Goal>> goalsInGames = new HashMap<>();
    protected static Map<String, ArrayList<Line>> linesInGames = new HashMap<>();
    protected static Map<String, Player> playersInTeam = new HashMap<>();

    private static final ArrayList<Position> attackerPositions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW));
    private static final ArrayList<Position> defenderPositions = new ArrayList<>(Arrays.asList(Position.LD, Position.RD));
    protected static Map<ChemistryConnection, Double> minPointsForChemistryConnections = new HashMap<>();
    protected static Map<ChemistryConnection, Double> maxPointsForChemistryConnections = new HashMap<>();
    private static AnalyzerService instance;
    private static boolean isJsonDatabase = false;
    protected static ArrayList<Player> attackers = new ArrayList<>();
    protected static ArrayList<Player> defenders = new ArrayList<>();
    private static boolean initializeChemistryConnectionAnalyzer = true;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if (!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getActivePlayersMap();
        }
        for(Player player : playersInTeam.values()) {
            Position position = Position.fromDatabaseName(player.getPosition());
            if(attackerPositions.contains(position)) {
                attackers.add(player);
            }
            if(defenderPositions.contains(position)) {
                defenders.add(player);
            }
        }
        if(initializeChemistryConnectionAnalyzer) {
            // Käytä vain MOST_GOALS_IN_POSITION tai PLAYERS_OWN_POSITION
            AllowedPlayerPosition allowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryConnectionAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), allowedPlayerPosition);
            initializeChemistryConnectionAnalyzer = false;
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
     *
     * Get best lines using goals from 3 last games
     *
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     */
    public Map<Integer, Line> getHotLines() {
        Map<Integer, Line> listOfBestLines = new HashMap<>();
        return listOfBestLines;
    }

    @Deprecated
    private Pair<Player, Double> getBestPlayerForPosition(Position position, ArrayList<Player> comparePlayers) {
        double bestChemistrySum = 0;
        if(comparePlayers != null) {
            Player bestPlayer = null;
            for(Player player : comparePlayers) {
                double chemistrySum = ChemistryConnectionAnalyzer.getBestChemistryPointsSum(player, position);
                if (chemistrySum > bestChemistrySum) {
                    bestChemistrySum = chemistrySum;
                    bestPlayer = player;
                }
            }
            if(bestPlayer != null) {
                return new Pair<>(bestPlayer, bestChemistrySum);
            }
        }
        return null;
    }

    enum AllowedPlayerPosition {
        MOST_GOALS_IN_POSITION,
        PLAYERS_OWN_POSITION
    }


    private void addToLines(Map<String, String> playersInLine, ArrayList<Pair<Map<String, String>, Integer>> playersInLines) {
        Set<String> set = new HashSet<>(playersInLine.keySet());
        boolean duplicatePlayers = set.size() < playersInLine.size();
        if (!duplicatePlayers) {
            int chemistryForLine = 0;
            // Collect chemistry sum in line
            for (Map.Entry<String, String> entry : playersInLine.entrySet()) {
                Position position = Position.fromDatabaseName(entry.getKey());
                String playerId = entry.getValue();
                for (Map.Entry<String, String> compareEntry : playersInLine.entrySet()) {
                    Position comparePosition = Position.fromDatabaseName(compareEntry.getKey());
                    String comparePlayerId = compareEntry.getValue();
                    if(!playerId.equals(comparePlayerId)) {
                        Pair<Position, String> player1 = new Pair<>(position, playerId);
                        Pair<Position, String> player2 = new Pair<>(comparePosition, comparePlayerId);
                        double points = ChemistryPointsAnalyzer.getChemistryPoints(player1, player2);
                        double chemistryPercent = ChemistryConnectionAnalyzer.getChemistryPercent(position, comparePosition, points);
                        chemistryForLine += chemistryPercent;
                    }
                }
            }
            playersInLines.add(new Pair<>(playersInLine, chemistryForLine));
        }
    }

    private void addToLineCombinations(ArrayList<Pair<Map<String, String>, Integer>> lineCombinations, ArrayList<Pair<ArrayList<Map<String, String>>, Integer>> addedLineCombinations) {
        int chemistrySumForLines = 0;
        ArrayList<Pair<Map<String, String>, Integer>> playersAndPoints = new ArrayList<>();
        ArrayList<String> addedPlayerIds = new ArrayList<>();
        for(Pair<Map<String, String>, Integer> lineCombination : lineCombinations) {
            Map<String, String> playersInLine = lineCombination.first;

            boolean lineContainsAddedPlayer = false;
            for (String playerId : playersInLine.values()) {
                if (addedPlayerIds.contains(playerId)) {
                    lineContainsAddedPlayer = true;
                }
            }
            // Return if lines contains duplicated playerId
            if (lineContainsAddedPlayer) {
                return;
            }
            addedPlayerIds.addAll(playersInLine.values());

            Integer chemistrySum = lineCombination.second;
            chemistrySumForLines += chemistrySum;
            playersAndPoints.add(new Pair<>(playersInLine, chemistrySum));
        }
        // Sort lines by chemistry to best lines
        Collections.sort(playersAndPoints, (o1, o2) -> Integer.compare(o2.second, o1.second));
        ArrayList<Map<String, String>> playersList = new ArrayList<>();
        for(Pair<Map<String, String>, Integer> playersAndPoint : playersAndPoints) {
            playersList.add(playersAndPoint.first);
        }
        addedLineCombinations.add(new Pair<>(playersList, chemistrySumForLines));
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get best lines
     *
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     */
    public Map<Integer, Line> getBestLines() {

        ArrayList<Position> positions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW, Position.LD, Position.RD));
        // Collect player by position to every place
        Map<Position, ArrayList<Player>> playersInPositions = new HashMap<>();
        for(Position position : positions) {
            playersInPositions.put(position, ChemistryConnectionAnalyzer.getAllowedComparePlayers(position));
        }

        ArrayList<Pair<Position, ArrayList<Player>>> playerLists = new ArrayList<>();
        for(Position position : positions) {
            ArrayList<Player> playersInPosition = playersInPositions.get(position);
            if(playersInPosition != null) {
                playerLists.add(new Pair<>(position, playersInPosition));
            }
        }

        Collections.sort(playerLists, (o1, o2) -> Integer.compare(o2.second.size(), o1.second.size()));
        ArrayList<Pair<Map<String, String>, Integer>> playersInLines = new ArrayList<>();
        // Loop all possible line combinations
        int positionsListsFound = playerLists.size();
        if(positionsListsFound > 0) {
            Pair<Position, ArrayList<Player>> list1 = playerLists.get(0);
            Position position1 = list1.first;
            for (Player player1 : list1.second) {
                if (playerLists.size() > 1) {
                    Pair<Position, ArrayList<Player>> list2 = playerLists.get(1);
                    Position position2 = list2.first;
                    for (Player player2 : list2.second) {
                        if (playerLists.size() > 2) {
                            Pair<Position, ArrayList<Player>> list3 = playerLists.get(2);
                            Position position3 = list3.first;
                            for (Player player3 : list3.second) {
                                if (playerLists.size() > 3) {
                                    Pair<Position, ArrayList<Player>> list4 = playerLists.get(3);
                                    Position position4 = list4.first;
                                    for (Player player4 : list4.second) {
                                        if (playerLists.size() > 4) {
                                            Pair<Position, ArrayList<Player>> list5 = playerLists.get(4);
                                            Position position5 = list5.first;
                                            for (Player player5 : list5.second) {
                                                Map<String, String> playersInLine = new HashMap<>();
                                                playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                                playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                                playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                                playersInLine.put(position4.toDatabaseName(), player4.getPlayerId());
                                                playersInLine.put(position5.toDatabaseName(), player5.getPlayerId());
                                                addToLines(playersInLine, playersInLines);
                                            }
                                        } else {
                                            Map<String, String> playersInLine = new HashMap<>();
                                            playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                            playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                            playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                            playersInLine.put(position4.toDatabaseName(), player4.getPlayerId());
                                            addToLines(playersInLine, playersInLines);
                                        }
                                    }
                                } else {
                                    Map<String, String> playersInLine = new HashMap<>();
                                    playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                    playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                    playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                    addToLines(playersInLine, playersInLines);
                                }
                            }
                        } else {
                            Map<String, String> playersInLine = new HashMap<>();
                            playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                            playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                            addToLines(playersInLine, playersInLines);
                        }
                    }
                } else {
                    Map<String, String> playersInLine = new HashMap<>();
                    playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                    addToLines(playersInLine, playersInLines);
                }
            }
        }

        // TODO Täytä puuttuvat pelipaikat?

        // LOGGING
        int index = 1;
        Collections.sort(playersInLines, (o1, o2) -> Double.compare(o2.second, o1.second));
        for (Pair<Map<String, String>, Integer> lineCombination : playersInLines) {
            Map<String, String> playersMap = lineCombination.first;
            Integer chemistryPercent = lineCombination.second;
            Logger.log(index + " KENTTÄ " + playersMap + ": " + chemistryPercent);
            index++;
        }

        // floor = round down to nearest integer
        int linesToCreate = (int)Math.floor(playersInTeam.size() / 5.0);
        if(linesToCreate > 4) {
            linesToCreate = 4;
        }

        boolean parhaat = false;
        boolean tasaisimmat = true;

        if(tasaisimmat) {
            // TASAISIMMAT KENTÄLLISET
            // Hae haluttujen kentällisten verran comboja ja laske kemiasumma
            ArrayList<Pair<ArrayList<Map<String, String>>, Integer>> bestSortedLineCombinations = new ArrayList<>();
            if (linesToCreate > 0) {
                for (Pair<Map<String, String>, Integer> lineCombination1 : playersInLines) {
                    if (linesToCreate > 1) {
                        for (Pair<Map<String, String>, Integer> lineCombination2 : playersInLines) {
                            if (linesToCreate > 2) {
                                for (Pair<Map<String, String>, Integer> lineCombination3 : playersInLines) {
                                    if (linesToCreate > 3) {
                                        for (Pair<Map<String, String>, Integer> lineCombination4 : playersInLines) {
                                            ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                            lineCombinations.add(lineCombination1);
                                            lineCombinations.add(lineCombination2);
                                            lineCombinations.add(lineCombination3);
                                            lineCombinations.add(lineCombination4);
                                            addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                        }
                                    } else {
                                        ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                        lineCombinations.add(lineCombination1);
                                        lineCombinations.add(lineCombination2);
                                        lineCombinations.add(lineCombination3);
                                        addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                    }
                                }
                            } else {
                                ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                lineCombinations.add(lineCombination1);
                                lineCombinations.add(lineCombination2);
                                addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                            }
                        }
                    } else {
                        ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                        lineCombinations.add(lineCombination1);
                        addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                    }
                }
            }

            //ArrayList<Pair<ArrayList<Map<String, String>>, Integer>> addedLineCombinations = new ArrayList<>();
            // Sort by chemistry sum
            Collections.sort(bestSortedLineCombinations, (o1, o2) -> Integer.compare(o2.second, o1.second));
            for (Pair<ArrayList<Map<String, String>>, Integer> lineCombination : bestSortedLineCombinations) {
                ArrayList<Map<String, String>> playerInLines = lineCombination.first;
                Integer chemistrySumInLines = lineCombination.second;
                Logger.log("KENTTÄ SUM " + chemistrySumInLines);
                for (Map<String, String> playersInLine : playerInLines) {
                    Logger.log("KENTTÄ PELAAJAT " + playersInLine);
                }
            }

            Map<Integer, Line> lines = new HashMap<>();
            if (bestSortedLineCombinations.size() > 0) {
                Pair<ArrayList<Map<String, String>>, Integer> bestLines = bestSortedLineCombinations.get(0);
                ArrayList<Map<String, String>> playerInLines = bestLines.first;
                int lineNumber = 1;
                for (Map<String, String> playersInLine : playerInLines) {
                    Line line = new Line();
                    line.setLineNumber(lineNumber);
                    line.setPlayerIdMap(playersInLine);
                    lines.put(lineNumber, line);
                    lineNumber++;
                }
            }
            return lines;
        } else if(parhaat) {
            // Collect lines
            ArrayList<Map<String, String>> bestLinesAdded = new ArrayList<>();
            ArrayList<String> addedPlayerIds = new ArrayList<>();
            for (Pair<Map<String, String>, Integer> lineCombination : playersInLines) {
                Map<String, String> playersMap = lineCombination.first;
                boolean lineContainsAddedPlayer = false;
                for (String playerId : playersMap.values()) {
                    if (addedPlayerIds.contains(playerId)) {
                        lineContainsAddedPlayer = true;
                    }
                }
                if (!lineContainsAddedPlayer) {
                    bestLinesAdded.add(playersMap);
                    addedPlayerIds.addAll(playersMap.values());
                }
            }

            Map<Integer, Line> lines = new HashMap<>();
            int lineNumber = 1;
            for (Map<String, String> playersInLine : bestLinesAdded) {
                Line line = new Line();
                line.setLineNumber(lineNumber);
                line.setPlayerIdMap(playersInLine);
                lines.put(lineNumber, line);
                lineNumber++;
            }
            return lines;
        }

        return new HashMap<>();
    }

    /*public Map<Integer, Line> getBestLines() {
        // Collect player by position to every place
        Map<Position, ArrayList<Player>> playersInPositionMap = new HashMap<>();
        for (Player player : playersInTeam.values()) {
            Position position = Position.fromDatabaseName(player.getPosition());
            ArrayList<Player> playersInPosition = playersInPositionMap.get(position);
            if(playersInPosition == null) {
                playersInPosition = new ArrayList<>();
            }
            playersInPosition.add(player);
            playersInPositionMap.put(position, playersInPosition);
        }

        Map<Player, Double> chemistrySumsForPosition = new HashMap<>();
        // Loop players for every position
        for (Map.Entry<Position, ArrayList<Player>> entry : playersInPositionMap.entrySet()) {
            Position currentPosition = entry.getKey();
            ArrayList<Player> playersInPosition = entry.getValue();
            for(Player player : playersInPosition) {
                double chemistryPointsSumForPosition = GetBestLinesHelper.getBestChemistryPointsSum(player, currentPosition, playersInPositionMap);
                chemistrySumsForPosition.put(player, chemistryPointsSumForPosition);
            }
        }

        // Convert chemistry sum values to sorted players array by position
        Map<Position, ArrayList<Player>> sortedBestPlayersForPosition = GetBestLinesHelper.getSortedBestPlayersForPosition(chemistrySumsForPosition);

        int linesToCreate;
        if(playersInTeam.size() < 10) {
            linesToCreate = 1;
        } else if(playersInTeam.size() < 15) {
            linesToCreate = 2;
        } else if(playersInTeam.size() < 20) {
            linesToCreate = 3;
        } else {
            linesToCreate = 4;
        }

        ArrayList<String> usedPlayers = new ArrayList<>();

        Map<Integer, Line> lines = new HashMap<>();
        for(int index = 0; index < linesToCreate; index++) {
            Line line = new Line();
            Player LW = GetBestLinesHelper.getPlayerInIndex(index, sortedBestPlayersForPosition.get(Position.LW));
            Player C = GetBestLinesHelper.getPlayerInIndex(index, sortedBestPlayersForPosition.get(Position.C));
            Player RW = GetBestLinesHelper.getPlayerInIndex(index, sortedBestPlayersForPosition.get(Position.RW));
            Player LD = GetBestLinesHelper.getPlayerInIndex(index, sortedBestPlayersForPosition.get(Position.LD));
            Player RD = GetBestLinesHelper.getPlayerInIndex(index, sortedBestPlayersForPosition.get(Position.RD));

            Map<Position, Player> playerMap = new HashMap<>();
            playerMap.put(Position.LW, LW);
            playerMap.put(Position.C, C);
            playerMap.put(Position.RW, RW);
            playerMap.put(Position.LD, LD);
            playerMap.put(Position.RD, RD);
            Map<String, String> playerIdMap = GetBestLinesHelper.convertToPlayerIdsMap(playerMap);
            usedPlayers.addAll(playerIdMap.values());

            int lineNumber = index + 1;
            line.setLineNumber(lineNumber);
            line.setPlayerIdMap(playerIdMap);
            lines.put(lineNumber, line);
        }

        ArrayList<Player> notAddedPlayers = new ArrayList<>();
        for (Player player : playersInTeam.values()) {
            if(!usedPlayers.contains(player.getPlayerId())) {
                notAddedPlayers.add(player);
            }
        }

        ArrayList<Position> positions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW, Position.LD, Position.RD));
        // Fill in empty slots with rest of players (player's position doesn't matter)
        for(Line line : lines.values()) {
            Map<String, String> playerIdMap = line.getPlayerIdMap();
            for(Position position : positions) {
                String playerId = playerIdMap.get(position.toDatabaseName());
                if(playerId == null) {
                    double currentSum = 0;
                    Player selectedPlayer = null;
                    for(Player notAddedPlayer : notAddedPlayers) {
                        double chemistrySum = GetBestLinesHelper.getBestChemistryPointsSum(notAddedPlayer, position, playersInPositionMap);
                        if(chemistrySum > currentSum) {
                            selectedPlayer = notAddedPlayer;
                            currentSum = chemistrySum;
                        }
                    }
                    if(selectedPlayer != null) {
                        playerIdMap.put(position.toDatabaseName(), selectedPlayer.getPlayerId());
                        notAddedPlayers.remove(selectedPlayer);
                    }
                }
            }
        }

        return lines;
    }*/

    /**
     * MAIN METHOD (called from UI)
     *
     * Get average percent for line. Average is calculated from closest positions.
     *
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercentForLine(Line line) {
        Map<ChemistryConnection, Integer> chemistryConnections = getChemistryConnectionPercentsForLine(line);
        return getLineChemistryPercentForLine(chemistryConnections);
    }

    public int getLineChemistryPercentForPlayers(Map<String, String> playersMap) {
        Map<ChemistryConnection, Integer> chemistryConnections = getChemistryConnectionPercentsForPlayers(playersMap);
        return getLineChemistryPercentForLine(chemistryConnections);
    }

    private int getLineChemistryPercentForLine(Map<ChemistryConnection, Integer> chemistryConnections) {
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
     *
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
            percentCount += getLineChemistryPercentForLine(line);
        }
        return (int) Math.round(percentCount / percentSize);
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     *
     * @param line which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<ChemistryConnection, Integer> getChemistryConnectionPercentsForLine(Line line) {
        Map<ChemistryConnection, Integer> chemistryConnectionPercents = new HashMap<>();
        if(line != null) {
            chemistryConnectionPercents = getChemistryConnectionPercentsForPlayers(line.getPlayerIdMap());
        }
        return chemistryConnectionPercents;
    }

    protected Map<ChemistryConnection, Integer> getChemistryConnectionPercentsForPlayers(Map<String, String> playersMap) {
        Map<ChemistryConnection, Integer> chemistryConnectionPercents = new HashMap<>();
        if(playersMap != null) {
            Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerWrapper.getChemistriesInLineForPositions(playersMap);
            chemistryConnectionPercents = getChemistryConnectionPercents(chemistryMap);
        }
        return chemistryConnectionPercents;
    }

    protected Map<ChemistryConnection, Integer> getChemistryConnectionPercents(Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<ChemistryConnection, Integer> chemistryConnectionPercents = new HashMap<>();
        // Center
        Map<Position, Double> compareChemistryPercentMap = AnalyzerWrapper.getConvertCompareChemistryPercentsForPosition(Position.C, chemistryMap);
        Double chemistryPercent = compareChemistryPercentMap.get(Position.LW);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.C_LW, (int) Math.round(chemistryPercent));
        }
        chemistryPercent = compareChemistryPercentMap.get(Position.RW);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.C_RW, (int) Math.round(chemistryPercent));
        }
        chemistryPercent = compareChemistryPercentMap.get(Position.LD);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.C_LD, (int) Math.round(chemistryPercent));
        }
        chemistryPercent = compareChemistryPercentMap.get(Position.RD);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.C_RD, (int) Math.round(chemistryPercent));
        }
        // Left defender
        compareChemistryPercentMap = AnalyzerWrapper.getConvertCompareChemistryPercentsForPosition(Position.LD, chemistryMap);
        chemistryPercent = compareChemistryPercentMap.get(Position.RD);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.LD_RD, (int) Math.round(chemistryPercent));
        }
        chemistryPercent = compareChemistryPercentMap.get(Position.LW);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.LD_LW, (int) Math.round(chemistryPercent));
        }
        // Right defender
        compareChemistryPercentMap = AnalyzerWrapper.getConvertCompareChemistryPercentsForPosition(Position.RD, chemistryMap);
        chemistryPercent = compareChemistryPercentMap.get(Position.RW);
        if (chemistryPercent != null) {
            chemistryConnectionPercents.put(ChemistryConnection.RD_RW, (int) Math.round(chemistryPercent));
        }

        return chemistryConnectionPercents;
    }

    /**
     * MAIN METHOD (called from UI)
     *
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
        Map<Position, Integer> closestChemistries = new HashMap<>();
        if(line != null && line.getPlayerIdMap() != null) {
            Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerWrapper.getChemistriesInLineForPositions(line.getPlayerIdMap());

            // Map contains only closest players, no others
            Map<Position, ArrayList<Chemistry>> filteredMap = AnalyzerWrapper.getFilteredChemistryMapToClosestPlayers(chemistryMap);

            // Calculate average points
            for (Map.Entry<Position, ArrayList<Chemistry>> entry : filteredMap.entrySet()) {
                Position position = entry.getKey();
                ArrayList<Chemistry> chemistries = entry.getValue();

                double percent = AnalyzerWrapper.getAverageChemistryPercent(chemistries);
                closestChemistries.put(position, (int)Math.round(percent));
            }
        }
        return closestChemistries;
    }

}
