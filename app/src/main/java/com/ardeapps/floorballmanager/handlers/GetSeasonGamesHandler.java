package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Game;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 10.3.2020.
 */
public interface GetSeasonGamesHandler {
    void onSeasonGamesLoaded(Map<String, ArrayList<Game>> seasonGames);
}
