package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Team;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetTeamsHandler {
    void onTeamsLoaded(Map<String, Team> teams);
}
