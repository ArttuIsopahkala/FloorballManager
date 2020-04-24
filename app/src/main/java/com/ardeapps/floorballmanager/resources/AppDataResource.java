package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.handlers.GetAppDataHandler;
import com.ardeapps.floorballmanager.objects.AppData;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
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
            AppData.PRIVACY_POLICY_URL = (String) dataSnapshot.child("PRIVACY_POLICY_URL").getValue();
            AppData.GOOGLE_PLAY_APP_URL = (String) dataSnapshot.child("GOOGLE_PLAY_APP_URL").getValue();
            AppData.NEWEST_VERSION_CODE = (long) dataSnapshot.child("NEWEST_VERSION_CODE").getValue();
            AppData.NEWEST_VERSION_CODE_SUPPORTED = (long) dataSnapshot.child("NEWEST_VERSION_CODE_SUPPORTED").getValue();
            AppData.NEWEST_VERSION_CODE_SUPPORTED_TEXT = (String) dataSnapshot.child("NEWEST_VERSION_CODE_SUPPORTED_TEXT").getValue();
            handler.onAppDataLoaded();
        });
    }
}
