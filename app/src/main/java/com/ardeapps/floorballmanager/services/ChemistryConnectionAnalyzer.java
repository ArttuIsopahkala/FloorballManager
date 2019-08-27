package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.ChemistryConnection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChemistryConnectionAnalyzer {

    private static Map<ChemistryConnection, Double> minPointsForChemistryConnections = new HashMap<>();
    private static Map<ChemistryConnection, Double> maxPointsForChemistryConnections = new HashMap<>();

    private static ArrayList<Player> playersInTeam = new ArrayList<>();
    private static final ArrayList<Position> attackerPositions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW));
    private static final ArrayList<Position> defenderPositions = new ArrayList<>(Arrays.asList(Position.LD, Position.RD));
    private static AnalyzerService.AllowedPlayerPosition allowedPlayerPosition;

    public static void initialize(ArrayList<Player> players, AnalyzerService.AllowedPlayerPosition allowedPlayerPositions) {
        allowedPlayerPosition = allowedPlayerPositions;
        playersInTeam = players;

        // Calculate limits
        Map<ChemistryConnection, Double> minConnectionPoints = new HashMap<>();
        Map<ChemistryConnection, Double> maxConnectionPoints = new HashMap<>();
        for(ChemistryConnection connection : ChemistryConnection.values()) {
            Pair<Position, Position> positions = ChemistryConnection.getPositionsInChemistryConnectionsAsPairs().get(connection);
            Double minPoints = null;
            double maxPoints = 0;
            if(positions != null) {
                Position position1 = positions.first;
                Position position2 = positions.second;
                ArrayList<Player> playersToCompare = getAllowedComparePlayers(position1);
                ArrayList<Player> comparePlayers = getAllowedComparePlayers(position2);

                for (Player player : playersToCompare) {
                    for (Player comparePlayer : comparePlayers) {
                        if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                            Pair<Position, String> player1 = new Pair<>(position1, player.getPlayerId());
                            Pair<Position, String> player2 = new Pair<>(position2, comparePlayer.getPlayerId());
                            double points = ChemistryPointsAnalyzer.getChemistryPoints(player1, player2);
                            if (minPoints == null || points < minPoints) {
                                minPoints = points;
                            }
                            if (points > maxPoints) {
                                maxPoints = points;
                            }
                        }
                    }
                }
            }
            minConnectionPoints.put(connection, minPoints == null ? 0 : minPoints);
            maxConnectionPoints.put(connection, maxPoints);
        }
        minPointsForChemistryConnections = minConnectionPoints;
        maxPointsForChemistryConnections = maxConnectionPoints;
    }

    public static ArrayList<Player> getAllowedComparePlayers(Position position) {
        /*for(Player player : playersInTeam) {
            Position comparePosition = Position.fromDatabaseName(player.getPosition());
            if(attackerPositions.contains(position) && attackerPositions.contains(comparePosition)) {
                comparePlayers.add(player);
            }
            if(defenderPositions.contains(position) && defenderPositions.contains(comparePosition)) {
                comparePlayers.add(player);
            }
        }*/
        // Jos käytetään useampaa pelaajaa niin laskenta hidastuu dramaattisesti
        ArrayList<Player> comparePlayers = new ArrayList<>();
        if(allowedPlayerPosition == AnalyzerService.AllowedPlayerPosition.MOST_GOALS_IN_POSITION) {
            for (Player player : playersInTeam) {
                Position comparePosition = AnalyzerHelper.getBestPositionFromGoals(player);
                if(position == comparePosition) {
                    comparePlayers.add(player);
                }
            }
        } else if(allowedPlayerPosition == AnalyzerService.AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            for (Player player : playersInTeam) {
                Position comparePosition = Position.fromDatabaseName(player.getPosition());
                if(position == comparePosition) {
                    comparePlayers.add(player);
                }
            }
        }
        return comparePlayers;
    }

    @Deprecated
    public static double getBestChemistryPointsSum(Player player, Position position) {
        ArrayList<Player> comparePlayers = getAllowedComparePlayers(position);
        double pointSumInConnections = 0;
        // Get closest players (chemistry connection) for this position. Example LD -> C_LD, LD_RD, LD_LW.
        ArrayList<ChemistryConnection> closestConnections = ChemistryConnection.getClosestChemistryConnections(position);
        for(ChemistryConnection closestConnection : closestConnections) {
            Position currentComparePosition = ChemistryConnection.getComparePosition(closestConnection, position);
            if(currentComparePosition != null) {
                if(comparePlayers != null) {
                    // Now we have data to calculate points for current chemistry connection
                    // Add only best chemistry to sum
                    double bestChemistryConnectionPoints = ChemistryConnectionAnalyzer.getBestChemistryPoints(player, position, currentComparePosition, comparePlayers);
                    pointSumInConnections += bestChemistryConnectionPoints;
                }
            }
        }

        return pointSumInConnections;
    }

    @Deprecated
    public static double getBestChemistryPointsSum(Player player, Position position, Map<Position, ArrayList<Player>> playersInPosition) {
        double pointSumInConnections = 0;
        // Get closest players (chemistry connection) for this position. Example LD -> C_LD, LD_RD, LD_LW.
        ArrayList<ChemistryConnection> closestConnections = ChemistryConnection.getClosestChemistryConnections(position);
        for(ChemistryConnection closestConnection : closestConnections) {
            Position currentComparePosition = ChemistryConnection.getComparePosition(closestConnection, position);
            if(currentComparePosition != null) {
                ArrayList<Player> comparePlayers = playersInPosition.get(currentComparePosition);
                if(comparePlayers != null) {
                    // Now we have data to calculate points for current chemistry connection
                    // Add only best chemistry to sum
                    double bestChemistryConnectionPoints = ChemistryConnectionAnalyzer.getBestChemistryPoints(player, position, currentComparePosition, comparePlayers);
                    pointSumInConnections += bestChemistryConnectionPoints;
                }
            }
        }

        return pointSumInConnections;
    }

    @Deprecated
    public static double getBestChemistryPoints(Player player, Position position, Position comparePosition, ArrayList<Player> comparePlayers) {
        double bestChemistryConnectionPoints = 0;
        for(Player comparePlayer : comparePlayers) {
            Pair<Position, String> player1 = new Pair<>(position, player.getPlayerId());
            Pair<Position, String> player2 = new Pair<>(comparePosition, comparePlayer.getPlayerId());
            double chemistryConnectionPoints = ChemistryPointsAnalyzer.getChemistryPoints(player1, player2);
            if(chemistryConnectionPoints > bestChemistryConnectionPoints) {
                bestChemistryConnectionPoints = chemistryConnectionPoints;
            }
        }
        return bestChemistryConnectionPoints;
    }

    // TODO update docs
    /**
     * Calculates chemistry as percent between two players.
     * This is how percent is calculated:
     * 1. Get all chemistry points from all players.
     * 2. Get minimum and maximum chemistries from those.
     * 3. Get chemistry between two compared players.
     * 4. Calculate percent where that chemistry takes place between min and max points.
     * Note: Percent is calculated from games where both compared players have been set in the same line.
     *
     * @param position1 player to compare against
     * @param position2 player to compare
     * @return average chemistry points as percent (0-100)
     */
    public static double getChemistryPercent(Position position1, Position position2, double chemistryPoints) {
        ChemistryConnection connection = ChemistryConnection.getChemistryConnection(position1, position2);
        double minChemistryPoints = 0.0;
        double maxChemistryPoints = 0.0;
        if(connection != null) {
            Double minPoints = minPointsForChemistryConnections.get(connection);
            Double maxPoints = maxPointsForChemistryConnections.get(connection);
            if(minPoints != null) {
                minChemistryPoints = minPoints;
            }
            if(maxPoints != null) {
                maxChemistryPoints = maxPoints;
            }
        }
        double division = maxChemistryPoints - minChemistryPoints;
        if(division > 0) {
            return chemistryPoints / (maxChemistryPoints - minChemistryPoints) * 100;
        } else {
            return 0;
        }
    }
}
