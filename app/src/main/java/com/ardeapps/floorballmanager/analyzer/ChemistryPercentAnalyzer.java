package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.ChemistryConnection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ChemistryPercentAnalyzer {

    private static Map<ChemistryConnection, Double> minPointsForChemistryConnections = new HashMap<>();
    private static Map<ChemistryConnection, Double> maxPointsForChemistryConnections = new HashMap<>();
    private static AllowedPlayerPosition currentAllowedPlayerPosition;
    private static ArrayList<Player> players;

    // Call this before call other methods
    public static void initialize(ArrayList<Player> playersInTeam, AllowedPlayerPosition allowedPlayerPosition) {
        players = playersInTeam;
        currentAllowedPlayerPosition = allowedPlayerPosition;
        // Calculate limits
        Map<ChemistryConnection, Double> minConnectionPoints = new HashMap<>();
        Map<ChemistryConnection, Double> maxConnectionPoints = new HashMap<>();
        for (ChemistryConnection connection : ChemistryConnection.values()) {
            Pair<Position, Position> positions = ChemistryConnection.getPositionsInChemistryConnectionsAsPairs().get(connection);
            Double minPoints = null;
            double maxPoints = 0;
            if (positions != null) {
                Position position1 = positions.first;
                Position position2 = positions.second;
                ArrayList<Player> playersToCompare = getAllowedComparePlayers(position1);
                ArrayList<Player> comparePlayers = getAllowedComparePlayers(position2);

                for (Player player : playersToCompare) {
                    for (Player comparePlayer : comparePlayers) {
                        if (!player.getPlayerId().equals(comparePlayer.getPlayerId())) {
                            double points = ChemistryPointsAnalyzer.getChemistryPoints(position1, player, position2, comparePlayer);
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
    private static double getChemistryPercent(Position position1, Position position2, double chemistryPoints) {
        ChemistryConnection connection = ChemistryConnection.getChemistryConnection(position1, position2);
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

    public static double getChemistryPercent(Position playerPos, Player player, Position comparePlayerPos, Player comparePlayer) {
        double chemistryPoints = ChemistryPointsAnalyzer.getChemistryPoints(playerPos, player, comparePlayerPos, comparePlayer);
        return getChemistryPercent(playerPos, comparePlayerPos, chemistryPoints);
    }
}
