package com.ardeapps.floorballmanager.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballmanager.services.FirebaseStorageService;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Arttu on 26.1.2019.
 */

public class LogoResource extends FirebaseStorageService {
    private static LogoResource instance;
    private static StorageReference storage;

    public static LogoResource getInstance() {
        if (instance == null) {
            instance = new LogoResource();
        }
        storage = getStorage().child(LOGOS);
        return instance;
    }

    public void addLogo(String teamId, Bitmap bitmap, final AddBitmapListener handler) {
        addBitmap(storage.child(teamId), bitmap, handler);
    }

    public void removeLogo(String teamId, final DeleteBitmapSuccessListener handler) {
        deleteBitmap(storage.child(teamId), handler);
    }

    public void getLogo(String teamId, final GetBitmapSuccessListener handler) {
        getBitmap(storage.child(teamId), handler);
    }

    public void getLogos(List<String> teamIds, final GetBitmapsSuccessListener handler) {
        getBitmaps(storage, teamIds, handler);
    }
}
