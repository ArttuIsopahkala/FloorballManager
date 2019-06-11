package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Player;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetPlayersHandler {
    void onPlayersLoaded(Map<String, Player> players);
}
