package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Goal;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 29.3.2019.
 */
public interface GetGoalsHandler {
    void onGoalsLoaded(Map<String, ArrayList<Goal>> goals);
}
