package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.User;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetUserHandler {
    void onUserLoaded(User user);
}
