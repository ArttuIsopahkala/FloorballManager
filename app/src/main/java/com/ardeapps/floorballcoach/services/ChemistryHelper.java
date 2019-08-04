package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChemistryHelper extends AnalyzerService {

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
     * Get average chemistry percents of given chemistries
     * @param chemistries list to calculate
     * @return line chemistry percent
     */
    public static int getAverageChemistryPercent(ArrayList<Chemistry> chemistries) {
        double chemistryCount = 0;
        double chemistrySize = chemistries.size();
        for(Chemistry chemistry : chemistries) {
            chemistryCount += chemistry.getChemistryPercent();
        }

        return (int)Math.round(chemistryCount / chemistrySize);
    }

    /**
     * Get chemistries of players in line indexed by position.
     * Chemistries are calculated from one line's position to other positions.
     * @param line player chemistries from this line are calculated
     * @return chemistries list indexed by playerId
     */
    public static Map<Position, ArrayList<Chemistry>> getChemistriesInLineForPositions(Line line) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

        if(line != null && line.getPlayerIdMap() != null) {
            Map<String, String> playersMap = line.getPlayerIdMap();
            for (Map.Entry<String, String> player : playersMap.entrySet()) {
                final String position = player.getKey();
                final String playerId = player.getValue();
                ArrayList<Chemistry> chemistries = new ArrayList<>();

                for (Map.Entry<String, String> comparePlayer : playersMap.entrySet()) {
                    Position comparedPosition = Position.fromDatabaseName(comparePlayer.getKey());
                    String comparedPlayerId = comparePlayer.getValue();

                    if(!playerId.equals(comparedPlayerId)) {
                        Chemistry chemistry = new Chemistry();
                        chemistry.setPlayerId(playerId);
                        chemistry.setComparePlayerId(comparedPlayerId);
                        chemistry.setComparePosition(comparedPosition);
                        ArrayList<Goal> filteredGoals = AnalyzerHelper.getGoalsWherePlayersInSameLine(playerId, comparedPlayerId);
                        int points = getChemistryPoints(playerId, comparedPlayerId, filteredGoals);
                        chemistry.setChemistryPoints(points);
                        int percent = getChemistryPercent(playerId, comparedPlayerId);
                        chemistry.setChemistryPercent(percent);

                        chemistries.add(chemistry);
                    }
                }

                chemistryMap.put(Position.fromDatabaseName(position), chemistries);
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
     * @param playerId player to compare against
     * @param comparePlayerId player to compare
     * @return average chemistry points as percent (0-100)
     */
    public static int getChemistryPercent(String playerId, String comparePlayerId) {
        ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(playerId, comparePlayerId);
        int points = ChemistryHelper.getChemistryPoints(playerId, comparePlayerId, goals);
        int maxPoints = ChemistryHelper.getMaxChemistryPoints();
        int minPoints = ChemistryHelper.getMinChemistryPoints();

        return (int)Math.round((double)points / (maxPoints - minPoints) * 100);
    }

    /**
     * Get filtered map to players' closest positions
     * @param chemistryMap map indexed by position in line
     * @return filtered map to closest positions
     */
    public static Map<Position, ArrayList<Chemistry>> getFilteredChemistryMapToClosestPlayers(Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, ArrayList<Chemistry>> filteredMap = new HashMap<>();

        for (Map.Entry<Position, ArrayList<Chemistry>> entry : chemistryMap.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Chemistry> chemistries = entry.getValue();

            List<Position> closestPositions = new ArrayList<>();
            if(position == Position.LW) {
                closestPositions = Arrays.asList(Position.C, Position.LD);
            } else if(position == Position.C) {
                closestPositions = Arrays.asList(Position.LW, Position.RW, Position.LD, Position.RD);
            } else if(position == Position.RW) {
                closestPositions = Arrays.asList(Position.C, Position.RD);
            } else if(position == Position.LD) {
                closestPositions = Arrays.asList(Position.C, Position.LW);
            } else if(position == Position.RD) {
                closestPositions = Arrays.asList(Position.C, Position.RW);
            }

            ArrayList<Chemistry> filteredChemistries = new ArrayList<>();
            for(Chemistry chemistry : chemistries) {
                if(closestPositions.contains(chemistry.getComparePosition())) {
                    filteredChemistries.add(chemistry);
                }
            }
            filteredMap.put(position, filteredChemistries);
        }

        return filteredMap;
    }

    /**
     * Get chemistry percents between given position and other map's positions
     * @param position position to use
     * @param chemistryMap map of all positions
     * @return chemistry points indexed by other positions
     */
    public static Map<Position, Integer> getConvertCompareChemistryPercentsForPosition(Position position, Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, Integer> chemistryPoints = new HashMap<>();
        ArrayList<Chemistry> chemistries = chemistryMap.get(position);
        if(chemistries != null) {
            for(Chemistry chemistry : chemistries) {
                Position comparePosition = chemistry.getComparePosition();
                chemistryPoints.put(comparePosition, chemistry.getChemistryPercent());
            }
        }

        return chemistryPoints;
    }

    /**
     * Get maximum chemistry points from all players of team
     * @return maximum chemistry points
     */
    public static int getMaxChemistryPoints() {
        int maxPoints = 0;
        for(Player player : playersInTeam) {
            for(Player comparePlayer : playersInTeam) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId());
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(points > maxPoints) {
                        maxPoints = points;
                    }
                }
            }
        }
        return maxPoints;
    }

    /**
     * Get minimum chemistry points from all players of team
     * @return minimum chemistry points
     */
    public static int getMinChemistryPoints() {
        Integer minPoints = null;
        for(Player player : playersInTeam) {
            for(Player comparePlayer : playersInTeam) {
                if(!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    ArrayList<Goal> goals = AnalyzerHelper.getGoalsWherePlayersInSameLine(player.getPlayerId(), comparePlayer.getPlayerId());
                    int points = getChemistryPoints(player.getPlayerId(), comparePlayer.getPlayerId(), goals);
                    if(minPoints == null || points < minPoints) {
                        minPoints = points;
                    }
                }
            }
        }
        return minPoints == null ? 0 : minPoints; // Default min is zero
    }
}
