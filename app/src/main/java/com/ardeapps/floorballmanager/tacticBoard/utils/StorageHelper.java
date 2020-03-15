package com.ardeapps.floorballmanager.tacticBoard.utils;

import android.os.Environment;

import com.ardeapps.floorballmanager.AppRes;

import java.io.File;
import java.util.ArrayList;

public class StorageHelper {

    private static final String DIR_NAME = "/FloorballManager/";
    private static final File EXTERNAL_DIR = Environment.getExternalStorageDirectory();
    private static final File INTERNAL_DIR = AppRes.getContext().getFilesDir();

    public static String getStoragePath() {
        String dirPath;
        if(StorageHelper.isExternalStorageWritable()) {
            dirPath = StorageHelper.getExternalDirectory();
        } else {
            dirPath = StorageHelper.getInternalDirectory();
        }
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    public static ArrayList<File> getStorageFiles() {
        if(StorageHelper.isExternalStorageWritable()) {
            return getExternalFiles();
        } else {
            return getInternalFiles();
        }
    }

    public static boolean isFileExists(String fileName) {
        if(StorageHelper.isExternalStorageWritable()) {
            return externalFileExists(fileName);
        } else {
            return internalFileExists(fileName);
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static String getExternalDirectory() {
        return EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME;
    }

    private static String getInternalDirectory() {
        return INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME;
    }

    private static boolean externalFileExists(String fileName){
        File file = new File(EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME + File.separator + fileName);
        return file.exists();
    }

    private static boolean internalFileExists(String fileName){
        File file = new File(INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME + File.separator + fileName);
        return file.exists();
    }

    private static ArrayList<File> getExternalFiles() {
        File dir = new File(EXTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME);
        return getFiles(dir);
    }

    private static ArrayList<File> getInternalFiles() {
        File dir = new File(INTERNAL_DIR.getAbsolutePath() + File.separator + DIR_NAME);
        return getFiles(dir);
    }

    private static ArrayList<File> getFiles(File dir) {
        ArrayList<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4") || file.getName().endsWith(".png")) {
                    result.add(file);
                }
            }
        }
        return result;
    }

}