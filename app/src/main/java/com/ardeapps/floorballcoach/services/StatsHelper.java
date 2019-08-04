package com.ardeapps.floorballcoach.services;

import android.util.Pair;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.PlayerStatsObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsHelper extends AnalyzerService {

    public static PlayerStatsObject getPlayerStats(String playerId, Map<Game, ArrayList<Goal>> stats) {
        int gamesCount = stats.size();
        int points = 0;

        int scores = 0;
        int yvScores = 0;
        int avScores = 0;
        int rlScores = 0;

        int assists = 0;
        int yvAssists = 0;
        int avAssists = 0;
        int pluses = 0;
        int minuses = 0;
        int plusMinus = 0;

        Pair<Integer, Integer> bestStats = new Pair<>(0, 0);
        int bestPlusMinus = 0;
        int longestStats = 0;
        int currentLongestStats = 0;

        double pointsPerGame = 0.0;
        double scoresPerGame = 0.0;
        double assistsPerGame = 0.0;
        // Loop all games
        for (Map.Entry<Game, ArrayList<Goal>> entry : stats.entrySet()) {
            ArrayList<Goal> goals = entry.getValue();
            int scoresInGame = 0;
            int assistsInGame = 0;
            int plusMinusInGame = 0;
            boolean isStatsInGame = false;
            for(Goal goal : goals) {
                Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
                // Points
                if(playerId.equals(goal.getScorerId()) || playerId.equals(goal.getAssistantId())) {
                    points++;
                    isStatsInGame = true;
                }
                // Goals
                if(playerId.equals(goal.getScorerId())) {
                    scores++;
                    scoresInGame++;
                    if(mode == Goal.Mode.YV) {
                        yvScores++;
                    }
                    if(mode == Goal.Mode.AV) {
                        avScores++;
                    }
                    if(mode == Goal.Mode.RL) {
                        rlScores++;
                    }
                }
                // Assists
                if(playerId.equals(goal.getAssistantId())) {
                    assists++;
                    assistsInGame++;
                    if(mode == Goal.Mode.YV) {
                        yvAssists++;
                    }
                    if(mode == Goal.Mode.AV) {
                        avAssists++;
                    }
                }
                int plusMinuses = 0;
                // Plus minus
                if(goal.getPlayerIds().contains(playerId)) {
                    // Plus
                    if(!goal.isOpponentGoal() && !(Goal.Mode.YV == mode || Goal.Mode.RL == mode)) {
                        pluses++;
                        plusMinuses++;
                    }

                    // Minus
                    if(goal.isOpponentGoal() && !(Goal.Mode.AV == mode || Goal.Mode.RL == mode)) {
                        minuses++;
                        plusMinuses--;
                    }
                }
                plusMinus += plusMinuses;
                plusMinusInGame += plusMinuses;
            }

            // Longest stats
            if(isStatsInGame) {
                currentLongestStats++;
                if(currentLongestStats > longestStats) {
                    longestStats = currentLongestStats;
                }
            } else {
                currentLongestStats = 0;
            }

            // Best stats
            int currentBestStatsTotal = bestStats.first + bestStats.second;
            int newBestStats = scoresInGame + assistsInGame;
            if(newBestStats > currentBestStatsTotal) {
                bestStats = new Pair<>(scoresInGame, assistsInGame);
            } else if (newBestStats == currentBestStatsTotal && scoresInGame > bestStats.first) {
                bestStats = new Pair<>(scoresInGame, assistsInGame);
            }

            // Best +/-
            if(plusMinusInGame > bestPlusMinus) {
                bestPlusMinus = plusMinusInGame;
            }
        }

        if(gamesCount > 0) {
            pointsPerGame = (double)points / gamesCount;
            scoresPerGame = (double)scores / gamesCount;
            assistsPerGame = (double)assists / gamesCount;
        }

        ArrayList<Goal> allGoals = new ArrayList<>();
        for(ArrayList<Goal> goals : stats.values()) {
            allGoals.addAll(goals);
        }
        // Best assistants
        Pair<ArrayList<String>, Integer> bestAssists = getBestAssistants(allGoals, playerId);
        // Best scorers
        Pair<ArrayList<String>, Integer> bestScorers = getBestScorers(allGoals, playerId);
        // Best line mates
        Pair<ArrayList<String>, Integer> bestLineMates = getBestLineMate(allGoals, playerId);

        PlayerStatsObject result = new PlayerStatsObject();
        result.gamesCount = gamesCount;
        result.points = points;
        result.pointsPerGame = pointsPerGame;
        result.pluses = pluses;
        result.minuses = minuses;
        result.plusMinus = plusMinus;
        result.scores = scores;
        result.scoresPerGame = scoresPerGame;
        result.yvScores = yvScores;
        result.avScores = avScores;
        result.rlScores = rlScores;
        result.assists = assists;
        result.assistsPerGame = assistsPerGame;
        result.yvAssists = yvAssists;
        result.avAssists = avAssists;
        result.bestStats = bestStats;
        result.longestStats = longestStats;
        result.bestPlusMinus = bestPlusMinus;
        result.bestAssists = bestAssists;
        result.bestScorers = bestScorers;
        result.bestLineMates = bestLineMates;

        return result;
    }

    public static Pair<ArrayList<String>, Integer> getBestLineMate(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        HashMap<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            if(goal.getPlayerIds().contains(playerId)) {
                for(String compareId : goal.getPlayerIds()) {
                    if(!compareId.equals(playerId)) {
                        Integer stats = players.get(compareId);
                        players.put(compareId, stats != null ? ++stats : 1);
                    }
                }
            }
        }

        // Get most goals
        int mostStatsTogether = 0;
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer stats = entry.getValue();
            if(stats > mostStatsTogether) {
                mostStatsTogether = stats;
            }
        }

        ArrayList<String> playerIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String statPlayerId = entry.getKey();
            Integer stats = entry.getValue();

            if(stats == mostStatsTogether) {
                playerIds.add(statPlayerId);
            }
        }

        return new Pair<>(playerIds, mostStatsTogether);
    }

    public static Pair<ArrayList<String>, Integer> getBestAssistants(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        HashMap<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            if(playerId.equals(goal.getScorerId())) {
                String assistantId = goal.getAssistantId();
                Integer assists = players.get(assistantId);
                players.put(assistantId, assists != null ? ++assists : 1);
            }
        }

        // Get highest assist
        int highestAssist = 0;
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer assists = entry.getValue();
            if(assists > highestAssist) {
                highestAssist = assists;
            }
        }

        ArrayList<String> assistantIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String assistantId = entry.getKey();
            Integer assists = entry.getValue();

            if(assists == highestAssist) {
                assistantIds.add(assistantId);
            }
        }

        return new Pair<>(assistantIds, highestAssist);
    }

    public static Pair<ArrayList<String>, Integer> getBestScorers(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        HashMap<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            if(playerId.equals(goal.getAssistantId())) {
                String scorerId = goal.getScorerId();
                Integer scores = players.get(scorerId);
                players.put(scorerId, scores != null ? ++scores : 1);
            }
        }

        // Get highest score
        int highestScores = 0;
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer scores = entry.getValue();
            if(scores > highestScores) {
                highestScores = scores;
            }
        }

        // Get scorers
        ArrayList<String> scorerIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String scorerId = entry.getKey();
            Integer scores = entry.getValue();

            if(scores == highestScores) {
                scorerIds.add(scorerId);
            }
        }

        return new Pair<>(scorerIds, highestScores);
    }
}
