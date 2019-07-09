package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;

import java.util.ArrayList;
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

            List<String> goalPlayerIds = goal.getPlayerIds();

            String scorerId = goal.getScorerId();
            String assistantId = goal.getAssistantId();

            boolean playersInSameLine = goalPlayerIds != null && goalPlayerIds.contains(playerId) && goalPlayerIds.contains(comparedPlayerId);

            if(goal.isOpponentGoal() && playersInSameLine) {
                chemistryPoints--;
            } else if(!goal.isOpponentGoal() && (playerId.equals(scorerId) && comparedPlayerId.equals(assistantId) || playerId.equals(assistantId) && comparedPlayerId.equals(scorerId))) {
                chemistryPoints += 3;
            } else if(!goal.isOpponentGoal() && (playerId.equals(scorerId) || comparedPlayerId.equals(scorerId) || playerId.equals(assistantId) || comparedPlayerId.equals(assistantId))) {
                chemistryPoints += 2;
            } else if(!goal.isOpponentGoal() && playersInSameLine) {
                chemistryPoints++;
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
        String centerPosition = "C";

        ArrayList<Chemistry> playerChemistries = getPlayerChemistries(players, goals);

        for (Player player : players) {
            if(player.getPosition().equals(centerPosition)) {
                centers.add(player);
            }
        }

        return null;
    }

    /**
     * @param line player chemistries from this line are calculated
     * @param goals team goals where chemistry is calculated
     * @return chemistries list indexed by playerId
     *
     */
    public static Map<Player.Position, ArrayList<Chemistry>> getLineChemistry(Line line, ArrayList<Goal> goals) {
        Map<Player.Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

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
                        int chemistryPoints = getChemistryPoints(playerId, comparedPlayerId, goals);
                        chemistry.setChemistryPoints(chemistryPoints);

                        chemistries.add(chemistry);
                    }
                }

                chemistryMap.put(Player.Position.fromDatabaseName(position), chemistries);
            }
        }

        return chemistryMap;
    }
}
