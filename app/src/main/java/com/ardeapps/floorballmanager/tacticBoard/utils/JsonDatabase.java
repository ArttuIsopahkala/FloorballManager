package com.ardeapps.floorballmanager.tacticBoard.utils;

import com.ardeapps.floorballmanager.tacticBoard.objects.ExportField;
import com.ardeapps.floorballmanager.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class JsonDatabase {

    /**
     * animatedFields: [
     *  field: {
     *      players: [
     *          x, y, playerId, positions
     *      ],
     *      balls: [
     *          x, y, id, positions
     *      ]
     *  }
     * ],
     * drawingFields: [
     * field: {
     *   lines: [
     *      Path,
     *      Path
     *   ],
     *   arrows: [],
     *   dotted_arrows: [],
     *   circles: [],
     *   crosses: []
     * },
     * field: {}
     * ]
     *
     **/
    private static final String JSON_NAME = "database.json";
    private static final String filePath = StorageHelper.getStoragePath() + File.separator + JSON_NAME;

    public static ArrayList<ExportField> getSavedFields() {
        try {
            FileReader json = new FileReader(filePath);

            Gson gson = new GsonBuilder().create();
            ArrayList<ExportField> fields = gson.fromJson(json, new TypeToken<ArrayList<ExportField>>(){}.getType());
            return fields == null ? new ArrayList<>() : fields;
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveField(ExportField field) {
        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Logger.log("Cannot create database: " + e);
                Logger.toast("Cannot create database.");
                return;
            }
        }

        ArrayList<ExportField> fields = getSavedFields();
        fields.add(field);

        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(fields, writer);
        } catch (IOException e) {
            Logger.log("Error when saving json object: " + e);
            Logger.toast("Error when saving json object.");
        }
    }
}