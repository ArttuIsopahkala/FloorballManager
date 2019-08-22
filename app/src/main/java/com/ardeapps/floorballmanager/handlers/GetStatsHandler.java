package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Goal;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 27.7.2019.
 */
public interface GetStatsHandler {
    void onStatsLoaded(Map<String, ArrayList<Goal>> stats);
}
