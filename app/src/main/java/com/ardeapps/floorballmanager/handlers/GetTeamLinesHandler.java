package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Line;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 11.7.2019.
 */
public interface GetTeamLinesHandler {
    void onTeamLinesLoaded(Map<String, ArrayList<Line>> lines);
}
