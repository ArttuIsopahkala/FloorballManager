package com.ardeapps.floorballmanager.tacticBoard.media;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.MainActivity;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;

import java.nio.ByteBuffer;

import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_RECORD_SCREEN;

@TargetApi(21)
public class ScreenshotRecorder {

    public final static int TIMEOUT = 5000;

    private Runnable timeoutRunnable;

    private MediaProjection mMediaProjection;
    private static int mScreenDensity;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1080;
    private static ScreenshotRecorder instance;

    public static ScreenshotRecorder getInstance() {
        if (instance == null) {
            instance = new ScreenshotRecorder();
            DisplayMetrics metrics = new DisplayMetrics();
            AppRes.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = metrics.densityDpi;
        }

        return instance;
    }

    public interface StartCaptureHandler {
        void onAskScreenshotPermission(Intent intent);
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
                Intent intent = new Intent(AppRes.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                handler.onAskScreenshotPermission(intent);
                // TODO ask this
                //AppRes.getContext().startActivity(new Intent(AppRes.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            return false;
        }
        return true;
    }

    public void setPermissionGranted(int resultCode, Intent data) {
        MediaProjectionManager mProjectionManager = (MediaProjectionManager) AppRes.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if(mProjectionManager != null) {
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        }
    }

    public void takeScreenshot(long delay, OnScreenshotHandlerCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                takeScreenshot(callback);
            }
        }, delay);
    }

    public void takeScreenshot(OnScreenshotHandlerCallback callback) {
        Logger.log("Start screenshot");
        final long screenshotStartTime = System.currentTimeMillis();

        // http://binwaheed.blogspot.tw/2015/03/how-to-correctly-take-screenshot-using.html
        // Get size of screen
        WindowManager wm = (WindowManager) AppRes.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Point size = new Point();
        display.getRealSize(size);
        final int mWidth = size.x;
        final int mHeight = size.y;
        int mDensity = metrics.densityDpi;
        final boolean isPortrait = mHeight > mWidth;

        //Create a imageReader for catch result
        final ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);

        final Handler handler = new Handler();

        //Take a screenshot
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
        mMediaProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, handler);

        //convert result into image
        Logger.log("add setOnImageAvailableListener");
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                mImageReader.setOnImageAvailableListener(null, handler);
                mImageReader.close();
                mMediaProjection.stop();

                Logger.log("Screenshot timeout");
                if (callback != null) {
                    callback.onScreenshotFailed(null);
                }
            }
        };
        handler.postDelayed(timeoutRunnable, TIMEOUT);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                reader.setOnImageAvailableListener(null, handler);
                Logger.log("onImageAvailable");
                Image image = null;
                Bitmap tempBmp = null;
                Bitmap realSizeBitmap = null;
                int[] size = null;
                Throwable error = null;

                try {
                    image = reader.acquireLatestImage();
//                    throw new UnsupportedOperationException("The producer output buffer format 0x5 doesn't match the ImageReader's configured buffer format 0x1.");
                    Logger.log("screenshot image info: width:" + image.getWidth() + " height:" + image.getHeight());
                    int deviceWidth = metrics.widthPixels;
                    int deviceHeight = metrics.heightPixels;
                    if (deviceHeight > deviceWidth != isPortrait) {
                        Logger.log("Height & width ratio is not match orientation, swap height & width");
                        //noinspection SuspiciousNameCombination
                        deviceWidth = metrics.heightPixels;
                        //noinspection SuspiciousNameCombination
                        deviceHeight = metrics.widthPixels;
                    }

                    int statusBarHeight = Helper.getStatusBarHeight();
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * deviceWidth;
                    // create bitmap
                    tempBmp = Bitmap.createBitmap(
                            deviceWidth + (int) ((float) rowPadding / (float) pixelStride),
                            deviceHeight + statusBarHeight, Bitmap.Config.ARGB_8888);
                    tempBmp.copyPixelsFromBuffer(buffer);

                    realSizeBitmap = Bitmap.createBitmap(tempBmp, 0, statusBarHeight,
                            deviceWidth, tempBmp.getHeight() - statusBarHeight);

                    Logger.log("Screenshot width: " + realSizeBitmap.getWidth() + " height: " + realSizeBitmap.getHeight());
                    //saveBmpToFile(realSizeBitmap, fileName);
                } catch (Throwable e) {
                    Logger.log("Screenshot failed " + e);
                    error = e;
                } finally {
                    if (image != null) {
                        image.close();
                    }
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Logger.log("Screenshot failed " + e);
                        error = e;
                    } finally {
                        mMediaProjection.stop();
                        if (tempBmp != null) {
                            tempBmp.recycle();
                        }
                        if (realSizeBitmap != null) {
                            realSizeBitmap.recycle();
                        }
                    }
                }

                if (timeoutRunnable != null) {
                    handler.removeCallbacks(timeoutRunnable);
                    timeoutRunnable = null;
                }
                if (error == null) {
                    long spentTime = System.currentTimeMillis() - screenshotStartTime;
                    Logger.log("Screenshot finished, spent: " + spentTime + " ms");
                    callback.onScreenshotFinished(realSizeBitmap);
                } else {
                    callback.onScreenshotFailed(error);
                }
            }
        }, handler);
    }

    public interface OnScreenshotHandlerCallback {
        void onScreenshotFinished(Bitmap screenshot);
        void onScreenshotFailed(@Nullable Throwable e);
    }
}
