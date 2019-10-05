package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetUserConnectionsHandler;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class UserConnectionsResource extends FirebaseDatabaseService {
    private static UserConnectionsResource instance;
    private static DatabaseReference database;

    public static UserConnectionsResource getInstance() {
        if (instance == null) {
            instance = new UserConnectionsResource();
        }
        database = getDatabase().child(TEAMS_USER_CONNECTIONS);
        return instance;
    }

    public void addUserConnection(UserConnection userConnection, final AddDataSuccessListener handler) {
        userConnection.setUserConnectionId(database.push().getKey());
        addData(database.child(AppRes.getInstance().getSelectedTeam().getTeamId()).child(userConnection.getUserConnectionId()), userConnection, handler);
    }

    public void editUserConnection(UserConnection userConnection, final EditDataSuccessListener handler) {
        editData(database.child(AppRes.getInstance().getSelectedTeam().getTeamId()).child(userConnection.getUserConnectionId()), userConnection, handler);
    }

    public void removeUserConnection(String userConnectionId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(AppRes.getInstance().getSelectedTeam().getTeamId()).child(userConnectionId), handler);
    }

    public void removeUserConnections(String teamId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(teamId), handler);
    }

    public void getUserConnections(String teamId, final GetUserConnectionsHandler handler) {
        getData(database.child(teamId), dataSnapshot -> {
            Map<String, UserConnection> userConnections = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                UserConnection userConnection = snapshot.getValue(UserConnection.class);
                if (userConnection != null) {
                    userConnections.put(userConnection.getUserConnectionId(), userConnection);
                }
            }
            handler.onUserConnectionsLoaded(userConnections);
        });
    }

    // Called by invited user
    public void editUserConnectionAsInvited(UserInvitation userInvitation, final UserConnection.Status status, final String userId, final EditDataSuccessListener handler) {
        final String teamId = userInvitation.getTeamId();
        final String userConnectionId = userInvitation.getUserConnectionId();
        getData(database.child(teamId).child(userConnectionId), dataSnapshot -> {
            UserConnection userConnection = dataSnapshot.getValue(UserConnection.class);
            if (userConnection != null) {
                userConnection.setStatus(status.toDatabaseName());
                userConnection.setUserId(userId);
                editData(database.child(teamId).child(userConnectionId), userConnection, handler);
            }
        });
    }

    public void editUserConnectionAsRequest(UserRequest userRequest, final UserConnection.Status status, final EditDataSuccessListener handler) {
        final String teamId = userRequest.getTeamId();
        final String userConnectionId = userRequest.getUserConnectionId();
        getData(database.child(teamId).child(userConnectionId), dataSnapshot -> {
            UserConnection userConnection = dataSnapshot.getValue(UserConnection.class);
            if (userConnection != null) {
                userConnection.setStatus(status.toDatabaseName());
                editData(database.child(teamId).child(userConnectionId), userConnection, handler);
            }
        });
    }
}
