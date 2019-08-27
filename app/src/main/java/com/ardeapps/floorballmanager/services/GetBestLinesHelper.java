package com.ardeapps.floorballmanager.services;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GetBestLinesHelper extends AnalyzerService {

    public static Map<String, String> convertToPlayerIdsMap(Map<Player.Position, Player> players) {
        Map<String, String> playerIdMap = new HashMap<>();
        for (Map.Entry<Player.Position, Player> entry : players.entrySet()) {
            Player.Position position = entry.getKey();
            Player player = entry.getValue();
            if(player != null) {
                playerIdMap.put(position.toDatabaseName(), player.getPlayerId());
            }
        }
        return playerIdMap;
    }

    public static Player getPlayerInIndex(int index, ArrayList<Player> players) {
        Player player = null;
        if(players != null && players.size() > index) {
            player = players.get(index);
        }
        return player;
    }

    public static Map<Position, ArrayList<Player>> getSortedBestPlayersForPosition(Map<Player, Double> chemistrySumsForPosition) {
        // Collect players for positions and sort by chemistry
        Map<Position, ArrayList<Pair<Player, Double>>> sortableBestPlayersForPosition = new HashMap<>();
        for (Map.Entry<Player, Double> entry : chemistrySumsForPosition.entrySet()) {
            Player player = entry.getKey();
            Position position = Position.fromDatabaseName(player.getPosition());
            double pointsSum = entry.getValue();

            ArrayList<Pair<Player, Double>> playerPairs = sortableBestPlayersForPosition.get(position);
            if(playerPairs == null) {
                playerPairs = new ArrayList<>();
            }
            playerPairs.add(new Pair<>(player, pointsSum));

            Collections.sort(playerPairs, (o1, o2) -> Double.compare(o2.second, o1.second));

            sortableBestPlayersForPosition.put(position, playerPairs);
        }

        // Convert pairs to list
        Map<Position, ArrayList<Player>> sortedBestPlayersForPosition = new HashMap<>();
        for (Map.Entry<Position, ArrayList<Pair<Player, Double>>> entry : sortableBestPlayersForPosition.entrySet()) {
            Position position = entry.getKey();
            ArrayList<Pair<Player, Double>> playerPairs = entry.getValue();
            ArrayList<Player> players = new ArrayList<>();
            for(Pair<Player, Double> playerPair : playerPairs) {
                players.add(playerPair.first);
            }
            sortedBestPlayersForPosition.put(position, players);
        }
        return sortedBestPlayersForPosition;
    }
}
