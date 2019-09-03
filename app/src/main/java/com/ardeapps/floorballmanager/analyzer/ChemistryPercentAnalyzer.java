package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Connection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * THIS IS ONLY CLASS CHEMISTRY POINTS ARE CALLED
 */
class ChemistryPercentAnalyzer {

    private static AllowedPlayerPosition currentAllowedPlayerPosition;
    private static ArrayList<Player> players;

    private static Map<Pair<Position, Connection>, PointLimits> oneToOtherPointLimits = new HashMap<>();
    private static Map<Connection, PointLimits> connectionPointLimits = new HashMap<>();

    private static class PointLimits {
        double minPoints;
        double maxPoints;
    }

    public static ArrayList<Player> getAllowedComparePlayers(Position position) {
        // Jos käytetään useampaa pelaajaa niin laskenta hidastuu dramaattisesti
        ArrayList<Player> allowedComparePlayers = new ArrayList<>();
        if(currentAllowedPlayerPosition == AllowedPlayerPosition.BEST_POSITION) {
            for (Player player : players) {
                Position comparePosition = AnalyzerDataCollector.getBestPositionFromGoals(player);
                if(position == comparePosition) {
                    allowedComparePlayers.add(player);
                }
            }
        } else if(currentAllowedPlayerPosition == AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            for (Player player : players) {
                Position comparePosition = Position.fromDatabaseName(player.getPosition());
                if(position == comparePosition) {
                    allowedComparePlayers.add(player);
                }
            }
        } else if(currentAllowedPlayerPosition == AllowedPlayerPosition.ANY_POSITION) {
            allowedComparePlayers = players;
        } else if(currentAllowedPlayerPosition == AllowedPlayerPosition.SIDES_POSITION) {
            ArrayList<Position> attackerPositions = new ArrayList<>(Arrays.asList(Position.LW, Position.C, Position.RW));
            ArrayList<Position> defenderPositions = new ArrayList<>(Arrays.asList(Position.LD, Position.RD));
            for (Player player : players) {
                Position comparePosition = Position.fromDatabaseName(player.getPosition());
                if(attackerPositions.contains(position) && attackerPositions.contains(comparePosition)) {
                    allowedComparePlayers.add(player);
                }
                if(defenderPositions.contains(position) && defenderPositions.contains(comparePosition)) {
                    allowedComparePlayers.add(player);
                }
            }
        }
        return allowedComparePlayers;
    }

    // Call this before call other methods
    public static void initialize(ArrayList<Player> playersInTeam, AllowedPlayerPosition allowedPlayerPosition) {
        players = playersInTeam;
        currentAllowedPlayerPosition = allowedPlayerPosition;
        oneToOtherPointLimits = new HashMap<>();
        connectionPointLimits = new HashMap<>();
        // Calculate limits
        for (Connection connection : Connection.values()) {
            // ONE_TO_OTHER
            Pair<Position, Position> positions = Connection.getPositionsInChemistryConnectionsAsPairs().get(connection);
            // Get connection limits to every position against every position in chemistry connections
            if (positions != null) {
                Position position1 = positions.first;
                Position position2 = positions.second;
                PointLimits pointLimits = getConnectionChemistryLimitsOneToOther(position1, connection);
                oneToOtherPointLimits.put(new Pair<>(position1, connection), pointLimits);

                pointLimits = getConnectionChemistryLimitsOneToOther(position2, connection);
                oneToOtherPointLimits.put(new Pair<>(position2, connection), pointLimits);
            }
            // ONE_TO_ONE
            PointLimits pointLimits = getConnectionChemistryLimitsOneToOne(connection);
            connectionPointLimits.put(connection, pointLimits);
        }
    }

    private static PointLimits getConnectionChemistryLimitsOneToOther(Position position, Connection connection) {
        Position comparePosition = Connection.getComparePosition(position, connection);
        Double currentMinPoints = null;
        double maxPoints = 0;
        ArrayList<Player> players = getAllowedComparePlayers(position);
        ArrayList<Player> comparePlayers = getAllowedComparePlayers(comparePosition);
        for (Player player : players) {
            for (Player comparePlayer : comparePlayers) {
                if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                    double points = ChemistryPointsAnalyzer.getChemistryPoints(position, player, comparePosition, comparePlayer);
                    if (currentMinPoints == null || points < currentMinPoints) {
                        currentMinPoints = points;
                    }
                    if (points > maxPoints) {
                        maxPoints = points;
                    }
                }
            }
        }
        double minPoints = currentMinPoints != null ? currentMinPoints : 0;

