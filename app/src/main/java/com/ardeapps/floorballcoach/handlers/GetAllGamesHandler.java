package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Game;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 31.7.2019.
 */
public interface GetAllGamesHandler {
    void onAllGamesLoaded(Map<String, ArrayList<Game>> games);
}
