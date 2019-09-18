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
     * Get chemistry connectionPoints of two players based on all abilities
     *
     * @param position1        player position to compare
     * @param player1         player to compare against
     * @param position2 player position to compare against
     * @param player2 player to compare
     * @return count of chemistry connectionPoints
     */
    static double getConnectionPoints(Position position1, Player player1, Position position2, Player player2) {
        double chemistryPoints = 0.0;
        if (player1 != null && player2 != null) {
            String playerId = player1.getPlayerId();
            String comparePlayerId = player2.getPlayerId();
            // GET COMMON GOALS
            int commonGameCount = AnalyzerDataCollector.getGameCountWherePlayersInSameLine(playerId, comparePlayerId);
            // Divide by games to get relative connectionPoints. (More connectionPoints in a few games -> higher chemistry)
            double goalChemistry = 0;
            if (commonGameCount > 0) {
                ArrayList<Goal> commonGoals = AnalyzerDataCollector.getCommonGoals(playerId, comparePlayerId);
                goalChemistry = getGoalsChemistry(playerId, comparePlayerId, commonGoals) / (double) commonGameCount;
            }
            double weightedGoalChemistry = getWeightedGoalChemistry(position1, player1, position2, player2, goalChemistry);

            // THESE ARE BASED ON POSITION -> COMPARE EACH OTHER
            Shoots playerShoots = Shoots.fromDatabaseName(player1.getShoots());
            Shoots comparePlayerShoots = Shoots.fromDatabaseName(player2.getShoots());
            int shootsChemistry = getShootsChemistry(position1, playerShoots, position2, comparePlayerShoots);
            shootsChemistry += getShootsChemistry(position2, comparePlayerShoots, position1, playerShoots);
            int strengthsChemistry = getStrengthsChemistry(position1, player1, position2, player2);
            strengthsChemistry += getStrengthsChemistry(position2, player2, position1, player1);
            int positionsChemistry = getPositionChemistry(position1, player1);
            positionsChemistry += getPositionChemistry(position2, player2);

            double playerPositionChemistry = 0.0;
            ArrayList<String> gameIds = AnalyzerDataCollector.getGamesByPlayerPosition(position1, playerId);
            ArrayList<Goal> goals = AnalyzerDataCollector.getGoalsOfGames(gameIds);
            if (gameIds.size() > 0) {
                playerPositionChemistry += getGoalPointsForPlayer(playerId, goals) / (double) gameIds.size();
            }
            gameIds = AnalyzerDataCollector.getGamesByPlayerPosition(position2, comparePlayerId);
            goals = AnalyzerDataCollector.getGoalsOfGames(gameIds);
            if (gameIds.size() > 0) {
                playerPositionChemistry += getGoalPointsForPlayer(comparePlayerId, goals) / (double) gameIds.size();
            }

            chemistryPoints = getWeightedChemistryPoints(weightedGoalChemistry, shootsChemistry, strengthsChemistry, playerPositionChemistry);
        }
        return chemistryPoints;
    }

    /**
     * @param goalChemistry goal chemistryPoints
     * @param shootsChemistry shoots chemistry connectionPoints
     * @param strengthsChemistry strengths chemistry connectionPoints
     * @return weighted chemistry connectionPoints
     */
    protected static double getWeightedChemistryPoints(double goalChemistry, int shootsChemistry, int strengthsChemistry, double positionsChemistry) {
        double positionsWeight = 0.3;
        double strengthsWeight = 0.2;
        double shootsWeight = 0.1;
        double chemistrySum;
        double positionsMax;
        double strengthsMax;
        double shootsMax;
        if(goalChemistry > 0) {
            positionsMax = goalChemistry * positionsWeight;
            strengthsMax = goalChemistry * strengthsWeight;
            shootsMax = goalChemistry * shootsWeight;
        } else {
            positionsMax = positionsWeight;
            strengthsMax = strengthsWeight;
            shootsMax = shootsWeight;
        }
        double weightedStrengthsChemistry = strengthsChemistry * strengthsMax;
        double weightedShootsChemistry = shootsChemistry * shootsMax;
        double weightedPositionsChemistry = positionsChemistry * positionsMax;
        chemistrySum = goalChemistry + weightedStrengthsChemistry + weightedShootsChemistry + weightedPositionsChemistry;

        return chemistrySum;
    }


    /**
     * More points if defenders has made goals
     *
     * @param player1
     * @param player2
     * @param goalChemistry
     * @return
     */
    protected static double getWeightedGoalChemistry(Position position1, Player player1, Position position2, Player player2, double goalChemistry) {
        double chemistryWeight = 0.0;
        Position playerOwnPosition = Position.fromDatabaseName(player1.getPosition());
        Position comparePlayerOwnPosition = Position.fromDatabaseName(player2.getPosition());

        if (position1.isAttacker() && playerOwnPosition.isAttacker()) {
            if (playerOwnPosition == position1) {
                chemistryWeight += 0.5;
            } else {
                chemistryWeight += 0.2;
            }
        } else if (position1.isDefender() && playerOwnPosition.isDefender()) {
            if (playerOwnPosition == position1) {
                chemistryWeight += 1.0;
            } else {
                chemistryWeight += 0.7;
            }
        }
        if (position2.isAttacker() && comparePlayerOwnPosition.isAttacker()) {
            if (comparePlayerOwnPosition == position2) {
                chemistryWeight += 0.5;
            } else {
                chemistryWeight += 0.2;
            }
        } else if (position2.isDefender() && comparePlayerOwnPosition.isDefender()) {
            if (comparePlayerOwnPosition == position2) {
                chemistryWeight += 1.0;
            } else {
                chemistryWeight += 0.7;
            }
        }
        return goalChemistry * (1 + chemistryWeight);
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

    static int getGoalPointsForPlayer(Position position, String playerId) {
        ArrayList<String> gameIds = AnalyzerDataCollector.getGamesByPlayerPosition(position, playerId);
        ArrayList<Goal> goals = AnalyzerDataCollector.getGoalsOfGames(gameIds);
        int chemistryPoints = 0;
        for (Goal goal : goals) {
            boolean playerOnField = goal.getPlayerIds().contains(playerId);
            if (playerOnField) {
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

    /**
     * Get chemistry based on player position
     * <p>
     * +2 if compared position is player's marked position
     * +1 if compared position is attacker position and player is attacker
     * +1 if compared position is defender position and player is defender
     * -1 if wrong attacker is in defender position or defender is in attacker position
     *
     * @param position player position to compare
     * @param player   player to compare
     * @return chemistry count
     */
    @Deprecated
    // Ei ehkä kannata laskea
    protected static int getPositionChemistry(Position position, Player player) {
        int chemistryPoints;
        Position markedPosition = Position.fromDatabaseName(player.getPosition());
        if (position == markedPosition) {
            chemistryPoints = 2;
        } else if (position.isAttacker() && markedPosition.isAttacker()) {
            chemistryPoints = 1;
        } else if (position.isDefender() && markedPosition.isDefender()) {
            chemistryPoints = 1;
        } else {
            chemistryPoints = -1;
        }
        return chemistryPoints;
    }
}
