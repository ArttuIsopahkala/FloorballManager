package com.ardeapps.floorballcoach;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Arttu on 26.2.2018.
 */

public class PrefRes {
    // App
    private static final String APP_PREF = "appPref";
    public static final String APP_STARTED_FIRST_TIME = "appStartedFirstTime";
    public static final String IS_APP_VISIBLE = "isAppVisible";
    public static final String EMAIL = "email";
    // TODO mene tallennettuun team dashboardiin
    public static final String SELECTED_TEAM_ID = "selectedTeamId";

    private static SharedPreferences getSharedPref() {
        return AppRes.getContext().getSharedPreferences(APP_PREF, 0);
    }

    public static void clearPref() {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.clear();
        editor.apply();
    }

    public static void removePref(String key) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean containsKey(String key) {
        return getSharedPref().contains(key);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return getSharedPref().getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return getSharedPref().getBoolean(key, false);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key) {
        return getSharedPref().getLong(key, 0);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        return getSharedPref().getInt(key, 0);
    }

    public static void putStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static Set<String> getStringSet(String key) {
        Set<String> emptySet = new HashSet<>();
        return getSharedPref().getStringSet(key, emptySet);
    }
}
