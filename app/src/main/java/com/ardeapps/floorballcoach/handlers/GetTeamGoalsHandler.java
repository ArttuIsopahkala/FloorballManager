package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 29.3.2019.
 */
public interface GetTeamGoalsHandler {
    void onTeamGoalsLoaded(Map<String, ArrayList<Goal>> goals);
}
