package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Connection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * THIS IS ONLY CLASS CHEMISTRY POINTS ARE CALLED
 */
class ChemistryPercentAnalyzer {

    private static ArrayList<Player> players;
    private static Map<Connection, PointLimits> connectionPointLimits = new HashMap<>();
    private static Map<Connection, ArrayList<ConnectionPlayers>> playerConnectionPoints = new HashMap<>();
    private static Map<Pair<Position, Player>, ArrayList<ComparePlayer>> comparePlayerConnectionPoints = new HashMap<>();

    public static ArrayList<Player> getAllowedComparePlayers(AllowedPlayerPosition allowedPlayerPosition, Position position) {
        // TODO varmista että on vähintään 5? pelaajaa
        // Jos käytetään useampaa pelaajaa niin laskenta hidastuu dramaattisesti
        ArrayList<Player> allowedComparePlayers = new ArrayList<>();
        if (allowedPlayerPosition == AllowedPlayerPosition.PLAYERS_OWN_POSITION) {
            for (Player player : players) {
                Position comparePosition = Position.fromDatabaseName(player.getPosition());
                if (position == comparePosition) {
                    allowedComparePlayers.add(player);
                }
            }
        } else if (allowedPlayerPosition == AllowedPlayerPosition.BEST_POSITION) {
            for (Player player : players) {
                Position comparePosition = Position.fromDatabaseName(player.getPosition());
                if (position.isAttacker() && comparePosition.isAttacker()) {
                    allowedComparePlayers.add(player);
                }
                if (position.isDefender() && comparePosition.isDefender()) {
                    allowedComparePlayers.add(player);
                }
            }
        }

        return allowedComparePlayers;
    }

    // Call this before call other methods
    public static void initialize(ArrayList<Player> playersInTeam) {
        players = playersInTeam;
        connectionPointLimits = new HashMap<>();

        for (Connection connection : Connection.values()) {
            ConnectionPlayers maxConnectionPlayers = new ConnectionPlayers();
            double maxConnectionPoints = 0;
            Double minConnectionPoints = null;
            ArrayList<ConnectionPlayers> connectionPlayersList = new ArrayList<>();
            Pair<Position, Position> positions = Connection.getPositionsInConnection(connection);
            if (positions != null) {
                Position position1 = positions.first;
                Position position2 = positions.second;
                for (Player player1 : players) {
                    for (Player player2 : players) {
                        if (!player1.getPlayerId().equals(player2.getPlayerId())) {
                            double connectionPoints = ChemistryPointsAnalyzer.getConnectionPoints(position1, player1, position2, player2);

                            if (connectionPoints > maxConnectionPoints) {
                                maxConnectionPoints = connectionPoints;
                                maxConnectionPlayers.position1 = position1;
                                maxConnectionPlayers.player1 = player1;
                                maxConnectionPlayers.position2 = position2;
                                maxConnectionPlayers.player2 = player2;
                                maxConnectionPlayers.connectionPoints = connectionPoints;
                            }
                            if (minConnectionPoints == null || connectionPoints < minConnectionPoints) {
                                minConnectionPoints = connectionPoints;
                            }

                            ConnectionPlayers connectionPlayers = new ConnectionPlayers();
                            connectionPlayers.position1 = position1;
                            connectionPlayers.player1 = player1;
                            connectionPlayers.position2 = position2;
                            connectionPlayers.player2 = player2;
                            connectionPlayers.connectionPoints = connectionPoints;
                            connectionPlayersList.add(connectionPlayers);

                            ComparePlayer comparePlayer = new ComparePlayer();
                            comparePlayer.position = position2;
                            comparePlayer.player = player2;
                            comparePlayer.connectionPoints = connectionPoints;
                            Pair<Position, Player> comparePair = new Pair<>(position1, player1);
                            ArrayList<ComparePlayer> comparePlayers = comparePlayerConnectionPoints.get(comparePair);
                            if (comparePlayers == null) {
                                comparePlayers = new ArrayList<>();
                            }
                            comparePlayers.add(comparePlayer);
                            Collections.sort(comparePlayers, (o1, o2) -> Double.compare(o2.connectionPoints, o1.connectionPoints));
                            comparePlayerConnectionPoints.put(comparePair, comparePlayers);
                        }
                    }
                }
            }
            Collections.sort(connectionPlayersList, (o1, o2) -> Double.compare(o2.connectionPoints, o1.connectionPoints));
            playerConnectionPoints.put(connection, connectionPlayersList);

            /*Logger.log("POINTS SIZE " + connection.name() + ": " + connectionPlayersList.size());
            for (ConnectionPlayers connectionPlayers : connectionPlayersList) {
                Logger.log("POINTS " + connection.name() + ": " + connectionPlayers.connectionPoints + " "
                        + connectionPlayers.position1 + " " + connectionPlayers.player1.getName() + " - " + connectionPlayers.position2 + " " + connectionPlayers.player2.getName());
            }*/


            for (Map.Entry<Pair<Position, Player>, ArrayList<ChemistryPercentAnalyzer.ComparePlayer>> entry : comparePlayerConnectionPoints.entrySet()) {
                Pair<Position, Player> player = entry.getKey();

                ArrayList<ChemistryPercentAnalyzer.ComparePlayer> comparePlayers = entry.getValue();
                ChemistryPercentAnalyzer.ComparePlayer comparePlayer = comparePlayers.get(0);
                //Logger.log(Math.round(comparePlayer.connectionPoints) + ": " + player.first + " " + player.second.getName() + " -> " + comparePlayer.position + " " + comparePlayer.player.getName());
                /*for(ChemistryPercentAnalyzer.ComparePlayer comparePlayer : comparePlayers) {
                    Logger.log(Math.round(comparePlayer.connectionPoints) + ": " + player.first + " " + player.second.getName() + " -> " + comparePlayer.position + " " + comparePlayer.player.getName());
                }*/
            }

            PointLimits pointLimits = new PointLimits();
            pointLimits.minPoints = minConnectionPoints != null ? minConnectionPoints : 0;
            pointLimits.maxPoints = maxConnectionPoints;
            connectionPointLimits.put(connection, pointLimits);
            Logger.log("MAX CONNECTION " + connection.name() + ": " + maxConnectionPlayers.connectionPoints + " "
                    + maxConnectionPlayers.position1 + " " + maxConnectionPlayers.player1.getName() + " - " + maxConnectionPlayers.position2 + " " + maxConnectionPlayers.player2.getName());
        }
    }

