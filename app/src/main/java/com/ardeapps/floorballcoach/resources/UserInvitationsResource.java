package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetTeamHandler;
import com.ardeapps.floorballcoach.handlers.GetUserInvitationsHandler;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.UserInvitation;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
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
        if(!userConnectionIds.isEmpty()) {
            final ArrayList<String> removedUserConnections = new ArrayList<>();
            for(final String userConnectionId : userConnectionIds)  {
                deleteData(database.child(userConnectionId), new DeleteDataSuccessListener() {
                    @Override
                    public void onDeleteDataSuccess() {
                        removedUserConnections.add(userConnectionId);
                        if(removedUserConnections.size() == userConnectionIds.size()) {
                            handler.onDeleteDataSuccess();
                        }
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
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, UserInvitation> userInvitations = new HashMap<>();
                final ArrayList<UserInvitation> foundUserInvitations = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final UserInvitation userInvitation = snapshot.getValue(UserInvitation.class);
                    if(userInvitation != null) {
                        // If emails matches
                        String email = AppRes.getInstance().getUser().getEmail();
                        String invitationEmail = userInvitation.getEmail();
                        if(email.equalsIgnoreCase(invitationEmail)) {
                            foundUserInvitations.add(userInvitation);
                        }
                    }
                }

                if(foundUserInvitations.isEmpty()) {
                    handler.onUserInvitationsLoaded(userInvitations);
                    return;
                }

                for(final UserInvitation userInvitation : foundUserInvitations) {
                    TeamsResource.getInstance().getTeam(userInvitation.getTeamId(), false, new GetTeamHandler() {
                        @Override
                        public void onTeamLoaded(Team team) {
                            userInvitation.setTeam(team);
                            userInvitations.put(userInvitation.getUserConnectionId(), userInvitation);

                            if(userInvitations.size() == foundUserInvitations.size()) {
                                handler.onUserInvitationsLoaded(userInvitations);
                            }
                        }
                    });
                }
            }
        });
    }
}
