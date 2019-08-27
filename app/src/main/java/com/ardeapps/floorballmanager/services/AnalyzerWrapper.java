package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzerWrapper extends ChemistryPointsAnalyzer {

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

    /**
     * Get chemistries of players in line indexed by position.
     * Chemistries are calculated from one line's position to other positions.
     *
     * @param playersMap chemistries from this players are calculated
     * @return chemistries list indexed by playerId
     */
    public static Map<Position, ArrayList<Chemistry>> getChemistriesInLineForPositions(Map<String, String> playersMap) {
        Map<Position, ArrayList<Chemistry>> chemistryMap = new HashMap<>();

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
                    Pair<Position, String> player1 = new Pair<>(position, playerId);
                    Pair<Position, String> player2 = new Pair<>(comparedPosition, comparedPlayerId);
                    double points = ChemistryPointsAnalyzer.getChemistryPoints(player1, player2);
                    double chemistryPercent = ChemistryConnectionAnalyzer.getChemistryPercent(position, comparedPosition, points);
                    chemistry.setChemistryPercent(chemistryPercent);

                    chemistries.add(chemistry);
                }
            }

            chemistryMap.put(position, chemistries);
        }

        return chemistryMap;
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

            List<Position> closestPositions = Player.getClosestPositions().get(position);
            if(closestPositions != null) {
                ArrayList<Chemistry> filteredChemistries = new ArrayList<>();
                for (Chemistry chemistry : chemistries) {
                    if (closestPositions.contains(chemistry.getComparePosition())) {
                        filteredChemistries.add(chemistry);
                    }
                }
                filteredMap.put(position, filteredChemistries);
            }
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
    public static Map<Position, Double> getConvertCompareChemistryPercentsForPosition(Position position, Map<Position, ArrayList<Chemistry>> chemistryMap) {
        Map<Position, Double> chemistryPoints = new HashMap<>();
        ArrayList<Chemistry> chemistries = chemistryMap.get(position);
        if (chemistries != null) {
            for (Chemistry chemistry : chemistries) {
                Position comparePosition = chemistry.getComparePosition();
                chemistryPoints.put(comparePosition, chemistry.getChemistryPercent());
            }
        }

        return chemistryPoints;
    }

}
