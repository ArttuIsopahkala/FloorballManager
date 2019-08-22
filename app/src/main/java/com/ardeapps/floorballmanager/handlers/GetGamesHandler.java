package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Game;

import java.util.Map;

/**
 * Created by Arttu on 13.2.2019.
 */
public interface GetGamesHandler {
    void onGamesLoaded(Map<String, Game> games);
}
