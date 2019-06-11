package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.Map;

/**
 * Created by Arttu on 30.1.2019.
 */
public interface SaveLinesHandler {
    void onLinesSaved(Map<Integer, Line> lines);
}
