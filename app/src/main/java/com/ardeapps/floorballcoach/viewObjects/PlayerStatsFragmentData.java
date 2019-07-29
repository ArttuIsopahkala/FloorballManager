package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerStatsFragmentData {

    private Player player;
    private Map<String, ArrayList<Goal>> stats;
    private Map<String, Game> games;

    public PlayerStatsFragmentData() {
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<String, ArrayList<Goal>> getStats() {
        if(stats == null) {
            stats = new HashMap<>();
        }
        return stats;
    }

    public void setStats(Map<String, ArrayList<Goal>> stats) {
        this.stats = stats;
    }

    public Map<String, Game> getGames() {
        if(games == null) {
            games = new HashMap<>();
        }
        return games;
    }

    public void setGames(Map<String, Game> games) {
        this.games = games;
    }
}
