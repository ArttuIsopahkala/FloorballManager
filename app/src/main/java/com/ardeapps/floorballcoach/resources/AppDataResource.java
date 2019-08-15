package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.handlers.GetAppDataHandler;
import com.ardeapps.floorballcoach.objects.AppData;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Arttu on 19.1.2018.
 */

public class AppDataResource extends FirebaseDatabaseService {
    private static AppDataResource instance;
    private static DatabaseReference database;

    public static AppDataResource getInstance() {
        if (instance == null) {
            instance = new AppDataResource();
        }
        database = getDatabase().child(APP_DATA);
        return instance;
    }

    public void getAppData(final GetAppDataHandler handler) {
        getData(database, dataSnapshot -> {
            AppData.PRIVACY_POLICY_URL = (String)dataSnapshot.child("PRIVACY_POLICY_URL").getValue();
            AppData.NEWEST_VERSION_CODE = (long)dataSnapshot.child("NEWEST_VERSION_CODE").getValue();
            handler.onAppDataLoaded();
        });
    }
}
