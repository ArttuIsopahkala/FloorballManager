package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerService {

    /**
     * Database path: goalsTeamGame
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param goals goals saved for team
     * @return count of chemistry points
     */
    public static int getChemistryPoints(String playerId, String comparedPlayerId, ArrayList<Goal> goals) {

        int chemistryPoints = 0;

        // +3 = if playerId has scored and compareId assisted
        // +2 = if playerId has assisted and compareId scored
        // +1 = if compareId and playerId has been on the field when goal happened
        // -1 = if playerId and compareId has been on the field when goal happened and isOpponentGoal == true
        for(Goal goal : goals) {
            List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
            List<String> scorerAndAssistant = Arrays.asList(goal.getScorerId(), goal.getAssistantId());

            boolean bothPlayersOnField = goal.getPlayerIds() != null && goal.getPlayerIds().containsAll(comparePlayers);
            if(bothPlayersOnField) {
                if(goal.isOpponentGoal()) {
                    chemistryPoints--;
                } else if(!goal.isOpponentGoal() && scorerAndAssistant.containsAll(comparePlayers)) {
                    chemistryPoints += 3;
                } else if(!goal.isOpponentGoal() && (scorerAndAssistant.contains(playerId) || scorerAndAssistant.contains(comparedPlayerId))) {
                    chemistryPoints += 2;
                } else if(!goal.isOpponentGoal()) {
                    chemistryPoints++;
                }
            }
        }
        return chemistryPoints;
    }

    /**
     * Database path: statsPlayerGame
     * @param lookingBestScorer if true looks for best scorer for playerId assists else best assistant for playerId scores
     * @param playerId assistant playerId
     * @param goals goals saved for player
     * @return playerId of the best scorer
     */
    public static String getBestScorerOrAssistant(boolean lookingBestScorer, String playerId, ArrayList<Goal> goals) {

        HashMap<String, Integer> players = new HashMap<>();
        String newPlayerId;

        for (Goal goal : goals) {
            if(lookingBestScorer && playerId.equals(goal.getAssistantId())) {
                newPlayerId = goal.getScorerId();
            } else if(!lookingBestScorer && playerId.equals(goal.getScorerId())) {
                newPlayerId = goal.getAssistantId();
            } else {
                newPlayerId = null;
            }

            if(newPlayerId != null) {
                Integer playerScores = players.get(newPlayerId);
                if(playerScores != null) {
                    playerScores++;
                    players.put(newPlayerId, playerScores);
                } else {
                    players.put(newPlayerId, 1);
                }
            }
        }

        Map.Entry<String, Integer> highestEntry = null;

        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer value = entry.getValue();

            if(highestEntry == null || highestEntry.getValue() < value) {
                highestEntry = entry;
            }
        }

        if(highestEntry == null) {
            return null;
        }

        return highestEntry.getKey();
    }

    /**
     * Database path: statsPlayerGame
     * @param players List of Players which chemistries are calculated
     * @param goals Team goals where chemistry is calculated
     * @return List of player chemistries
     */
    public static ArrayList<Chemistry> getPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {

        ArrayList<Chemistry> playerChemistryList = new ArrayList<>();

        for(Player player : players) {
            Chemistry chemistry = new Chemistry();
            chemistry.setPlayerId(player.getPlayerId());
            for(Player comparePlayer : players) {
                if(!comparePlayer.getPlayerId().equals(player.getPlayerId())) {
                    int chemistryPoints = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    chemistry.setChemistryPoints(chemistryPoints);
                    chemistry.setComparePlayerId(comparePlayer.getPlayerId());
                    chemistry.setComparePosition(comparePlayer.getPosition());
                }
            }

            playerChemistryList.add(chemistry);
        }

        return playerChemistryList;
    }

    /**
     * Database path: goalsTeamGame
     * @param players All players in a team
     * @param goals team goals where chemistry is calculated
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     *
     * This method should get List of Players and return Lines where are separated players in the way that
     * best chemistries are taken in notice between players and their positions
     *
     * Something like this:
     *     bestChemistryPointsCalculation(List<Players> players, List<Goals> goals) {
     *
     *     List<Players> centers = players.getCenters();
     *
     *     List<PlayerChemistries> playerChemistries = getChemistries() (chemistries between every player)
     *
     *     take every center one by one, get 4 highest player chemistryPoints (notice correct positions (LW, RW, LD, RD))
     *     Calculate best average chemistry for a Line (all positions) from the previous calculation
     *
     *     List<Lines> bestLines = create best lines (1. line center and players with average best chemistries between every player
     *                                                2. second best etc)
     *
     *     Can be appended in the future to make best lines for defending (weight to defenders chemistry), goalmaking (weight to forwards chemsitry) etc
     *
     */
    public static Map<Integer, Line> getBestPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {
        // TODO tee loppuun ja testaa
        // playerIdMap:
        // key = position(Player.Position), value = playerId
        ArrayList<Player> centers = new ArrayList<>();
        Map<String, ArrayList<Chemistry>> centerPlayerChemistriesMap = new HashMap<>();
        ArrayList<Chemistry> centerPlayerChemistryList = new ArrayList<>();
        ArrayList<Player> listOfPlayers = new ArrayList<>();
        Map<Integer, Line> listOfBestLines = new HashMap<>();

        ArrayList<Chemistry> playerChemistries = getPlayerChemistries(players, goals);

        // Set center to own ArrayList and rest of the players to another
        for (Player player : players) {
            Player.Position position = Player.Position.fromDatabaseName(player.getPosition());
            if(position == Player.Position.C) {
                centers.add(player);
            } else {
                listOfPlayers.add(player);
            }
        }

        // Create Map which contains centers and chemistries between every center individually and every player in other positions
        for (Player center : centers) {
            for (Player player : listOfPlayers) {
                int chemistryPoints = getChemistryPoints(center.getPlayerId(), player.getPlayerId(), goals);
                Chemistry newChemistry = new Chemistry();
                newChemistry.setPlayerId(center.getPlayerId());
                newChemistry.setComparePlayerId(player.getPlayerId());
                newChemistry.setComparePosition(player.getPosition());
                newChemistry.setChemistryPoints(chemistryPoints);
                centerPlayerChemistryList.add(newChemistry);
            }

            centerPlayerChemistriesMap.put(center.getPlayerId(), centerPlayerChemistryList);
        }

        // TODO Continue from here probably save centerId + centerBestOverallChemistry to Map<String, Integer> or something and get best center from that
        for (Player center : centers) {
            ArrayList<Chemistry> chemistries = centerPlayerChemistriesMap.get(center.getPlayerId());

            int centerBestOverallChemistry = 0;

            int bestChemistryPointsLw = 0;
            int bestChemistryPointsRw = 0;
            int bestChemistryPointsLd = 0;
            int bestChemistryPointsRd = 0;

            for (Chemistry chemistry : chemistries) {

                Player.Position position = Player.Position.fromDatabaseName(chemistry.getComparePosition());
                int playerChemistryPoints = chemistry.getChemistryPoints();

                if(position.equals(Player.Position.LW)) {
                    if(bestChemistryPointsLw < playerChemistryPoints) {
                        bestChemistryPointsLw = playerChemistryPoints;
                    }
                } else if(position == Player.Position.RW) {
                    if(bestChemistryPointsRw < playerChemistryPoints) {
                        bestChemistryPointsRw = playerChemistryPoints;
                    }
                } else if(position == Player.Position.LD) {
                    if(bestChemistryPointsLd < playerChemistryPoints) {
                        bestChemistryPointsLd = playerChemistryPoints;
                    }
                } else if(position == Player.Position.RD) {
                    if(bestChemistryPointsRd < playerChemistryPoints) {
                        bestChemistryPointsRd = playerChemistryPoints;
                    }
                }
            }

            centerBestOverallChemistry = bestChemistryPointsLw + bestChemistryPointsRw + bestChemistryPointsLd + bestChemistryPointsRd;
        }

        // TODO This is old code at the moment just to be reminder will not be used!
        for (Player center : centers) {
            for (Player player : listOfPlayers) {

                int bestChemistryPointsLw = 0;
                int bestChemistryPointsRw = 0;
                int bestChemistryPointsLd = 0;
                int bestChemistryPointsRd = 0;

                Player bestLeftWing = null;
                Player bestRightWing = null;
                Player bestLeftDefender = null;
                Player bestRightDefender = null;

                int playersChemistryPoints = getChemistryPoints(center.getPlayerId(), player.getPlayerId(), goals);

                Player.Position position = Player.Position.fromDatabaseName(player.getPosition());
                if(position == Player.Position.LW) {
                    if(bestChemistryPointsLw < playersChemistryPoints) {
                        bestChemistryPointsLw = playersChemistryPoints;
                        bestLeftWing = player.clone();
                    }
                } else if(position == Player.Position.RW) {
                    if(bestChemistryPointsRw < playersChemistryPoints) {
                        bestChemistryPointsRw = playersChemistryPoints;
                        bestRightWing = player.clone();
                    }
                } else if(position == Player.Position.LD) {
                    if(bestChemistryPointsLd < playersChemistryPoints) {
                        bestChemistryPointsLd = playersChemistryPoints;
                        bestLeftDefender = player.clone();
                    }
                } else if(position == Player.Position.RD) {
                    if(bestChemistryPointsRd < playersChemistryPoints) {
                        bestChemistryPointsRd = playersChemistryPoints;
                        bestRightDefender = player.clone();
                    }
                }

                // Remove players which are added to the line with the center so those are not taken notice
                // for the next center and new line
                listOfPlayers.remove(bestLeftWing);
                listOfPlayers.remove(bestRightWing);
                listOfPlayers.remove(bestLeftDefender);
                listOfPlayers.remove(bestRightDefender);
                centers.remove(center);

                // This shit is here just to be reminder and example
                Map<String, String> playerIdMap = new HashMap<>();
                playerIdMap.put(bestLeftWing.getPlayerId(), bestLeftWing.getPosition());
                playerIdMap.put(bestRightWing.getPlayerId(), bestRightWing.getPosition());
                playerIdMap.put(bestLeftDefender.getPlayerId(), bestLeftDefender.getPosition());
                playerIdMap.put(bestRightDefender.getPlayerId(), bestRightDefender.getPosition());

                Line newFirstLine = new Line();
                newFirstLine.setLineId("asd");
                newFirstLine.setLineNumber(1);
                newFirstLine.setPlayerIdMap(playerIdMap);

                listOfBestLines.put(1, newFirstLine);

            }
        }

        return listOfBestLines;
    }

    /**
     * @param line player chemistries from this line are calculated
     * @return chemistries list indexed by playerId
     */
    public static Map<Player.Position, ArrayList<Chemistry>> getLineChemistry(Line line, Map<String, ArrayList<Goal>> goals, Map<String, ArrayList<Line>> lines) {
        Map<Player.Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();
        ArrayList<Player> players = new ArrayList<>(AppRes.getInstance().getPlayers().values());

        if(line != null && line.getPlayerIdMap() != null) {
            Map<String, String> playersMap = line.getPlayerIdMap();
            for (Map.Entry<String, String> player : playersMap.entrySet()) {
                final String position = player.getKey();
                final String playerId = player.getValue();
                ArrayList<Chemistry> chemistries = new ArrayList<>();

                for (Map.Entry<String, String> comparePlayer : playersMap.entrySet()) {
                    String comparedPosition = comparePlayer.getKey();
                    String comparedPlayerId = comparePlayer.getValue();

                    if(!playerId.equals(comparedPlayerId)) {
                        Chemistry chemistry = new Chemistry();
                        chemistry.setPlayerId(playerId);
                        chemistry.setComparePlayerId(comparedPlayerId);
                        chemistry.setComparePosition(comparedPosition);
                        int percent = AnalyzerService.getChemistryPointsPercent(playerId, comparedPlayerId, players, goals, lines);
                        chemistry.setChemistryPoints(percent);

                        chemistries.add(chemistry);
                    }
                }

                chemistryMap.put(Player.Position.fromDatabaseName(position), chemistries);
            }
        }

        return chemistryMap;
    }

    /**
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param gameGoals indexed by gameId
     */
    public static double getChemistryPointsAvg(String playerId, String comparedPlayerId, Map<String, ArrayList<Goal>> gameGoals) {
        Map<String, Integer> chemistryMap = getChemistryPointsOfGames(playerId, comparedPlayerId, gameGoals);

        int size = chemistryMap.size();
        if(size == 0) {
            return 0.0;
        }

        double sum = 0.0;
        for (Map.Entry<String, Integer> entry : chemistryMap.entrySet()) {
            int points = entry.getValue();
            sum += points;
        }

        return sum / size;
    }

    /**
     * @param playerId player to compare against
     * @param comparedPlayerId player to compare
     * @param gameGoals indexed by gameId
     */
    public static Map<String, Integer> getChemistryPointsOfGames(String playerId, String comparedPlayerId, Map<String, ArrayList<Goal>> gameGoals) {
        Map<String, Integer> chemistryMap = new HashMap<>();

        for (Map.Entry<String, ArrayList<Goal>> entry : gameGoals.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Goal> goals = entry.getValue();
            int chemistryPoints = getChemistryPoints(playerId, comparedPlayerId, goals);
            chemistryMap.put(gameId, chemistryPoints);
        }

        return chemistryMap;
    }

    public static int getChemistryPointsPercent(String playerId, String comparePlayerId, ArrayList<Player> players, Map<String, ArrayList<Goal>> teamGoalsMap, Map<String, ArrayList<Line>> teamLinesMap) {
        int maxPoints = AnalyzerService.getMaxChemistryPoints(players, teamGoalsMap, teamLinesMap);
        int minPoints = AnalyzerService.getMinChemistryPoints(players, teamGoalsMap, teamLinesMap);
        ArrayList<Goal> goals = getGoalsWherePlayersInSameLine(playerId, comparePlayerId, teamGoalsMap, teamLinesMap);
        int points = getChemistryPoints(playerId, comparePlayerId, goals);

        return (int)Math.round((double)points / (maxPoints - minPoints) * 100);
    }

    public static int getMaxChemistryPoints(ArrayList<Player> players, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        int maxPoints = 0;
        for(Player player : players) {
            for(Player comparePlayer : players) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId(), linesMap);
                    ArrayList<Goal> goals = getGoalsOfGames(gameIds, gameGoals);
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(points > maxPoints) {
                        maxPoints = points;
                    }
                }
            }
        }
        return maxPoints;
    }

    public static int getMinChemistryPoints(ArrayList<Player> players, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        int minPoints = 999;
        for(Player player : players) {
            for(Player comparePlayer : players) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId(), linesMap);
                    ArrayList<Goal> goals = getGoalsOfGames(gameIds, gameGoals);
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(points < minPoints) {
                        minPoints = points;
                    }
                }
            }
        }
        return minPoints;
    }

    public static ArrayList<Goal> getGoalsWherePlayersInSameLine(String playerId, String comparePlayerId, Map<String, ArrayList<Goal>> gameGoals, Map<String, ArrayList<Line>> linesMap) {
        ArrayList<String> gameIds = getGameIdsWherePlayersInSameLine(playerId, comparePlayerId, linesMap);
        return getGoalsOfGames(gameIds, gameGoals);
    }

    public static ArrayList<String> getGameIdsWherePlayersInSameLine(String playerId, String comparedPlayerId, Map<String, ArrayList<Line>> linesMap) {
        List<String> comparePlayers = Arrays.asList(playerId, comparedPlayerId);
        ArrayList<String> gameIds = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Line>> entry : linesMap.entrySet()) {
            final String gameId = entry.getKey();
            final ArrayList<Line> lines = entry.getValue();
            for(Line line : lines) {
                if(line.getPlayerIdMap() != null && line.getPlayerIdMap().values().containsAll(comparePlayers)) {
                    gameIds.add(gameId);
                }
            }
        }
        return gameIds;
    }

    public static ArrayList<Goal> getGoalsOfGames(ArrayList<String> gameIds, Map<String, ArrayList<Goal>> gameGoals) {
        ArrayList<Goal> goals = new ArrayList<>();
        for(String gameId : gameIds) {
            ArrayList<Goal> foundGoals = gameGoals.get(gameId);
            if(foundGoals != null) {
                goals.addAll(foundGoals);
            }
        }
        return goals;
    }
}
