package com.ardeapps.floorballmanager.analyzer;

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
import java.util.Map;

public class AnalyzerService {

    // These must be set before calling methods. GoalsInGames is not loaded automatically.
    // See LinesFragment and analyze chemistry button.
    static Map<String, ArrayList<Goal>> goalsInGames = new HashMap<>();
    static Map<String, ArrayList<Line>> linesInGames = new HashMap<>();
    static Map<String, Player> playersInTeam = new HashMap<>();
    private static AnalyzerService instance;
    private static boolean isJsonDatabase = false;
    // Käytä aina PLAYERS_OWN_POSITION paitsi parhaiden kenttien haussa
    private static AllowedPlayerPosition currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;

    public static AnalyzerService getInstance() {
        if (instance == null) {
            instance = new AnalyzerService();
        }
        if (!isJsonDatabase) {
            goalsInGames = AppRes.getInstance().getGoalsByGame();
            linesInGames = AppRes.getInstance().getLinesByGame();
            playersInTeam = AppRes.getInstance().getActivePlayersMap();
        }
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
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
     * MAIN METHOD (called from UI)
     *
     * Get best lines
     *
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     */
    public Map<Integer, Line> getBestLines(AllowedPlayerPosition allowedPlayerPosition, BestLineType bestLineType) {
        currentAllowedPlayerPosition = allowedPlayerPosition;
        ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);

        ArrayList<Position> positions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW, Position.LD, Position.RD));
        // Collect player by position to every place
        Map<Position, ArrayList<Player>> playersInPositions = new HashMap<>();
        for(Position position : positions) {
            playersInPositions.put(position, ChemistryPercentAnalyzer.getAllowedComparePlayers(position));
        }

        ArrayList<Pair<Position, ArrayList<Player>>> playerLists = new ArrayList<>();
        for(Position position : positions) {
            ArrayList<Player> playersInPosition = playersInPositions.get(position);
            if(playersInPosition != null) {
                playerLists.add(new Pair<>(position, playersInPosition));
            }
        }
        // Sort by longest position list
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
                                                AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                            }
                                        } else {
                                            Map<String, String> playersInLine = new HashMap<>();
                                            playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                            playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                            playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                            playersInLine.put(position4.toDatabaseName(), player4.getPlayerId());
                                            AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                        }
                                    }
                                } else {
                                    Map<String, String> playersInLine = new HashMap<>();
                                    playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                                    playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                                    playersInLine.put(position3.toDatabaseName(), player3.getPlayerId());
                                    AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                                }
                            }
                        } else {
                            Map<String, String> playersInLine = new HashMap<>();
                            playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                            playersInLine.put(position2.toDatabaseName(), player2.getPlayerId());
                            AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
                        }
                    }
                } else {
                    Map<String, String> playersInLine = new HashMap<>();
                    playersInLine.put(position1.toDatabaseName(), player1.getPlayerId());
                    AnalyzerWrapper.addChemistryToLine(playersInLine, playersInLines);
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

        Map<Integer, Line> lines = new HashMap<>();
        ArrayList<Map<String, String>> playersInBestLines = new ArrayList<>();

        // TASAISIMMAT KENTÄLLISET
        if(bestLineType == BestLineType.BEST_TEAM_CHEMISTRY) {
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
                                            AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                        }
                                    } else {
                                        ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                        lineCombinations.add(lineCombination1);
                                        lineCombinations.add(lineCombination2);
                                        lineCombinations.add(lineCombination3);
                                        AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                                    }
                                }
                            } else {
                                ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                                lineCombinations.add(lineCombination1);
                                lineCombinations.add(lineCombination2);
                                AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                            }
                        }
                    } else {
                        ArrayList<Pair<Map<String, String>, Integer>> lineCombinations = new ArrayList<>();
                        lineCombinations.add(lineCombination1);
                        AnalyzerHelper.addToLineCombinations(lineCombinations, bestSortedLineCombinations);
                    }
                }
            }

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

            if (bestSortedLineCombinations.size() > 0) {
                Pair<ArrayList<Map<String, String>>, Integer> bestLines = bestSortedLineCombinations.get(0);
                playersInBestLines = bestLines.first;
            }
        } else if(bestLineType == BestLineType.BEST_LINE_CHEMISTRY) {
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
            playersInBestLines = bestLinesAdded;
        }

        // Finally create lines
        int lineNumber = 1;
        for (Map<String, String> playersInLine : playersInBestLines) {
            Line line = new Line();
            line.setLineNumber(lineNumber);
            line.setPlayerIdMap(playersInLine);
            lines.put(lineNumber, line);
            lineNumber++;
        }
        return lines;
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
     * @param playersMap which players are calculated
     * @return chemistry percents to closest players indexed by position
     */
    public Map<Position, Integer> getClosestChemistryPercentsForPosition(Map<String, String> playersMap) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
        Map<Position, Integer> closestChemistries = new HashMap<>();
        if(playersMap != null) {
            Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerWrapper.getChemistriesInLineForPositions(playersMap);

            // Map contains only closest players, no others
            Map<Position, ArrayList<Chemistry>> filteredMap = AnalyzerHelper.getFilteredChemistryMapToClosestPlayers(chemistryMap);

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

    /**
     * MAIN METHOD (called from UI)
     *
     * Get average percent for team. Percent is average of line chemistry percentages.
     *
     * @param lines team lines
     * @return chemistry percent for team
     */
    public int getTeamChemistryPercent(Map<Integer, Line> lines) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
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
     * Get average percent for line. Average is calculated from closest positions.
     *
     * @param line which players are calculated
     * @return chemistry percent for line
     */
    public int getLineChemistryPercentForLine(Line line) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
        int percent = 0;
        if(line != null) {
            Map<ChemistryConnection, Integer> chemistryConnections = getChemistryConnectionPercents(line.getPlayerIdMap());
            double percentSize = chemistryConnections.size();
            if (percentSize == 0) {
                return 0;
            }
            double percentCount = 0;
            for (Map.Entry<ChemistryConnection, Integer> entry : chemistryConnections.entrySet()) {
                Integer chemistryConnectionPercent = entry.getValue();
                percentCount += chemistryConnectionPercent == null ? 0 : chemistryConnectionPercent;
            }

            percent = (int) Math.round(percentCount / percentSize);
        }
        return percent;
    }

    /**
     * MAIN METHOD (called from UI)
     *
     * Get map of chemistry connections and chemistry percents to use in UI
     *
     * @param playersMap which players are calculated
     * @return chemistry percent for chemistry connection lines and texts in UI
     */
    public Map<ChemistryConnection, Integer> getChemistryConnectionPercents(Map<String, String> playersMap) {
        if(currentAllowedPlayerPosition != AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            currentAllowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            ChemistryPercentAnalyzer.initialize(new ArrayList<>(playersInTeam.values()), currentAllowedPlayerPosition);
        }
        Map<ChemistryConnection, Integer> chemistryConnectionPercents = new HashMap<>();
        if(playersMap != null) {
            Map<Position, ArrayList<Chemistry>> chemistryMap = AnalyzerWrapper.getChemistriesInLineForPositions(playersMap);
            // Center
            Map<Position, Double> compareChemistryPercentMap = AnalyzerHelper.getConvertCompareChemistryPercentsForPosition(Position.C, chemistryMap);
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
            compareChemistryPercentMap = AnalyzerHelper.getConvertCompareChemistryPercentsForPosition(Position.LD, chemistryMap);
            chemistryPercent = compareChemistryPercentMap.get(Position.RD);
            if (chemistryPercent != null) {
                chemistryConnectionPercents.put(ChemistryConnection.LD_RD, (int) Math.round(chemistryPercent));
            }
            chemistryPercent = compareChemistryPercentMap.get(Position.LW);
            if (chemistryPercent != null) {
                chemistryConnectionPercents.put(ChemistryConnection.LD_LW, (int) Math.round(chemistryPercent));
            }
            // Right defender
            compareChemistryPercentMap = AnalyzerHelper.getConvertCompareChemistryPercentsForPosition(Position.RD, chemistryMap);
            chemistryPercent = compareChemistryPercentMap.get(Position.RW);
            if (chemistryPercent != null) {
                chemistryConnectionPercents.put(ChemistryConnection.RD_RW, (int) Math.round(chemistryPercent));
            }
        }
        return chemistryConnectionPercents;
    }
}
