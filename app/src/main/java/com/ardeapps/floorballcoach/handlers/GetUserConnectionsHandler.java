package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.UserConnection;

import java.util.Map;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetUserConnectionsHandler {
    void onUserConnectionsLoaded(Map<String, UserConnection> userConnections);
}
