package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.handlers.GetUserHandler;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
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
        getData(database.child(userId), dataSnapshot -> {
            User user = dataSnapshot.getValue(User.class);
            handler.onUserLoaded(user);
        });
    }

    /*public void getUserByEmail(String email, final GetUserHandler handler) {
        getData(database.orderByChild("email").equalTo(email).limitToFirst(1), dataSnapshot -> {
            if (dataSnapshot.getChildren().iterator().hasNext()) {
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                User user = snapshot.getValue(User.class);
                handler.onUserLoaded(user);
            } else {
                handler.onUserLoaded(null);
            }
        });
    }*/
}