        PointLimits pointLimits = new PointLimits();
        pointLimits.minPoints = minPoints;
        pointLimits.maxPoints = maxPoints;
        return pointLimits;
    }

    private static PointLimits getConnectionChemistryLimitsOneToOne(Connection connection) {
        Pair<Position, Position> positions = Connection.getPositionsInChemistryConnectionsAsPairs().get(connection);
        // Get connection limits between positions
        Double currentMinPointsSum = null;
        double maxPointsSum = 0;
        if (positions != null) {
            Position position = positions.first;
            Position comparePosition = positions.second;
            ArrayList<Player> players = getAllowedComparePlayers(position);
            ArrayList<Player> comparePlayers = getAllowedComparePlayers(comparePosition);
            for (Player player : players) {
                for (Player comparePlayer : comparePlayers) {
                    if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                        double pointsSum = ChemistryPointsAnalyzer.getChemistryPoints(position, player, comparePosition, comparePlayer);
                        pointsSum += ChemistryPointsAnalyzer.getChemistryPoints(comparePosition, comparePlayer, position, player);
                        if (currentMinPointsSum == null || pointsSum < currentMinPointsSum) {
                            currentMinPointsSum = pointsSum;
                        }
                        if (pointsSum > maxPointsSum) {
                            maxPointsSum = pointsSum;
                        }
                    }
                }
            }
        }
        double minPointsSum = currentMinPointsSum != null ? currentMinPointsSum : 0;

        PointLimits pointLimits = new PointLimits();
        pointLimits.minPoints = minPointsSum;
        pointLimits.maxPoints = maxPointsSum;
        return pointLimits;
    }

    // ONE_TO_ONE
    static double getConnectionChemistryPercentOneToOne(Position position, Player player, Position comparePosition, Player comparePlayer) {
        Connection connection = Connection.getChemistryConnection(position, comparePosition);

        double chemistrySum = ChemistryPointsAnalyzer.getChemistryPoints(position, player, comparePosition, comparePlayer);
        chemistrySum += ChemistryPointsAnalyzer.getChemistryPoints(comparePosition, comparePlayer, position, player);

        PointLimits pointLimits = connectionPointLimits.get(connection);
        if(pointLimits != null) {
            double division = pointLimits.maxPoints - pointLimits.minPoints;
            if (division > 0) {
                return chemistrySum / (pointLimits.maxPoints - pointLimits.minPoints) * 100;
            }
        }
        return 0;
    }

    // ONE_TO_OTHER
    static double getConnectionChemistryPercentOneToOther(Position position, Player player, Connection connection) {
        double playerConnectionChemistryPoints = getPlayerConnectionChemistryPoints(position, player, connection);
        PointLimits pointLimits = oneToOtherPointLimits.get(new Pair<>(position, connection));
        if(pointLimits != null) {
            double division = pointLimits.maxPoints - pointLimits.minPoints;
            if (division > 0) {
                return playerConnectionChemistryPoints / (pointLimits.maxPoints - pointLimits.minPoints) * 100;
            }
        }
        return 0;
    }

    private static double getPlayerConnectionChemistryPoints(Position position, Player player, Connection connection) {
        Position comparePosition = Connection.getComparePosition(position, connection);
        ArrayList<Player> comparePlayers = getAllowedComparePlayers(comparePosition);
        double bestChemistryPoints = 0;
        for(Player comparePlayer : comparePlayers) {
            double chemistryPoints = ChemistryPointsAnalyzer.getChemistryPoints(position, player, comparePosition, comparePlayer);
            if(chemistryPoints > bestChemistryPoints) {
                bestChemistryPoints = chemistryPoints;
            }
        }
        return bestChemistryPoints;
    }
}
