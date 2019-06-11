package com.ardeapps.floorballcoach.views;

import android.content.Context;
import android.graphics.Typeface;

import com.ardeapps.floorballcoach.utils.Logger;

import java.util.Hashtable;

/**
 * Created by Arttu on 13.1.2019.
 */

public class Typefaces {
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Logger.log("Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}