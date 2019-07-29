package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.Season;

import java.util.Map;

/**
 * Created by Arttu on 28.7.2019.
 */
public interface GetSeasonsHandler {
    void onSeasonsLoaded(Map<String, Season> seasons);
}
