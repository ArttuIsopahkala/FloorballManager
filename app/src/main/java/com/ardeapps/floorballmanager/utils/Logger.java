package com.ardeapps.floorballmanager.utils;

import android.util.Log;
import android.widget.Toast;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.BuildConfig;

/**
 * Created by Arttu on 21.8.2017.
 */

public class Logger {

    public static void logInfo(Object message) {
        if (BuildConfig.DEBUG) {
            String className = new Exception().getStackTrace()[1].getFileName();
            Log.i(className, message + "");
        }
    }

    public static void log(Object message) {
        if (BuildConfig.DEBUG) {
            String className = new Exception().getStackTrace()[1].getFileName();
            Log.e(className, message + "");
        }
    }

    public static void toast(Object message) {
        Toast.makeText(AppRes.getContext(), message + "", Toast.LENGTH_LONG).show();
    }

    public static void toast(int resourceId) {
        Toast.makeText(AppRes.getContext(), resourceId, Toast.LENGTH_LONG).show();
    }
}
