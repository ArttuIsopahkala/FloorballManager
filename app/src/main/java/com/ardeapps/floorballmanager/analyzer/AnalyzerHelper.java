package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
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
}
