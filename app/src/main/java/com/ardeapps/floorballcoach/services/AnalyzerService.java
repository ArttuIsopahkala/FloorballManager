package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.PlayerChemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerService {

    /**
     * Database path: goalsTeamGame
     * @param player Player object to compare against
     * @param comparedPlayer Player object to compare
     * @param goals goals saved for team
     * @return count of chemistry points
     */
    public static int getChemistryPoints(Player player, Player comparedPlayer, ArrayList<Goal> goals) {

        int chemistryPoints = 0;

        // +3 = if playerId has scored and compareId assisted
        // +2 = if playerId has assisted and compareId scored
        // +1 = if compareId and playerId has been on the field when goal happened
        for(Goal goal : goals) {

            List<String> goalPlayerIds = goal.getPlayerIds();

            String playerId = player.getPlayerId();
            String comparedPlayerId = comparedPlayer.getPlayerId();
            String scorerId = goal.getScorerId();
            String assistantId = goal.getAssistantId();

            boolean playersInSameLine = goalPlayerIds != null && goalPlayerIds.contains(playerId) && goalPlayerIds.contains(comparedPlayerId);

            if(goal.isOpponentGoal() && playersInSameLine) {
                chemistryPoints--;
            } else if(!goal.isOpponentGoal() && playerId.equals(scorerId) && comparedPlayerId.equals(assistantId)) {
                chemistryPoints += 3;
            } else if(!goal.isOpponentGoal() && comparedPlayerId.equals(scorerId) && playerId.equals(assistantId)) {
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
    public static ArrayList<PlayerChemistry> getPlayerChemistries(ArrayList<Player> players, ArrayList<Goal> goals) {

        ArrayList<PlayerChemistry> playerChemistryList = new ArrayList<>();

        for(Player player : players) {
            PlayerChemistry playerChemistry = new PlayerChemistry();
            playerChemistry.setPlayerId(player.getPlayerId());
            for(Player comparePlayer : players) {
                if(!comparePlayer.getPlayerId().equals(player.getPlayerId())) {
                    int chemistryPoints = getChemistryPoints(player, comparePlayer, goals);
                    playerChemistry.getComparePlayers().put(comparePlayer.getPlayerId(), chemistryPoints);
                }
            }

            playerChemistryList.add(playerChemistry);
        }

        return playerChemistryList;
    }

    /**
     * Database path: goalsTeamGame
     * @param playerIdMap players which chemistries are calculated
     * @param goals team goals where chemistry is calculated
     * @return List on Lines of Players with best chemistries (Integer = Line number)
     *
     * This method should get List of Players and return Lines where are separated players in the way that
     * best chemistries are taken in notice between players and their positions
     *
     */
    public static Map<Integer, Line> getBestPlayerChemistries(Map<String, String> playerIdMap, ArrayList<Goal> goals) {
        // TODO tee loppuun ja testaa
        // playerIdMap:
        // key = position(Player.Position), value = playerId
        Map<Integer, Line> lines = new HashMap<>();

        return null;
    }
}
