package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.Penalty;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 21.10.2019.
 */
public interface GetPenaltiesHandler {
    void onPenaltiesLoaded(Map<String, ArrayList<Penalty>> penalties);
}
