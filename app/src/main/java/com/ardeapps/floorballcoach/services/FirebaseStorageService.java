package com.ardeapps.floorballcoach.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.BuildConfig;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.Loader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseStorageService {

    private static final String DEBUG = "DEBUG";
    private static final String RELEASE = "RELEASE";
    protected static final String LOGOS = "LOGOS";
    protected static final String PICTURES = "PICTURES";

    private final long SIZE480PX = 480 * 480;
    private final long ONE_MEGABYTE = 1024 * 1024;

    protected static StorageReference getStorage() {
        if (BuildConfig.DEBUG) {
            return FirebaseStorage.getInstance().getReference(DEBUG);
        } else {
            return FirebaseStorage.getInstance().getReference(RELEASE);
        }
    }

    private static void onNetworkError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_network);
    }

    private static void onDatabaseError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_database);
    }

    private static void onDataNotFoundError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_data_not_found);
    }

    private static void logAction() {
        String callingClass = Thread.currentThread().getStackTrace()[4].getFileName();
        int lineNumber = Thread.currentThread().getStackTrace()[4].getLineNumber();
        String callingMethod = Thread.currentThread().getStackTrace()[4].getMethodName();
        Logger.log(callingClass + ":" + lineNumber + " - " + callingMethod);
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppRes.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected static void addBitmap(StorageReference storage, Bitmap bitmap, final AddBitmapListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storage.putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Loader.hide();
                    if(task.isSuccessful()) {
                        handler.onAddBitmapSuccess();
                    } else {
                        Logger.toast(R.string.error_service_action);
                    }
                }
            });
        } else onNetworkError();
    }

    protected void getBitmap(StorageReference storage, final GetBitmapSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            storage.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    Loader.hide();
                    Bitmap bitmap = null;
                    if(task.isSuccessful()) {
                        byte[] bytes = task.getResult();
                        if(bytes != null) {
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        }
                    }
                    handler.onGetBitmapSuccess(bitmap);
                }
            });
        } else onNetworkError();
    }

    protected void getBitmaps(StorageReference storage, final List<String> ids, final GetBitmapsSuccessListener handler) {
        logAction();
        final Map<String, Bitmap> bitmaps = new HashMap<>();
        if(ids.size() > 0) {
            Loader.show();
            for (final String id : ids) {
                storage.child(id).getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        Bitmap bitmap = null;
                        if(task.isSuccessful()) {
                            byte[] bytes = task.getResult();
                            if(bytes != null) {
                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            }
                        }
                        bitmaps.put(id, bitmap);
                        if (bitmaps.size() == ids.size()) {
                            Loader.hide();
                            handler.onGetBitmapsSuccess(bitmaps);
                        }
                    }
                });
            }
        } else {
            handler.onGetBitmapsSuccess(bitmaps);
        }
    }

    protected void deleteBitmap(StorageReference storage, final DeleteBitmapSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Loader.hide();
                    if(task.isSuccessful()) {
                        handler.onDeleteBitmapSuccess();
                    } else {
                        Logger.toast(R.string.error_service_action);
                    }
                }
            });
        } else onNetworkError();
    }

    public interface AddBitmapListener {
        void onAddBitmapSuccess();
    }

    public interface GetBitmapSuccessListener {
        void onGetBitmapSuccess(Bitmap bitmap);
    }

    public interface GetBitmapsSuccessListener {
        void onGetBitmapsSuccess(Map<String, Bitmap> bitmaps);
    }

    public interface DeleteBitmapSuccessListener {
        void onDeleteBitmapSuccess();
    }
}
