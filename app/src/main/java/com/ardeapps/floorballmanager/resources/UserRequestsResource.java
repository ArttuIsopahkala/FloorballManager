package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.handlers.GetUserRequestsHandler;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arttu on 4.10.2019.
 */

public class UserRequestsResource extends FirebaseDatabaseService {
    private static UserRequestsResource instance;
    private static DatabaseReference database;

    public static UserRequestsResource getInstance() {
        if (instance == null) {
            instance = new UserRequestsResource();
        }
        database = getDatabase().child(USER_REQUESTS);
        return instance;
    }

    public void addUserRequest(UserRequest userRequest, final AddDataSuccessListener handler) {
        userRequest.setUserConnectionId(database.push().getKey());
        addData(database.child(userRequest.getUserConnectionId()), userRequest, handler);
    }

    public void editUserRequest(UserRequest userRequest, final EditDataSuccessListener handler) {
        editData(database.child(userRequest.getUserConnectionId()), userRequest, handler);
    }

    public void removeUserRequest(String userRequestId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(userRequestId), handler);
    }

    public void removeUserRequests(final Set<String> userRequestIds, final DeleteDataSuccessListener handler) {
        if (!userRequestIds.isEmpty()) {
            final ArrayList<String> removedUserRequests = new ArrayList<>();
            for (final String userRequestId : userRequestIds) {
                deleteData(database.child(userRequestId), () -> {
                    removedUserRequests.add(userRequestId);
                    if (removedUserRequests.size() == userRequestIds.size()) {
                        handler.onDeleteDataSuccess();
                    }
                });
            }
        } else {
            handler.onDeleteDataSuccess();
        }
    }




    /**
     * Called by team member
     */
    public void getUserRequests(String teamId, final GetUserRequestsHandler handler) {
        getData(database, dataSnapshot -> {
            Map<String, UserRequest> userRequests = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                UserRequest userRequest = snapshot.getValue(UserRequest.class);
                if (userRequest != null) {
                    if(userRequest.getTeamId().equals(teamId)) {
                        userRequests.put(userRequest.getUserConnectionId(), userRequest);
                    }
                }
            }
            handler.onUserRequestsLoaded(userRequests);
        });
    }

    /**
     * Called by user
     */
    public void getUserRequestsAsUser(String userId, final GetUserRequestsHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, UserRequest> userRequests = new HashMap<>();
            ArrayList<UserRequest> foundUserRequests = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                UserRequest userRequest = snapshot.getValue(UserRequest.class);
                if (userRequest != null) {
                    if(userRequest.getUserId().equals(userId)) {
                        foundUserRequests.add(userRequest);
                    }
                }
            }

            if (foundUserRequests.isEmpty()) {
                handler.onUserRequestsLoaded(userRequests);
                return;
            }

            for (UserRequest userRequest : foundUserRequests) {
                TeamsResource.getInstance().getTeam(userRequest.getTeamId(), true, team -> {
                    userRequest.setTeam(team);
                    userRequests.put(userRequest.getUserConnectionId(), userRequest);

                    if (userRequests.size() == foundUserRequests.size()) {
                        handler.onUserRequestsLoaded(userRequests);
                    }
                });
            }
        });
    }
}
