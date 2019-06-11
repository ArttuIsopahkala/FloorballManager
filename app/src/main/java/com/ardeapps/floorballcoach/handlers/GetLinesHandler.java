package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.Map;

/**
 * Created by Arttu on 30.1.2019.
 */
public interface GetLinesHandler {
    void onLinesLoaded(Map<Integer, Line> lines);
}
