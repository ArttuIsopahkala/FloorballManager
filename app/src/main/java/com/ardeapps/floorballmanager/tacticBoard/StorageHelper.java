package com.ardeapps.floorballmanager.tacticBoard;

import android.os.Environment;

import com.ardeapps.floorballmanager.AppRes;

import java.io.File;
import java.util.ArrayList;

public class StorageHelper {

    private static final String DIR_NAME = "/FloorballRecordings/";
    private static final File EXTERNAL_DIR = Environment.getExternalStorageDirectory();
    private static final File INTERNAL_DIR = AppRes.getContext().getFilesDir();

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getExternalDirectory() {
        return EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME;
    }

    public static String getInternalDirectory() {
        return INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME;
    }

    public static boolean externalFileExists(String fileName){
        File file = new File(EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME + File.separator + fileName);
        return file.exists();
    }

    public static boolean internalFileExists(String fileName){
        File file = new File(INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME + File.separator + fileName);
        return file.exists();
    }

    public static ArrayList<File> getExternalFiles() {
        File dir = new File(EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME);
        return getFiles(dir);
    }

    public static ArrayList<File> getInternalFiles() {
        File dir = new File(INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME);
        return getFiles(dir);
    }

    private static ArrayList<File> getFiles(File dir) {
        ArrayList<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4")) {
                    result.add(file);
                }
            }
        }
        return result;
    }

}