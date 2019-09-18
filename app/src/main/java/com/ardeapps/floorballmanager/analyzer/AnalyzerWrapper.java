package com.ardeapps.floorballmanager.analyzer;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnalyzerWrapper {

    // TODO
    public static void addChemistryToLine(Map<String, String> playersInLine, ArrayList<Pair<Map<String, String>, Integer>> playersInLines) {
        Set<String> set = new HashSet<>(playersInLine.keySet());
        boolean duplicatePlayers = set.size() < playersInLine.size();
        if (!duplicatePlayers) {
            int chemistryForLine = 0;
            // Collect chemistry sum in line
            for (Map.Entry<String, String> entry : playersInLine.entrySet()) {
                Position position = Position.fromDatabaseName(entry.getKey());
                String playerId = entry.getValue();
                Player player = AnalyzerService.playersInTeam.get(playerId);
                for (Map.Entry<String, String> compareEntry : playersInLine.entrySet()) {
                    Position comparePosition = Position.fromDatabaseName(compareEntry.getKey());
                    String comparePlayerId = compareEntry.getValue();
                    Player comparePlayer = AnalyzerService.playersInTeam.get(comparePlayerId);
                    if(!playerId.equals(comparePlayerId)) {
                        /*double chemistryPercent = ChemistryPercentAnalyzer.getConnectionChemistryPercentOneToOne(position, player, comparePosition, comparePlayer);
                        chemistryForLine += chemistryPercent;*/
                    }
                }
            }
            playersInLines.add(new Pair<>(playersInLine, chemistryForLine));
        }
    }

}
