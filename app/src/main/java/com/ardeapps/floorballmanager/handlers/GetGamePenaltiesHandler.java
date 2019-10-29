package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Penalty;

import java.util.Map;

/**
 * Created by Arttu on 21.10.2019.
 */
public interface GetGamePenaltiesHandler {
    void onPenaltiesLoaded(Map<String, Penalty> penalties);
}
