package com.ardeapps.floorballmanager.handlers;

import com.ardeapps.floorballmanager.objects.UserRequest;

import java.util.Map;

/**
 * Created by Arttu on 4.10.2019.
 */
public interface GetUserRequestsHandler {
    void onUserRequestsLoaded(Map<String, UserRequest> userRequests);
}
