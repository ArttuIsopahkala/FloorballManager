package com.ardeapps.floorballmanager.services;

import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerCore extends AnalyzerService {

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
     * @param playerId         player to compare against
     * @param comparePlayerPos player position to compare against
     * @param comparedPlayerId player to compare
     * @param goals            goals saved for team
     * @return count of chemistry points
     */
    public static int getChemistryPoints(Position playerPos, String playerId, Position comparePlayerPos, String comparedPlayerId, ArrayList<Goal> goals) {
        int chemistryPoints = 0;
        Player player = playersInTeam.get(playerId);
        Player comparePlayer = playersInTeam.get(comparedPlayerId);
        if (player != null && comparePlayer != null) {
            Player.Shoots playerShoots = Player.Shoots.fromDatabaseName(player.getShoots());
            Player.Shoots comparePlayerShoots = Player.Shoots.fromDatabaseName(comparePlayer.getShoots());
            chemistryPoints += getGoalsChemistry(playerId, comparedPlayerId, goals);

            chemistryPoints += getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots);
            chemistryPoints += getShootsChemistry(comparePlayerPos, comparePlayerShoots, playerPos, playerShoots);

            chemistryPoints += getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer);
            chemistryPoints += getStrengthsChemistry(comparePlayerPos, comparePlayer, playerPos, player);
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
    public static int getShootsChemistry(Position playerPos, Player.Shoots playerShoots, Position comparePlayerPos, Player.Shoots comparePlayerShoots) {
        int chemistryPoints = 0;
        // VASEN HYÖKKÄÄJÄ
        if (playerPos == Position.LW && playerShoots == Player.Shoots.LEFT) {
            if (comparePlayerPos == Position.C && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LW && playerShoots == Player.Shoots.RIGHT) {
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            // CENTTERI
        } else if (playerPos == Position.C && playerShoots == Player.Shoots.LEFT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.C && playerShoots == Player.Shoots.RIGHT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
            // OIKEA HYÖKKÄÄJÄ
        } else if (playerPos == Position.RW && playerShoots == Player.Shoots.LEFT) {
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RW && playerShoots == Player.Shoots.RIGHT) {
            if (comparePlayerPos == Position.C && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            // VASEN PAKKI
        } else if (playerPos == Position.LD && playerShoots == Player.Shoots.LEFT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LD && playerShoots == Player.Shoots.RIGHT) {
            if (comparePlayerPos == Position.LW && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.RD && comparePlayerShoots == Player.Shoots.LEFT) {
                chemistryPoints++;
            }
            // OIKEA PAKKI
        } else if (playerPos == Position.RD && playerShoots == Player.Shoots.LEFT) {
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
            if (comparePlayerPos == Position.LD && comparePlayerShoots == Player.Shoots.RIGHT) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RD && playerShoots == Player.Shoots.RIGHT) {
            if (comparePlayerPos == Position.RW && comparePlayerShoots == Player.Shoots.LEFT) {
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
    public static int getStrengthsChemistry(Position playerPos, Player player, Position comparePlayerPos, Player comparePlayer) {
        int chemistryPoints = 0;
        Player.Shoots playerShoots = Player.Shoots.fromDatabaseName(player.getShoots());

        // VASEN HYÖKKÄÄJÄ
        if (playerPos == Position.LW && playerShoots == Player.Shoots.LEFT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.LW && playerShoots == Player.Shoots.RIGHT) {
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
        } else if (playerPos == Position.RW && playerShoots == Player.Shoots.LEFT) {
            if (player.hasSomeOfSkills(attackSkills1) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills3)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills2) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills1)) {
                chemistryPoints++;
            } else if (player.hasSomeOfSkills(attackSkills3) && comparePlayerPos == Position.C && comparePlayer.hasSomeOfSkills(attackSkills2)) {
                chemistryPoints++;
            }
        } else if (playerPos == Position.RW && playerShoots == Player.Shoots.RIGHT) {
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
        } else if (playerPos == Position.RD && playerShoots == Player.Shoots.RIGHT) {
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
    public static int getGoalsChemistry(String playerId1, String playerId2, ArrayList<Goal> goals) {
        int chemistryPoints = 0;

        for (Goal goal : goals) {
            List<String> comparePlayers = Arrays.asList(playerId1, playerId2);
            List<String> scorerAndAssistant = Arrays.asList(goal.getScorerId(), goal.getAssistantId());

            boolean bothPlayersOnField = goal.getPlayerIds() != null && goal.getPlayerIds().containsAll(comparePlayers);
            if (bothPlayersOnField) {
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

    /**
     * Get average chemistry percents of given chemistries
     *
     * @param chemistries list to calculate
     * @return line chemistry percent
     */
    public static int getAverageChemistryPercent(ArrayList<Chemistry> chemistries) {
        double chemistryCount = 0;
        double chemistrySize = chemistries.size();
        for (Chemistry chemistry : chemistries) {
            chemistryCount += chemistry.getChemistryPercent();
        }

        return (int) Math.round(chemistryCount / chemistrySize);
    }

    /**
     * Get chemistries of players in line indexed by position.
     * Chemistries are calculated from one line's position to other positions.
     *
     * @param line player chemistries from this line are calculated
     * @return chemistries list indexed by playerId
     */
    public static Map<Position, ArrayList<Chemistry>> getChemistriesInLineForPositions(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

        if (line != null && line.getPlayerIdMap() != null) {
            Map<String, String> playersMap = line.getPlayerIdMap();
            for (Map.Entry<String, String> player : playersMap.entrySet()) {
                final Position position = Position.fromDatabaseName(player.getKey());
                final String playerId = player.getValue();
                ArrayList<Chemistry> chemistries = new ArrayList<>();

                for (Map.Entry<String, String> comparePlayer : playersMap.entrySet()) {
                    Position comparedPosition = Position.fromDatabaseName(comparePlayer.getKey());
                    String comparedPlayerId = comparePlayer.getValue();

                    if (!playerId.equals(comparedPlayerId)) {
                        Chemistry chemistry = new Chemistry();
                        chemistry.setPlayerId(playerId);
                        chemistry.setComparePlayerId(comparedPlayerId);
                        chemistry.setComparePosition(comparedPosition);
                        int percent = getChemistryPercent(position, playerId, comparedPosition, comparedPlayerId);
                        chemistry.setChemistryPercent(percent);

                        chemistries.add(chemistry);
                    }
                }

                chemistryMap.put(position, chemistries);
            }
        }

        return chemistryMap;
    }

    /**
     * Calculates chemistry as percent between two players.
     * This is how percent is calculated:
     * 1. Get all chemistry points from all players.
     * 2. Get minimum and maximum chemistries from those.
     * 3. Get chemistry between two compared players.
     * 4. Calculate percent where that chemistry takes place between min and max points.
     * Note: Percent is calculated from games where both compared players have been set in the same line.
     *
     * @param playerId        player to compare against
     * @param comparePlayerId player to compare
     * @return average chemistry points as percent (0-100)
     */
    public static int getChemistryPercent(Position playerPosition, String playerId, Position comparePlayerPosition, String comparePlayerId) {
        ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(playerId, comparePlayerId);
        int points = AnalyzerCore.getChemistryPoints(playerPosition, playerId, comparePlayerPosition, comparePlayerId, goals);
        int maxPoints = AnalyzerCore.getMaxChemistryPoints();
        int minPoints = AnalyzerCore.getMinChemistryPoints();

        return (int) Math.round((double) points / (maxPoints - minPoints) * 100);
    }

    /**
     * Get filtered map to players' closest positions
     *
     * @param chemistryMap map indexed by position in line
     * @return filtered map to closest positions
     */
    public static Map<Position, ArrayList<Chemistry>> getFilteredChemistryMapToClosestPlayers(Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, ArrayList<Chemistry>> filteredMap = new HashMap<>();

        for (Map.Entry<Position, ArrayList<Chemistry>> entry : chemistryMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            List<Position> closestPositions = new ArrayList<>();
            if (position == Position.LW) {
                closestPositions = Arrays.asList(Position.C, Position.LD);
            } else if (position == Position.C) {
                closestPositions = Arrays.asList(Position.LW, Position.RW, Position.LD, Position.RD);
            } else if (position == Position.RW) {
                closestPositions = Arrays.asList(Position.C, Position.RD);
            } else if (position == Position.LD) {
                closestPositions = Arrays.asList(Position.C, Position.LW);
            } else if (position == Position.RD) {
                closestPositions = Arrays.asList(Position.C, Position.RW);
            }

            ArrayList<Chemistry> filteredChemistries = new ArrayList<>();
            for (Chemistry chemistry : chemistries) {
                if (closestPositions.contains(chemistry.getComparePosition())) {
                    filteredChemistries.add(chemistry);
                }
            }
            filteredMap.put(position, filteredChemistries);
        }

        return filteredMap;
    }

    /**
     * Get chemistry percents between given position and other map's positions
     *
     * @param position     position to use
     * @param chemistryMap map of all positions
     * @return chemistry points indexed by other positions
     */
    public static Map<Position, Integer> getConvertCompareChemistryPercentsForPosition(Position position, Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, Integer> chemistryPoints = new HashMap<>();
        ArrayList<Chemistry> chemistries = chemistryMap.get(position);
        if (chemistries != null) {
            for (Chemistry chemistry : chemistries) {
                Position comparePosition = chemistry.getComparePosition();
                chemistryPoints.put(comparePosition, chemistry.getChemistryPercent());
            }
        }

        return chemistryPoints;
    }

    /**
     * Get maximum chemistry points from all players of team
     *
     * @return maximum chemistry points
     */
    public static int getMaxChemistryPoints() {
        int maxPoints = 0;
        for (Player player : playersInTeam.values()) {
            Position position = Position.fromDatabaseName(player.getPosition());
            for (Player comparePlayer : playersInTeam.values()) {
                if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    Position comparePosition = Position.fromDatabaseName(comparePlayer.getPosition());
                    ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId());
                    int points = getChemistryPoints(position, player.getPlayerId(), comparePosition, comparePlayer.getPlayerId(), goals);
                    if (points > maxPoints) {
                        maxPoints = points;
                    }
                }
            }
        }
        return maxPoints;
    }

    /**
     * Get minimum chemistry points from all players of team
     *
     * @return minimum chemistry points
     */
    public static int getMinChemistryPoints() {
        Integer minPoints = null;
        for (Player player : playersInTeam.values()) {
            Position position = Position.fromDatabaseName(player.getPosition());
            for (Player comparePlayer : playersInTeam.values()) {
                if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    Position comparePosition = Position.fromDatabaseName(comparePlayer.getPosition());
                    ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId());
                    int points = getChemistryPoints(position, player.getPlayerId(), comparePosition, comparePlayer.getPlayerId(), goals);
                    if (minPoints == null || points < minPoints) {
                        minPoints = points;
                    }
                }
            }
        }
        return minPoints == null ? 0 : minPoints; // Default min is zero
    }
}
