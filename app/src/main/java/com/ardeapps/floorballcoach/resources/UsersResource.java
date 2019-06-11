package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.handlers.GetUserHandler;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class UsersResource extends FirebaseDatabaseService {
    private static UsersResource instance;
    private static DatabaseReference database;

    public static UsersResource getInstance() {
        if (instance == null) {
            instance = new UsersResource();
        }
        database = getDatabase().child(USERS);
        return instance;
    }

    public void editUser(User user, final EditDataSuccessListener handler) {
        editData(database.child(user.getUserId()), user, handler);
    }

    public void editUser(User user) {
        editData(database.child(user.getUserId()), user);
    }

    public void removeUser(String userId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(userId), handler);
    }

    public void getUser(String userId, final GetUserHandler handler) {
        getData(database.child(userId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                handler.onUserLoaded(user);
            }
        });
    }
}
