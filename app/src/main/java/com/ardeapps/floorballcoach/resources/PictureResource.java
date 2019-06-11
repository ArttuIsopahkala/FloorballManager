package com.ardeapps.floorballcoach.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballcoach.services.FirebaseStorageService;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Arttu on 26.1.2019.
 */

public class PictureResource extends FirebaseStorageService {
    private static PictureResource instance;
    private static StorageReference storage;

    public static PictureResource getInstance() {
        if (instance == null) {
            instance = new PictureResource();
        }
        storage = getStorage().child(PICTURES);
        return instance;
    }

    public void addPicture(String playerId, Bitmap bitmap, final AddBitmapListener handler) {
        addBitmap(storage.child(playerId), bitmap, handler);
    }

    public void removePicture(String playerId, final DeleteBitmapSuccessListener handler) {
        deleteBitmap(storage.child(playerId), handler);
    }

    public void getPicture(String playerId, final GetBitmapSuccessListener handler) {
        getBitmap(storage.child(playerId), handler);
    }

    public void getPictures(List<String> playerIds, final GetBitmapsSuccessListener handler) {
        getBitmaps(storage, playerIds, handler);
    }
}
