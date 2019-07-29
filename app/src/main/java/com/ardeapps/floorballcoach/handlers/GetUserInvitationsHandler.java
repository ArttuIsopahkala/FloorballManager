package com.ardeapps.floorballcoach.handlers;

import com.ardeapps.floorballcoach.objects.UserInvitation;

import java.util.Map;

/**
 * Created by Arttu on 20.7.2019.
 */
public interface GetUserInvitationsHandler {
    void onUserInvitationsLoaded(Map<String, UserInvitation> userInvitations);
}
