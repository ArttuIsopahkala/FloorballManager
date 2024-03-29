package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.analyzer.AnalyzerService;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.viewObjects.ExtPlayerStatsData;
import com.ardeapps.floorballmanager.viewObjects.PlayerPenaltiesData;
import com.ardeapps.floorballmanager.viewObjects.PlayerPointsData;
import com.ardeapps.floorballmanager.viewObjects.PlayerStatsData;
import com.ardeapps.floorballmanager.viewObjects.TeamGameData;
import com.ardeapps.floorballmanager.viewObjects.TeamStatsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatsHelper extends AnalyzerService {

    public static PlayerStatsData getPlayerStats(String playerId, int gamesCount, ArrayList<Goal> stats) {
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

        for (Goal goal : stats) {
            Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
            // Points
            if (playerId.equals(goal.getScorerId()) || playerId.equals(goal.getAssistantId())) {
                points++;
            }
            // Goals
            if (playerId.equals(goal.getScorerId())) {
                scores++;
                if (mode == Goal.Mode.YV) {
                    yvScores++;
                }
                if (mode == Goal.Mode.AV) {
                    avScores++;
                }
                if (mode == Goal.Mode.RL) {
                    rlScores++;
                }
            }
            // Assists
            if (playerId.equals(goal.getAssistantId())) {
                assists++;
                if (mode == Goal.Mode.YV) {
                    yvAssists++;
                }
                if (mode == Goal.Mode.AV) {
                    avAssists++;
                }
            }
            int plusMinuses = 0;
            // Plus minus
            if (goal.getPlayerIds().contains(playerId)) {
                // Plus
                if (!goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                    pluses++;
                    plusMinuses++;
                }

                // Minus
                if (goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                    minuses++;
                    plusMinuses--;
                }
            }
            plusMinus += plusMinuses;
        }

        double pointsPerGame = 0.0;
        double scoresPerGame = 0.0;
        double assistsPerGame = 0.0;

        if (gamesCount > 0) {
            pointsPerGame = (double) points / gamesCount;
            scoresPerGame = (double) scores / gamesCount;
            assistsPerGame = (double) assists / gamesCount;
        }

        PlayerStatsData result = new PlayerStatsData();
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
        return result;
    }

    public static PlayerPenaltiesData getPlayerPenaltiesData(String playerId, int gamesCount, ArrayList<Penalty> penalties) {

        // Penalties
        int penalties2min = 0;
        int penalties5min = 0;
        int penalties10min = 0;
        int penalties20min = 0;
        int penaltyMinutes = 0;
        for(Penalty penalty : penalties) {
            if(playerId.equals(penalty.getPlayerId())) {
                penaltyMinutes += penalty.getLength();
                if(penalty.getLength() == 2) {
                    penalties2min++;
                } else if(penalty.getLength() == 5) {
                    penalties5min++;
                } else if(penalty.getLength() == 10) {
                    penalties10min++;
                } else if(penalty.getLength() == 20) {
                    penalties20min++;
                }
            }
        }

        double penaltiesPerGame = 0.0;

        if (gamesCount > 0) {
            penaltiesPerGame = (double) penaltyMinutes / gamesCount;
        }

        PlayerPenaltiesData result = new PlayerPenaltiesData();
        result.gamesCount = gamesCount;
        result.penalties2min = penalties2min;
        result.penalties5min = penalties5min;
        result.penalties10min = penalties10min;
        result.penalties20min = penalties20min;
        result.penalties = penaltyMinutes;
        result.penaltiesPerGame = penaltiesPerGame;
        return result;
    }

    public static ExtPlayerStatsData getExtPlayerStats(String playerId, Map<Game, ArrayList<Goal>> stats) {
        int gamesCount = stats.size();
        ArrayList<Goal> allGoals = new ArrayList<>();
        for (ArrayList<Goal> goals : stats.values()) {
            allGoals.addAll(goals);
        }
        // Best assistants
        Pair<ArrayList<String>, Integer> bestAssists = getBestAssistants(allGoals, playerId);
        // Best scorers
        Pair<ArrayList<String>, Integer> bestScorers = getBestScorers(allGoals, playerId);
        // Best line mates
        Pair<ArrayList<String>, Integer> bestLineMates = getBestLineMate(allGoals, playerId);

        PlayerStatsData statsData = getPlayerStats(playerId, gamesCount, allGoals);

        Pair<Integer, Integer> bestStats = new Pair<>(0, 0);
        int bestPlusMinus = 0;
        int longestStats = 0;
        int currentLongestStats = 0;

        // Loop all games
        for (Map.Entry<Game, ArrayList<Goal>> entry : stats.entrySet()) {
            ArrayList<Goal> goals = entry.getValue();
            int scoresInGame = 0;
            int assistsInGame = 0;
            int plusMinusInGame = 0;
            boolean isStatsInGame = false;
            for (Goal goal : goals) {
                Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
                // Points
                if (playerId.equals(goal.getScorerId()) || playerId.equals(goal.getAssistantId())) {
                    isStatsInGame = true;
                }
                // Goals
                if (playerId.equals(goal.getScorerId())) {
                    scoresInGame++;
                }
                // Assists
                if (playerId.equals(goal.getAssistantId())) {
                    assistsInGame++;
                }
                int plusMinuses = 0;
                // Plus minus
                if (goal.getPlayerIds().contains(playerId)) {
                    // Plus
                    if (!goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                        plusMinuses++;
                    }

                    // Minus
                    if (goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                        plusMinuses--;
                    }
                }
                plusMinusInGame += plusMinuses;
            }

            // Longest stats
            if (isStatsInGame) {
                currentLongestStats++;
                if (currentLongestStats > longestStats) {
                    longestStats = currentLongestStats;
                }
            } else {
                currentLongestStats = 0;
            }

            // Best stats
            int currentBestStatsTotal = bestStats.first + bestStats.second;
            int newBestStats = scoresInGame + assistsInGame;
            if (newBestStats > currentBestStatsTotal) {
                bestStats = new Pair<>(scoresInGame, assistsInGame);
            } else if (newBestStats == currentBestStatsTotal && scoresInGame > bestStats.first) {
                bestStats = new Pair<>(scoresInGame, assistsInGame);
            }

            // Best +/-
            if (plusMinusInGame > bestPlusMinus) {
                bestPlusMinus = plusMinusInGame;
            }
        }

        ExtPlayerStatsData result = new ExtPlayerStatsData(statsData);
        result.bestStats = bestStats;
        result.longestStats = longestStats;
        result.bestPlusMinus = bestPlusMinus;
        result.bestAssists = bestAssists;
        result.bestScorers = bestScorers;
        result.bestLineMates = bestLineMates;
        return result;
    }

    public static TeamStatsData getTeamStats(Map<Game, ArrayList<Goal>> stats) {

        int gamesCount = stats.size();
        int wins = 0;
        int draws = 0;
        int loses = 0;
        int plusGoals = 0;
        int plusGoalsYv = 0;
        int plusGoalsAv = 0;
        int plusGoalsRl = 0;
        int minusGoals = 0;
        int minusGoalsYv = 0;
        int minusGoalsAv = 0;
        int minusGoalsRl = 0;
        TeamGameData biggestWin = null;
        TeamGameData biggestLose = null;
        int longestWin = 0;
        int longestNotLose = 0;

        // Helper variables
        int plusGoalsPeriod1 = 0;
        int plusGoalsPeriod2 = 0;
        int plusGoalsPeriod3 = 0;
        int plusGoalsPeriodJA = 0;
        int minusGoalsPeriod1 = 0;
        int minusGoalsPeriod2 = 0;
        int minusGoalsPeriod3 = 0;
        int minusGoalsPeriodJA = 0;
        int winGoalDiff = 0;
        int loseGoalDiff = 0;
        int currentWinCount = 0;
        int currentNotLoseCount = 0;

        // Loop all games
        for (Map.Entry<Game, ArrayList<Goal>> entry : stats.entrySet()) {
            Game game = entry.getKey();
            ArrayList<Goal> goals = entry.getValue();

            int homeGoals = game.getHomeGoals() != null ? game.getHomeGoals() : 0;
            int awayGoals = game.getAwayGoals() != null ? game.getAwayGoals() : 0;

            int goalDiff;
            if (game.isHomeGame()) {
                goalDiff = homeGoals - awayGoals;
                plusGoals += homeGoals;
                minusGoals += awayGoals;
                if (homeGoals > awayGoals) {
                    wins++;
                    currentWinCount++;
                    currentNotLoseCount++;
                } else if (homeGoals < awayGoals) {
                    loses++;
                    currentWinCount = 0;
                    currentNotLoseCount = 0;
                } else {
                    draws++;
                    currentNotLoseCount++;
                }
            } else {
                goalDiff = awayGoals - homeGoals;
                plusGoals += awayGoals;
                minusGoals += homeGoals;
                if (homeGoals < awayGoals) {
                    wins++;
                    currentWinCount++;
                    currentNotLoseCount++;
                } else if (homeGoals > awayGoals) {
                    loses++;
                    currentWinCount = 0;
                    currentNotLoseCount = 0;
                } else {
                    draws++;
                    currentNotLoseCount++;
                }
            }

            if (currentWinCount > longestWin) {
                longestWin = currentWinCount;
            }
            if (currentNotLoseCount > longestNotLose) {
                longestNotLose = currentNotLoseCount;
            }

            if (goalDiff > winGoalDiff) {
                biggestWin = new TeamGameData();
                biggestWin.homeGoals = homeGoals;
                biggestWin.awayGoals = awayGoals;
                biggestWin.opponentName = game.getOpponentName();
                winGoalDiff = goalDiff;
            }
            if (goalDiff < loseGoalDiff) {
                biggestLose = new TeamGameData();
                biggestLose.homeGoals = homeGoals;
                biggestLose.awayGoals = awayGoals;
                biggestLose.opponentName = game.getOpponentName();
                loseGoalDiff = goalDiff;
            }

            long firstPeriodEnd = TimeUnit.MINUTES.toMillis(game.getPeriodInMinutes());
            long secondPeriodEnd = firstPeriodEnd * 2;
            long thirdPeriodEnd = firstPeriodEnd * 3;

            // LOOP GOALS
            for (Goal goal : goals) {
                Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
                if (goal.isOpponentGoal()) {
                    // MINUS GOALS
                    if (goal.getTime() < firstPeriodEnd) {
                        minusGoalsPeriod1++;
                    } else if (goal.getTime() >= firstPeriodEnd && goal.getTime() < secondPeriodEnd) {
                        minusGoalsPeriod2++;
                    } else if (goal.getTime() >= secondPeriodEnd && goal.getTime() < thirdPeriodEnd) {
                        minusGoalsPeriod3++;
                    } else {
                        minusGoalsPeriodJA++;
                    }

                    if (mode == Goal.Mode.YV) {
                        minusGoalsYv++;
                    }
                    if (mode == Goal.Mode.AV) {
                        minusGoalsAv++;
                    }
                    if (mode == Goal.Mode.RL) {
                        minusGoalsRl++;
                    }
                } else {
                    // PLUS GOALS
                    if (goal.getTime() < firstPeriodEnd) {
                        plusGoalsPeriod1++;
                    } else if (goal.getTime() >= firstPeriodEnd && goal.getTime() < secondPeriodEnd) {
                        plusGoalsPeriod2++;
                    } else if (goal.getTime() >= secondPeriodEnd && goal.getTime() < thirdPeriodEnd) {
                        plusGoalsPeriod3++;
                    } else {
                        plusGoalsPeriodJA++;
                    }

                    if (mode == Goal.Mode.YV) {
                        plusGoalsYv++;
                    }
                    if (mode == Goal.Mode.AV) {
                        plusGoalsAv++;
                    }
                    if (mode == Goal.Mode.RL) {
                        plusGoalsRl++;
                    }
                }
            }
        }

        int winPercent = 0;
        int drawPercent = 0;
        int losePercent = 0;
        int plusGoalsPercentPeriod1 = 0;
        int plusGoalsPercentPeriod2 = 0;
        int plusGoalsPercentPeriod3 = 0;
        int plusGoalsPercentPeriodJA = 0;
        int minusGoalsPercentPeriod1 = 0;
        int minusGoalsPercentPeriod2 = 0;
        int minusGoalsPercentPeriod3 = 0;
        int minusGoalsPercentPeriodJA = 0;
        double plusGoalsPerGame = 0.0;
        double minusGoalsPerGame = 0.0;

        if (gamesCount > 0) {
            winPercent = (int) Math.round((double) wins / gamesCount * 100);
            drawPercent = (int) Math.round((double) draws / gamesCount * 100);
            losePercent = (int) Math.round((double) loses / gamesCount * 100);
            plusGoalsPerGame = (double) plusGoals / gamesCount;
            minusGoalsPerGame = (double) minusGoals / gamesCount;
            plusGoalsPercentPeriod1 = (int) Math.round((double) plusGoalsPeriod1 / plusGoals * 100);
            plusGoalsPercentPeriod2 = (int) Math.round((double) plusGoalsPeriod2 / plusGoals * 100);
            plusGoalsPercentPeriod3 = (int) Math.round((double) plusGoalsPeriod3 / plusGoals * 100);
            plusGoalsPercentPeriodJA = (int) Math.round((double) plusGoalsPeriodJA / plusGoals * 100);
            minusGoalsPercentPeriod1 = (int) Math.round((double) minusGoalsPeriod1 / minusGoals * 100);
            minusGoalsPercentPeriod2 = (int) Math.round((double) minusGoalsPeriod2 / minusGoals * 100);
            minusGoalsPercentPeriod3 = (int) Math.round((double) minusGoalsPeriod3 / minusGoals * 100);
            minusGoalsPercentPeriodJA = (int) Math.round((double) minusGoalsPeriodJA / minusGoals * 100);
        }

        TeamStatsData result = new TeamStatsData();
        result.gamesCount = gamesCount;
        result.wins = wins;
        result.draws = draws;
        result.loses = loses;
        result.plusGoals = plusGoals;
        result.plusGoalsYv = plusGoalsYv;
        result.plusGoalsAv = plusGoalsAv;
        result.plusGoalsRl = plusGoalsRl;
        result.minusGoals = minusGoals;
        result.minusGoalsYv = minusGoalsYv;
        result.minusGoalsAv = minusGoalsAv;
        result.minusGoalsRl = minusGoalsRl;
        result.winPercent = winPercent;
        result.drawPercent = drawPercent;
        result.losePercent = losePercent;
        result.plusGoalsPerGame = plusGoalsPerGame;
        result.minusGoalsPerGame = minusGoalsPerGame;
        result.plusGoalsPercentPeriod1 = plusGoalsPercentPeriod1;
        result.plusGoalsPercentPeriod2 = plusGoalsPercentPeriod2;
        result.plusGoalsPercentPeriod3 = plusGoalsPercentPeriod3;
        result.plusGoalsPercentPeriodJA = plusGoalsPercentPeriodJA;
        result.minusGoalsPercentPeriod1 = minusGoalsPercentPeriod1;
        result.minusGoalsPercentPeriod2 = minusGoalsPercentPeriod2;
        result.minusGoalsPercentPeriod3 = minusGoalsPercentPeriod3;
        result.minusGoalsPercentPeriodJA = minusGoalsPercentPeriodJA;
        result.biggestWin = biggestWin;
        result.biggestLose = biggestLose;
        result.longestWin = longestWin;
        result.longestNotLose = longestNotLose;

        return result;
    }

    public static ArrayList<PlayerPointsData> getSortedTrendingPlayers(ArrayList<Goal> goalsInThreeLastGames) {
        Map<String, PlayerPointsData> playerPointsMap = new HashMap<>();

        // Collect points
        for (Goal goal : goalsInThreeLastGames) {
            if (goal.getScorerId() != null) {
                PlayerPointsData playerPointsData = playerPointsMap.get(goal.getScorerId());
                if (playerPointsData == null) {
                    playerPointsData = new PlayerPointsData();
                    playerPointsData.playerId = goal.getScorerId();
                }
                playerPointsData.goals++;
                playerPointsMap.put(goal.getScorerId(), playerPointsData);
            }
            if (goal.getAssistantId() != null) {
                PlayerPointsData playerPointsData = playerPointsMap.get(goal.getAssistantId());
                if (playerPointsData == null) {
                    playerPointsData = new PlayerPointsData();
                    playerPointsData.playerId = goal.getAssistantId();
                }
                playerPointsData.assists++;
                playerPointsMap.put(goal.getAssistantId(), playerPointsData);
            }
        }
        ArrayList<PlayerPointsData> playerPoints = new ArrayList<>(playerPointsMap.values());
        Collections.sort(playerPoints, (o1, o2) -> {
            int points1 = o1.goals + o1.assists;
            int points2 = o2.goals + o2.assists;
            if (points1 == points2) {
                return o2.goals - o1.goals;
            } else {
                return points2 - points1;
            }
        });
        return playerPoints;
    }

    private static Pair<ArrayList<String>, Integer> getBestLineMate(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        HashMap<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            if (goal.getPlayerIds().contains(playerId)) {
                for (String compareId : goal.getPlayerIds()) {
                    if (!compareId.equals(playerId)) {
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
            if (stats > mostStatsTogether) {
                mostStatsTogether = stats;
            }
        }

        ArrayList<String> playerIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String statPlayerId = entry.getKey();
            Integer stats = entry.getValue();

            if (stats == mostStatsTogether) {
                playerIds.add(statPlayerId);
            }
        }

        return new Pair<>(playerIds, mostStatsTogether);
    }

    private static Pair<ArrayList<String>, Integer> getBestAssistants(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        Map<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            String assistantId = goal.getAssistantId();
            if (assistantId != null && playerId.equals(goal.getScorerId())) {
                Integer assists = players.get(assistantId);
                players.put(assistantId, assists != null ? ++assists : 1);
            }
        }

        // Get highest assist
        int highestAssist = 0;
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer assists = entry.getValue();
            if (assists > highestAssist) {
                highestAssist = assists;
            }
        }

        ArrayList<String> assistantIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String assistantId = entry.getKey();
            Integer assists = entry.getValue();

            if (assists == highestAssist) {
                assistantIds.add(assistantId);
            }
        }

        return new Pair<>(assistantIds, highestAssist);
    }

    private static Pair<ArrayList<String>, Integer> getBestScorers(ArrayList<Goal> goals, String playerId) {
        // Collect assists
        Map<String, Integer> players = new HashMap<>();
        for (Goal goal : goals) {
            String scorerId = goal.getScorerId();
            if (scorerId != null && playerId.equals(goal.getAssistantId())) {
                Integer scores = players.get(scorerId);
                players.put(scorerId, scores != null ? ++scores : 1);
            }
        }

        // Get highest score
        int highestScores = 0;
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            Integer scores = entry.getValue();
            if (scores > highestScores) {
                highestScores = scores;
            }
        }

        // Get scorers
        ArrayList<String> scorerIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : players.entrySet()) {
            String scorerId = entry.getKey();
            Integer scores = entry.getValue();

            if (scores == highestScores) {
                scorerIds.add(scorerId);
            }
        }

        return new Pair<>(scorerIds, highestScores);
    }
}
