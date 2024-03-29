package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetUserInvitationsHandler;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arttu on 19.1.2018.
 */

public class UserInvitationsResource extends FirebaseDatabaseService {
    private static UserInvitationsResource instance;
    private static DatabaseReference database;

    public static UserInvitationsResource getInstance() {
        if (instance == null) {
            instance = new UserInvitationsResource();
        }
        database = getDatabase().child(USER_INVITATIONS);
        return instance;
    }

    public void sendUserInvitation(UserInvitation userInvitation, final EditDataSuccessListener handler) {
        editData(database.child(userInvitation.getUserConnectionId()), userInvitation, handler);
    }

    public void removeUserInvitation(String userConnectionId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(userConnectionId), handler);
    }

    public void removeUserInvitations(final Set<String> userConnectionIds, final DeleteDataSuccessListener handler) {
        if (!userConnectionIds.isEmpty()) {
            final ArrayList<String> removedUserConnections = new ArrayList<>();
            for (final String userConnectionId : userConnectionIds) {
                deleteData(database.child(userConnectionId), () -> {
                    removedUserConnections.add(userConnectionId);
                    if (removedUserConnections.size() == userConnectionIds.size()) {
                        handler.onDeleteDataSuccess();
                    }
                });
            }
        } else {
            handler.onDeleteDataSuccess();
        }
    }

    /**
     * Get user invitations which has same email than caller user, indexed by userConnectionId.
     */
    public void getUserInvitations(final GetUserInvitationsHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, UserInvitation> userInvitations = new HashMap<>();
            final ArrayList<UserInvitation> foundUserInvitations = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final UserInvitation userInvitation = snapshot.getValue(UserInvitation.class);
                if (userInvitation != null) {
                    // If emails matches
                    String email = AppRes.getInstance().getUser().getEmail();
                    String invitationEmail = userInvitation.getEmail();
                    if (email.equalsIgnoreCase(invitationEmail)) {
                        foundUserInvitations.add(userInvitation);
                    }
                }
            }

            if (foundUserInvitations.isEmpty()) {
                handler.onUserInvitationsLoaded(userInvitations);
                return;
            }

            for (final UserInvitation userInvitation : foundUserInvitations) {
                TeamsResource.getInstance().getTeam(userInvitation.getTeamId(), true, team -> {
                    userInvitation.setTeam(team);
                    userInvitations.put(userInvitation.getUserConnectionId(), userInvitation);

                    if (userInvitations.size() == foundUserInvitations.size()) {
                        handler.onUserInvitationsLoaded(userInvitations);
                    }
                });
            }
        });
    }
}
