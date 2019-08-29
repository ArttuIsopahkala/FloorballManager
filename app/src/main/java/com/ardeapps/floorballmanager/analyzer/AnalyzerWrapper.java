package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Connection;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * THIS IS ONLY CLASS CHEMISTRY PERCENTS ARE CALLED
 */
public class AnalyzerWrapper {

    public static double getPlayerChemistryPercent(Position position, Player player) {
        ArrayList<Connection> connections = Connection.getClosestChemistryConnections(position);
        int connectionCount = connections.size();
        int percent = 0;
        if(connectionCount > 0) {
            double percentSum = 0;
            for (Connection connection : connections) {
                percentSum += ChemistryPercentAnalyzer.getConnectionChemistryPercent(position, player, connection);
            }
            percent = (int) Math.round(percentSum / connectionCount);
        }
        return percent;
    }

    /**
     * Get chemistries of players in line indexed by position.
     * Chemistries are calculated from one line's position to other positions.
     *
     * @param playersMap chemistries from this players are calculated
     * @return chemistries list indexed by playerId
     */
    public static Map<Position, ArrayList<Chemistry>> getChemistriesInLineForPositions(Map<String, String> playersMap) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

        for (Map.Entry<String, String> playerEntry : playersMap.entrySet()) {
            final Position position = Position.fromDatabaseName(playerEntry.getKey());
            String playerId = playerEntry.getValue();
            final Player player = AnalyzerService.playersInTeam.get(playerId);
            if(player != null) {
                ArrayList<Chemistry> chemistries = new ArrayList<>();
                for (Map.Entry<String, String> comparePlayerEntry : playersMap.entrySet()) {
                    Position comparePosition = Position.fromDatabaseName(comparePlayerEntry.getKey());
                    String comparePlayerId = comparePlayerEntry.getValue();
                    final Player comparePlayer = AnalyzerService.playersInTeam.get(comparePlayerId);
                    if(comparePlayer != null) {
                        if (!playerId.equals(comparePlayerId)) {
                            Chemistry chemistry = new Chemistry();
                            chemistry.setPlayerId(playerId);
                            chemistry.setComparePlayerId(comparePlayerId);
                            chemistry.setComparePosition(comparePosition);
                            double chemistryPercent = ChemistryPercentAnalyzer.getConnectionChemistryPercent(position, player, comparePosition, comparePlayer);
                            chemistry.setChemistryPercent(chemistryPercent);
                            // TODO jatka, pitäisi olla OK
                            chemistries.add(chemistry);
                        }
                    }
                }
                chemistryMap.put(position, chemistries);
            }
        }

        return chemistryMap;
    }

    public static void addChemistryToLine(Map<String, String> playersInLine, ArrayList<Pair<Map<String, String>, Integer>> playersInLines) {
        Set<String> set = new HashSet<>(playersInLine.keySet());
        boolean duplicatePlayers = set.size() < playersInLine.size();
        if (!duplicatePlayers) {
            int chemistryForLine = 0;
            // Collect chemistry sum in line
            for (Map.Entry<String, String> entry : playersInLine.entrySet()) {
                Position position = Position.fromDatabaseName(entry.getKey());
                String playerId = entry.getValue();
                for (Map.Entry<String, String> compareEntry : playersInLine.entrySet()) {
                    Position comparePosition = Position.fromDatabaseName(compareEntry.getKey());
                    String comparePlayerId = compareEntry.getValue();
                    if(!playerId.equals(comparePlayerId)) {
                        double chemistryPercent = ChemistryPercentAnalyzer.getChemistryPercent(position, AnalyzerService.playersInTeam.get(playerId), comparePosition, AnalyzerService.playersInTeam.get(comparePlayerId));
                        chemistryForLine += chemistryPercent;
                    }
                }
            }
            playersInLines.add(new Pair<>(playersInLine, chemistryForLine));
        }
    }

    /**
     * Get average chemistry percents of given chemistries
     *
     * @param chemistries list to calculate
     * @return line chemistry percent
     */
    public static double getAverageChemistryPercent(ArrayList<Chemistry> chemistries) {
        double chemistryCount = 0;
        double chemistrySize = chemistries.size();
        for (Chemistry chemistry : chemistries) {
            chemistryCount += chemistry.getChemistryPercent();
        }

        return chemistryCount / chemistrySize;
    }

}
