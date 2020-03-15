package com.ardeapps.floorballmanager.tacticBoard.media;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.tacticBoard.utils.StorageHelper;
import com.ardeapps.floorballmanager.utils.Logger;

import java.io.IOException;

import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_RECORD_SCREEN;

@TargetApi(21)
public class AnimationRecorder {

    private static int mScreenDensity;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1080;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private Surface surface;

    private static AnimationRecorder instance;

    public static AnimationRecorder getInstance() {
        if (instance == null) {
            instance = new AnimationRecorder();
            DisplayMetrics metrics = new DisplayMetrics();
            AppRes.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = metrics.densityDpi;
        }

        return instance;
    }

    public interface StartCaptureHandler {
        void onAskScreenCapturePermission(Intent intent);
    }

    public boolean isPermissionGiven(StartCaptureHandler handler) {
        if (ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(AppRes.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AppRes.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_ACCESS_RECORD_SCREEN);
            Logger.log("Ask permission to record screen");
            return false;
        }

        if (mMediaProjection == null) {
            MediaProjectionManager mProjectionManager = (MediaProjectionManager) AppRes.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if(mProjectionManager != null) {
                handler.onAskScreenCapturePermission(mProjectionManager.createScreenCaptureIntent());
            }
            return false;
        }
        return true;
    }

    public void setPermissionGranted(int resultCode, Intent data) {
        MediaProjectionManager mProjectionManager = (MediaProjectionManager) AppRes.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if(mProjectionManager != null) {
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            mMediaProjection.registerCallback(new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    try {
                        mMediaRecorder.stop();
                    } catch (RuntimeException stopException) {
                        Logger.log("RECORDER STOP FAILED");
                    }
                    mMediaRecorder.reset();
                    Logger.log("Recording Stopped");
                    mMediaProjection = null;
                    Logger.log("MediaProjection Stopped");
                }
            }, null);
        }
    }

    public void prepareRecorder(String fileName) {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        mMediaRecorder.setVideoFrameRate(30);

        String storagePath = StorageHelper.getStoragePath();
        String file = storagePath + fileName + ".mp4";
        mMediaRecorder.setOutputFile(file);
        Logger.log("Recorder file will be saved to: " + file);

        try {
            mMediaRecorder.prepare();
            if(surface == null) {
                surface = mMediaRecorder.getSurface();
            }
        } catch (IllegalStateException | IOException e) {
            Logger.log("MEDIA RECORDER PREPARE FAILED " + e.getMessage());
        }
    }

    public void startRecording() {
        Logger.log("startRecording");
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    public void stopRecording() {
        Logger.log("stopRecording");
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
    }

    public boolean isRecording() {
        return mMediaProjection != null;
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("FieldRecordView",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface, null, null);
    }

}
