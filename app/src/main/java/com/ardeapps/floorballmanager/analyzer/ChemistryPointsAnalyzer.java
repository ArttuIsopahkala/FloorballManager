package com.ardeapps.floorballmanager.analyzer;

import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;
import com.ardeapps.floorballmanager.objects.Player.Shoots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChemistryPointsAnalyzer {

    private static final ArrayList<Player.Skill> attackSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.PASSING, Player.Skill.GAME_SENSE));
    private static final ArrayList<Player.Skill> attackSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING, Player.Skill.BALL_HANDLING));
    private static final ArrayList<Player.Skill> attackSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.SPEED, Player.Skill.PHYSICALITY, Player.Skill.BALL_PROTECTION));

    private static final ArrayList<Player.Skill> defenceSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING, Player.Skill.PHYSICALITY));
    private static final ArrayList<Player.Skill> defenceSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.GAME_SENSE, Player.Skill.BALL_PROTECTION, Player.Skill.PASSING));
    private static final ArrayList<Player.Skill> defenceSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.BLOCKING, Player.Skill.INTERCEPTION));

    /**
     * Get chemistry points of two players based on all abilities
     *
     * @param playerPos        player position to compare
     * @param player         player to compare against
     * @param comparePlayerPos player position to compare against
     * @param comparePlayer player to compare
     * @return count of chemistry points
     */
    static double getChemistryPoints(Position playerPos, Player player, Position comparePlayerPos, Player comparePlayer) {
        double chemistryPoints = 0.0;
        if (player != null && comparePlayer != null) {
            String playerId = player.getPlayerId();
            String comparePlayerId = comparePlayer.getPlayerId();
            int gameCount = AnalyzerDataCollector.getGameCountWherePlayersInSameLine(playerId, comparePlayerId);
            // Divide by games to get relative points. (More points in a few games -> higher chemistry)
            int goalChemistry = 0;
            if(gameCount > 0) {
                ArrayList<Goal> commonGoals = AnalyzerDataCollector.getCommonGoals(playerId, comparePlayerId);
                goalChemistry = getGoalsChemistry(playerId, comparePlayerId, commonGoals) / gameCount;
            }

            Shoots playerShoots = Shoots.fromDatabaseName(player.getShoots());
            Shoots comparePlayerShoots = Shoots.fromDatabaseName(comparePlayer.getShoots());
            int shootsChemistry = getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots);
            shootsChemistry += getShootsChemistry(comparePlayerPos, comparePlayerShoots, playerPos, playerShoots);

            int strengthsChemistry = getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer);
            strengthsChemistry += getStrengthsChemistry(comparePlayerPos, comparePlayer, playerPos, player);

            chemistryPoints = getWeightedChemistryPoints(goalChemistry, shootsChemistry, strengthsChemistry);
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
            }
        }
        return chemistryPoints;
    }

    static int getGoalPointsForPlayer(String playerId, ArrayList<Goal> goals) {
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
     * @param playerPos           player position to compare
     * @param playerShoots        player shoots
     * @param comparePlayerPos    player position to compare against
     * @param comparePlayerShoots compare player shoots
     * @return chemistry count
     */
    protected static int getShootsChemistry(Position playerPos, Player.Shoots playerShoots, Position comparePlayerPos, Player.Shoots comparePlayerShoots) {
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
     * @param playerPos        player position to compare
     * @param player           player to compare
     * @param comparePlayerPos player position to compare against
     * @param comparePlayer    player to compare against
     * @return chemistry count
     */
    protected static int getStrengthsChemistry(Position playerPos, Player player, Position comparePlayerPos, Player comparePlayer) {
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