    public static Map<Position, ArrayList<Player>> getBestPlayersForPosition(AllowedPlayerPosition allowedPlayerPosition, int numberOfPlayers) {
        Map<Position, ArrayList<Pair<Player, Double>>> allPlayersForPosition = new HashMap<>();

        // Get 5 best players for each position. These are compare players.
        // Compare those players in line every to every position
        // Choose best line or best team chemistry
        for (Position position : Position.values()) {
            // Get closest connections to this position
            ArrayList<Position> closestPositions = Connection.getClosestPositions(position);
            ArrayList<Pair<Player, Double>> playersInPosition = new ArrayList<>();
            ArrayList<Player> players = getAllowedComparePlayers(allowedPlayerPosition, position);

            for (Player player1 : players) {
                // Player is best when chemistry sum is highest
                double chemistryPointSum = 0.0;
                for (Position comparePosition : closestPositions) {
                    ArrayList<Player> comparePlayers = getAllowedComparePlayers(allowedPlayerPosition, comparePosition);
                    double bestChemistryPoints = 0.0;
                    for (Player player2 : comparePlayers) {
                        if (!player1.getPlayerId().equals(player2.getPlayerId())) {
                            double connectionPoints = ChemistryPointsAnalyzer.getConnectionPoints(position, player1, comparePosition, player2);
                            if (connectionPoints > bestChemistryPoints) {
                                bestChemistryPoints = connectionPoints;
                            }
                        }
                    }
                    chemistryPointSum += bestChemistryPoints;
                }
                playersInPosition.add(new Pair<>(player1, chemistryPointSum));
            }

            Collections.sort(playersInPosition, (o1, o2) -> Double.compare(o2.second, o1.second));
            allPlayersForPosition.put(position, playersInPosition);
        }

        // Convert to position - split players map
        Map<Position, ArrayList<Player>> bestPlayersForPosition = new HashMap<>();
        for (Map.Entry<Position, ArrayList<Pair<Player, Double>>> entry : allPlayersForPosition.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Player> players = new ArrayList<>();
            ArrayList<Pair<Player, Double>> playerPairs = entry.getValue();
            for (Pair<Player, Double> playerPair : playerPairs) {
                if (players.size() < numberOfPlayers) {
                    players.add(playerPair.first);
                }
            }
            bestPlayersForPosition.put(position, players);
        }
        // TODO Turha? Täytä vähintään kaikki kentät
        /*if (allowedPlayerPosition == AllowedPlayerPosition.BEST_POSITION && getUniquePlayersCount(bestPlayersForPosition) < players.size()) {
            // Täytä vähintään kaikki kentät
            int linesToCreate = (int)Math.floor(players.size() / 5.0);
            if(linesToCreate > 4) {
                linesToCreate = 4;
            }
            for (Map.Entry<Position, ArrayList<Player>> entry : bestPlayersForPosition.entrySet()) {
                Position position = entry.getKey();
                ArrayList<Player> players = entry.getValue();
                if(players.size() < linesToCreate) {
                    //players.add()
                }
            }
        }*/
        return bestPlayersForPosition;
    }

