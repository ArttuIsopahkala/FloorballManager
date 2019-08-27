package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Chemistry;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AnalyzerHelper {

    public static void addToLineCombinations(ArrayList<Pair<Map<String, String>, Integer>> lineCombinations, ArrayList<Pair<ArrayList<Map<String, String>>, Integer>> addedLineCombinations) {
        int chemistrySumForLines = 0;
        ArrayList<Pair<Map<String, String>, Integer>> playersAndPoints = new ArrayList<>();
        ArrayList<String> addedPlayerIds = new ArrayList<>();
        for(Pair<Map<String, String>, Integer> lineCombination : lineCombinations) {
            Map<String, String> playersInLine = lineCombination.first;

            boolean lineContainsAddedPlayer = false;
            for (String playerId : playersInLine.values()) {
                if (addedPlayerIds.contains(playerId)) {
                    lineContainsAddedPlayer = true;
                }
            }
            // Return if lines contains duplicated playerId
            if (lineContainsAddedPlayer) {
                return;
            }
            addedPlayerIds.addAll(playersInLine.values());

            Integer chemistrySum = lineCombination.second;
            chemistrySumForLines += chemistrySum;
            playersAndPoints.add(new Pair<>(playersInLine, chemistrySum));
        }
        // Sort lines by chemistry to best lines
        Collections.sort(playersAndPoints, (o1, o2) -> Integer.compare(o2.second, o1.second));
        ArrayList<Map<String, String>> playersList = new ArrayList<>();
        for(Pair<Map<String, String>, Integer> playersAndPoint : playersAndPoints) {
            playersList.add(playersAndPoint.first);
        }
        addedLineCombinations.add(new Pair<>(playersList, chemistrySumForLines));
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
