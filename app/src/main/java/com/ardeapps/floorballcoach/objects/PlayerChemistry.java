package com.ardeapps.floorballcoach.objects;

import java.util.HashMap;
import java.util.Map;

public class PlayerChemistry {

    private String playerId;
    private Map<String, Integer> comparePlayers;

    public PlayerChemistry() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Map<String, Integer> getComparePlayers() {
        if(comparePlayers == null) {
            comparePlayers = new HashMap<>();
        }
        return comparePlayers;
    }

    public void setComparePlayers(Map<String, Integer> comparePlayers) {
        this.comparePlayers = comparePlayers;
    }
}
