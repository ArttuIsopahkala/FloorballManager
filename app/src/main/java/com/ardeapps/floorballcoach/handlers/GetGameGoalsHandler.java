package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.Map;

/**
 * Created by Arttu on 29.3.2019.
 */
public interface GetGameGoalsHandler {
    void onGoalsLoaded(Map<String, Goal> goals);
}
