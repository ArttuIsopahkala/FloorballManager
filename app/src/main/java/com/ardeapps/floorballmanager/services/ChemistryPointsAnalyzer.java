package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;
import com.ardeapps.floorballmanager.objects.Player.Shoots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChemistryPointsAnalyzer extends AnalyzerService {

    private static final ArrayList<Player.Skill> attackSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.PASSING, Player.Skill.GAME_SENSE));
    private static final ArrayList<Player.Skill> attackSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING, Player.Skill.BALL_HANDLING));
    private static final ArrayList<Player.Skill> attackSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.SPEED, Player.Skill.PHYSICALITY, Player.Skill.BALL_PROTECTION));

    private static final ArrayList<Player.Skill> defenceSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING, Player.Skill.PHYSICALITY));
    private static final ArrayList<Player.Skill> defenceSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.GAME_SENSE, Player.Skill.BALL_PROTECTION, Player.Skill.PASSING));
    private static final ArrayList<Player.Skill> defenceSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.BLOCKING, Player.Skill.INTERCEPTION));

    /**
     * Get chemistry points of two players based on all abilities
     *
     * @param player1 playerId and position to compare
     * @param player2 playerId and position to compare against
     * @return count of chemistry points
     */
    protected static double getChemistryPoints(Pair<Position, String> player1, Pair<Position, String> player2) {
        Position playerPos = player1.first;
        String playerId = player1.second;
        Position comparePlayerPos = player2.first;
        String comparePlayerId = player2.second;
        
        double chemistryPoints = 0.0;
        Player player = playersInTeam.get(playerId);
        Player comparePlayer = playersInTeam.get(comparePlayerId);
        if (player != null && comparePlayer != null) {
            int gameCount = AnalyzerHelper.getGameCountWherePlayersInSameLine(playerId, comparePlayerId);
            // Divide by games to get relative points. (More points in a few games -> higher chemistry)
            int goalChemistry = 0;
            if(gameCount > 0) {
                ArrayList<Goal> commonGoals = AnalyzerHelper.getCommonGoals(playerId, comparePlayerId);
                goalChemistry = getGoalsChemistry(playerId, comparePlayerId, commonGoals) / gameCount;
            }

            Shoots playerShoots = Shoots.fromDatabaseName(player.getShoots());
            Shoots comparePlayerShoots = Shoots.fromDatabaseName(comparePlayer.getShoots());
            Pair<Position, Shoots> shootsPlayer = new Pair<>(playerPos, playerShoots);
            Pair<Position, Shoots> shootsComparePlayer = new Pair<>(comparePlayerPos, comparePlayerShoots);
            int shootsChemistry = getShootsChemistry(shootsPlayer, shootsComparePlayer);
            shootsChemistry += getShootsChemistry(shootsComparePlayer, shootsPlayer);

            Pair<Position, Player> strengthsPlayer = new Pair<>(playerPos, player);
            Pair<Position, Player> strengthsComparePlayer = new Pair<>(playerPos, player);
            int strengthsChemistry = getStrengthsChemistry(strengthsPlayer, strengthsComparePlayer);
            strengthsChemistry += getStrengthsChemistry(strengthsComparePlayer, strengthsPlayer);

            chemistryPoints = getWeightedChemistryPoints(goalChemistry, shootsChemistry, strengthsChemistry);
        }

        if(chemistryPoints < 0) {
            chemistryPoints = 0;
        }
        return chemistryPoints;
    }

    /**
     * @param goalChemistry goal chemistryPoints
     * @param shootsChemistry shoots chemistry points
     * @param strengthsChemistry strengths chemistry points
     * @return weighted chemistry points
     */
    protected static double getWeightedChemistryPoints(int goalChemistry, int shootsChemistry, int strengthsChemistry) {
        double chemistrySum;
        double strengthsMax;
        double shootsMax;
        if(goalChemistry > 0) {
            strengthsMax = goalChemistry * 0.2;
            shootsMax = goalChemistry * 0.1;
        } else {
            strengthsMax = 2.0;
            shootsMax = 1.0;
        }
        double weightedStrengthsChemistry = strengthsChemistry / 2.0 * strengthsMax;
        double weightedShootsChemistry = shootsChemistry / 2.0 * shootsMax;
        chemistrySum = goalChemistry + weightedStrengthsChemistry + weightedShootsChemistry;

        return chemistrySum;
    }

    /**
     * Get chemistry of given goals
     * +3 = if playerId has scored and compareId assisted
     * +2 = if playerId has assisted and compareId scored
     * +1 = if compareId and playerId has been on the field when goal happened
     * -1 = if playerId and compareId has been on the field when opponent has scored
     *
     * @param playerId1 player to compare
     * @param playerId2 player to compare against
     * @param goals     team goals where chemistry is calculated
     * @return chemistry count
     */
    protected static int getGoalsChemistry(String playerId1, String playerId2, ArrayList<Goal> goals) {
        int chemistryPoints = 0;
        for (Goal goal : goals) {
            List<String> comparePlayers = Arrays.asList(playerId1, playerId2);
            boolean playersOnField = goal.getPlayerIds().containsAll(comparePlayers);
            if(playersOnField) {
                List<String> scorerAndAssistant = Arrays.asList(goal.getScorerId(), goal.getAssistantId());
                if (goal.isOpponentGoal()) {
                    chemistryPoints--;
                } else if (!goal.isOpponentGoal() && scorerAndAssistant.containsAll(comparePlayers)) {
                    chemistryPoints += 3;
                } else if (!goal.isOpponentGoal() && (scorerAndAssistant.contains(playerId1) || scorerAndAssistant.contains(playerId2))) {
                    chemistryPoints += 2;
                } else if (!goal.isOpponentGoal()) {
                    chemistryPoints++;
                }
                // TODO lokita kuinka Kustilla on niin  isot pisteet? bugi?
                /*if(playerId1.equals("-LlhMkH_blh3-pxXf_7N") && playerId2.equals("-LlgUzUYqGM-mCnwn7NP")) {
                    Logger.log("Kustin ja Mikin maalit: " + chemistryPoints);
                }
                if(playerId2.equals("-LlhMkH_blh3-pxXf_7N") && playerId1.equals("-LlgUzUYqGM-mCnwn7NP")) {
                    Logger.log("Kustin ja Mikin maalit: " + chemistryPoints);
                }*/
            }
        }
        return chemistryPoints;
    }

    protected static int getGoalPointsForPlayer(String playerId, ArrayList<Goal> goals) {
        int chemistryPoints = 0;
        for (Goal goal : goals) {
            boolean playerOnField = goal.getPlayerIds().contains(playerId);
            if(playerOnField) {
                List<String> scorerAndAssistant = Arrays.asList(goal.getScorerId(), goal.getAssistantId());
                if (goal.isOpponentGoal()) {
                    chemistryPoints--;
                } else if (!goal.isOpponentGoal() && scorerAndAssistant.contains(playerId)) {
                    chemistryPoints += 3;
                } else if (!goal.isOpponentGoal() && (scorerAndAssistant.contains(playerId) || scorerAndAssistant.contains(playerId))) {
                    chemistryPoints += 2;
                } else if (!goal.isOpponentGoal()) {
                    chemistryPoints++;
                }
            }
        }
        return chemistryPoints;
    }

    /**
     * Get chemistry based on player shoots (left or right)
     *
     * @param player1 player position and shoot to compare
     * @param player2 player position and shoot to compare against
     * @return chemistry count
     */
    protected static int getShootsChemistry(Pair<Position, Shoots> player1, Pair<Position, Shoots> player2) {
        Position playerPos = player1.first;
        Shoots playerShoots = player1.second;
        Position comparePlayerPos = player2.first;
        Shoots comparePlayerShoots = player2.second;
        
        int chemistryPoints = 0;
        // VASEN HYÖKKÄÄJÄ
        if (playerPos == Position.LW && playerShoots == Shoots.LEFT) {
            if (comparePlayerPos == Position.C && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LW && playerShoots == Shoots.RIGHT) {
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            // CENTTERI
        } else if (playerPos == Position.C && playerShoots == Shoots.LEFT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.C && playerShoots == Shoots.RIGHT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
            // OIKEA HYÖKKÄÄJÄ
        } else if (playerPos == Position.RW && playerShoots == Shoots.LEFT) {
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RW && playerShoots == Shoots.RIGHT) {
            if (comparePlayerPos == Position.C && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            // VASEN PAKKI
        } else if (playerPos == Position.LD && playerShoots == Shoots.LEFT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LD && playerShoots == Shoots.RIGHT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
            // OIKEA PAKKI
        } else if (playerPos == Position.RD && playerShoots == Shoots.LEFT) {
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RD && playerShoots == Shoots.RIGHT) {
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Shoots.LEFT) {
                chemistryPoints++;
            }
        }
        return chemistryPoints;
    }

    /**
     * Get chemistry based on player strengths
     *
     * @param player1 player and position to compare
     * @param player2 player and position to compare against
     * @return chemistry count
     */
    protected static int getStrengthsChemistry(Pair<Position, Player> player1, Pair<Position, Player> player2) {
        Position playerPos = player1.first;
        Player player = player1.second;
        Position comparePlayerPos = player2.first;
        Player comparePlayer = player2.second;
        
        int chemistryPoints = 0;
        Shoots playerShoots = Shoots.fromDatabaseName(player.getShoots());

        // VASEN HYÖKKÄÄJÄ
        if (playerPos == Position.LW && playerShoots == Shoots.LEFT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LW && playerShoots == Shoots.RIGHT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills3)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
            // CENTTERI
        } else if (playerPos == Position.C) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayer.hasSomeOfSkills(attackSkills2) && (comparePlayerPos == Position.LW || comparePlayerPos == Position.RW)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayer.hasSomeOfSkills(attackSkills1) && (comparePlayerPos == Position.LW || comparePlayerPos == Position.RW)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && (comparePlayer.hasSomeOfSkills(attackSkills1) || comparePlayer.hasSomeOfSkills(attackSkills2))
                    && (comparePlayerPos == Position.LW || comparePlayerPos == Position.RW)) {
                chemistryPoints++;
            }
            // OIKEA HYÖKKÄÄJÄ
        } else if (playerPos == Position.RW && playerShoots == Shoots.LEFT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills3)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RW && playerShoots == Shoots.RIGHT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
            // VASEN PAKKI
        } else if (playerPos == Position.LD) {
            if (player.hasSomeOfSkills(defenceSkills1) && comparePlayerPos == Position.RD && comparePlayer.hasSomeOfSkills(defenceSkills2)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(defenceSkills2) && comparePlayerPos == Position.RD && comparePlayer.hasSomeOfSkills(defenceSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(defenceSkills3) && comparePlayerPos == Position.RD && (comparePlayer.hasSomeOfSkills(defenceSkills1) || comparePlayer.hasSomeOfSkills(defenceSkills2))) {
                chemistryPoints++;
            }
            // OIKEA PAKKI
        } else if (playerPos == Position.RD && playerShoots == Shoots.RIGHT) {
            if (player.hasSomeOfSkills(defenceSkills1) && comparePlayerPos == Position.LD && comparePlayer.hasSomeOfSkills(defenceSkills2)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(defenceSkills2) && comparePlayerPos == Position.LD && comparePlayer.hasSomeOfSkills(defenceSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(defenceSkills3) && comparePlayerPos == Position.LD && (comparePlayer.hasSomeOfSkills(defenceSkills1) || comparePlayer.hasSomeOfSkills(defenceSkills2))) {
                chemistryPoints++;
            }
        }
        return chemistryPoints;
    }
}