    private static int getUniquePlayersCount(Map<Position, ArrayList<Player>> bestPlayersForPosition) {
        ArrayList<String> uniquePlayers = new ArrayList<>();
        for (ArrayList<Player> players : bestPlayersForPosition.values()) {
            for (Player player : players) {
                if (!uniquePlayers.contains(player.getPlayerId())) {
                    uniquePlayers.add(player.getPlayerId());
                }
            }
        }
        return uniquePlayers.size();
    }

    public static Map<Pair<Position, Player>, ArrayList<ComparePlayer>> getComparePlayerConnectionPoints() {
        return comparePlayerConnectionPoints;
    }

    public static Map<Connection, ArrayList<ConnectionPlayers>> getPlayerConnectionPoints() {
        return playerConnectionPoints;
    }

    // Used when called from UI
    public static double getConnectionPercent(Player player1, Player player2, Connection connection) {
        double percent = 0.0;
        Pair<Position, Position> positions = Connection.getPositionsInConnection(connection);
        if (positions != null) {
            Position position1 = positions.first;
            Position position2 = positions.second;
            double connectionPoints = ChemistryPointsAnalyzer.getConnectionPoints(position1, player1, position2, player2);
            PointLimits pointLimits = connectionPointLimits.get(connection);
            if (pointLimits != null) {
                double division = pointLimits.maxPoints - pointLimits.minPoints;
                if (division > 0) {
                    percent = (connectionPoints - pointLimits.minPoints) / (pointLimits.maxPoints - pointLimits.minPoints) * 100;
                    // TODO
                    //Logger.log("PERCENT " + connection.name() + ": " + Math.round(percent) + " " + player1.getName() + " - " + player2.getName() +  " -> " + connectionPoints + ", " + pointLimits.maxPoints + " : " + pointLimits.minPoints);
                    if (player1 != null && player2 != null && (player1.getPlayerId().equals("-LlgUzUYqGM-mCnwn7NP") || player2.getPlayerId().equals("-LlgUzUYqGM-mCnwn7NP"))) {
                        // Logger.log("PERCENT " + connection.name() + ": " + Math.round(percent) + " " + player1.getName() + " - " + player2.getName() +  " -> " + connectionPoints + ", " + pointLimits.maxPoints + " : " + pointLimits.minPoints);
                    }
                }
            }
        }
        return percent;
    }

    // Used when search best players
    public static double getConnectionPercent(Position position1, Player player1, Position position2, Player player2, Connection connection) {
        double percent = 0.0;
        double connectionPoints = ChemistryPointsAnalyzer.getConnectionPoints(position1, player1, position2, player2);
        PointLimits pointLimits = connectionPointLimits.get(connection);
        if (pointLimits != null) {
            double division = pointLimits.maxPoints - pointLimits.minPoints;
            if (division > 0) {
                percent = (connectionPoints - pointLimits.minPoints) / (pointLimits.maxPoints - pointLimits.minPoints) * 100;
            }
        }
        return percent;
    }

    private static class PointLimits {
        double minPoints;
        double maxPoints;
    }

    public static class ConnectionPlayers {
        Position position1;
        Player player1;
        Position position2;
        Player player2;
        double connectionPoints;
    }

    public static class ComparePlayer {
        Position position;
        Player player;
        double connectionPoints;
    }

}
