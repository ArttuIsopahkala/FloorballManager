package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Goal;

import java.util.Map;

/**
 * Created by Arttu on 29.3.2019.
 */
public interface GetGameGoalsHandler {
    void onGoalsLoaded(Map<String, Goal> goals);
}
