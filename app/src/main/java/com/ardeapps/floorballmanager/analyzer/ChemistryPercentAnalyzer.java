package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Connection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * THIS IS ONLY CLASS CHEMISTRY POINTS ARE CALLED
 */
class ChemistryPercentAnalyzer {


    private static Map<Connection, Double> minPointsForChemistryConnections = new HashMap<>();
    private static Map<Connection, Double> maxPointsForChemistryConnections = new HashMap<>();
    private static AllowedPlayerPosition currentAllowedPlayerPosition;
    private static ArrayList<Player> players;

    private static Map<OneToOther, PointLimits> oneToOtherPointLimits = new HashMap<>();
    private static Map<Connection, PointLimits> connectionPointLimits = new HashMap<>();

    private static class OneToOther {
        Position position;
        Connection connection;

        public OneToOther(Position position, Connection connection) {
            this.position = position;
            this.connection = connection;
        }
    }

    private static class PointLimits {
        double minPoints;
        double maxPoints;
    }

    public static ArrayList<Player> getAllowedComparePlayers(Position position) {
        // Jos käytetään useampaa pelaajaa niin laskenta hidastuu dramaattisesti
        ArrayList<Player> allowedComparePlayers = new ArrayList<>();
        if(currentAllowedPlayerPosition == AllowedPlayerPosition.MOST_GOALS_IN_POSITION) {
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
                oneToOtherPointLimits.put(new OneToOther(position1, connection), pointLimits);

                pointLimits = getConnectionChemistryLimitsOneToOther(position2, connection);
                oneToOtherPointLimits.put(new OneToOther(position2, connection), pointLimits);
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
    public static double getConnectionChemistryPercent(Position position, Player player, Position comparePosition, Player comparePlayer) {
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
    public static double getConnectionChemistryPercent(Position position, Player player, Connection connection) {
        double playerConnectionChemistryPoints = getPlayerConnectionChemistryPoints(position, player, connection);
        PointLimits pointLimits = oneToOtherPointLimits.get(new OneToOther(position, connection));
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
    @Deprecated
    private static double getChemistryPercent(Position position1, Position position2, double chemistryPoints) {
        Connection connection = Connection.getChemistryConnection(position1, position2);
        double minChemistryPoints = 0.0;
        double maxChemistryPoints = 0.0;
        if (connection != null) {
            Double minPoints = minPointsForChemistryConnections.get(connection);
            Double maxPoints = maxPointsForChemistryConnections.get(connection);
            if (minPoints != null) {
                minChemistryPoints = minPoints;
            }
            if (maxPoints != null) {
                maxChemistryPoints = maxPoints;
            }
        }
        double division = maxChemistryPoints - minChemistryPoints;
        if (division > 0) {
            return chemistryPoints / (maxChemistryPoints - minChemistryPoints) * 100;
        } else {
            return 0;
        }
    }

    @Deprecated
    public static double getChemistryPercent(Position playerPos, Player player, Position comparePlayerPos, Player comparePlayer) {
        double chemistryPoints = ChemistryPointsAnalyzer.getChemistryPoints(playerPos, player, comparePlayerPos, comparePlayer);
        return getChemistryPercent(playerPos, comparePlayerPos, chemistryPoints);
    }

}
