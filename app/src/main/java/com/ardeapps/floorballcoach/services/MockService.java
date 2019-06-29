package com.ardeapps.floorballcoach.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MockService {

    private static String PATH_TO_DB = System.getProperty("user.dir");

    public static void main(String[] args){

        System.out.println("Hello World");
        getGoals();
    }

    public static void getGoals() {

        JSONObject json = readFileContent();
        System.out.println(json);

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try {
            JsonObject object = (JsonObject) parser.parse(new FileReader(PATH_TO_DB + "/app/src/main/java/com/ardeapps/floorballcoach/database_dumbs/floorball-coach-export_28_6.json"));// response will be the json String
            JsonObject root = object.getAsJsonObject("DEBUG");
            System.out.println(root);
            /*JsonArray goalsTeamGame = root.getAsJsonObject("goalsTeamGame");

            final ArrayList<?> jsonArray = new Gson().fromJson(goalsTeamGame.toString(), ArrayList.class);

            System.out.println(jsonArray);*/

            //getJSONArray(object, "DEBUG");

        } catch (FileNotFoundException e) {
            System.out.println("not found");
        }
        /*try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("c:\\exer4-courses.json"));

            for (Object o : a) {
                JSONObject person = (JSONObject) o;

                String name = (String) person.get("name");
                System.out.println(name);

                String city = (String) person.get("city");
                System.out.println(city);

                String job = (String) person.get("job");
                System.out.println(job);

                JSONArray cars = (JSONArray) person.get("cars");

                for (Object c : cars) {
                    System.out.println(c + "");
                }
            }
        } catch (FileNotFoundException e) {

        }*/
    }

    private static JSONObject readFileContent() {
        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(PATH_TO_DB + "/app/src/main/java/com/ardeapps/floorballcoach/database_dumbs/floorball-coach-export_28_6.json"));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                if(line != null) {
                    buffer.append(line+"\n");
                    System.out.println(line);
                }
            }

            JSONObject json = convertToJSONObject(buffer.toString());
            return json;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String TAG = "LunchService";

    private String getNode(JSONObject object, String node) {
        String value = "";
        try {
            if (!object.isNull(node))
                value = decode(object.getString(node).trim());
        } catch (JSONException e) {
            Log.e(TAG, "getNodeError - " + node + " not found from " + object.toString());
        }
        return value;
    }

    private ArrayList<String> getArrayNode(JSONObject object, String node) {
        ArrayList<String> objects = new ArrayList<>();
        try {
            if (!object.isNull(node)) {
                JSONArray arrJson = object.getJSONArray(node);
                int length = arrJson.length();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        objects.add(arrJson.getString(i));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "getNodeError - " + node + " not found from " + object.toString());
        }
        return objects;
    }

    private static JSONObject convertToJSONObject(String json) {
        JSONObject obj;
        try {
            System.out.print(json);
            return new JSONObject(json);
        } catch (JSONException e) {
            obj = new JSONObject();
            Log.e(TAG, "convertToJSONObject " + json);
        }
        return obj;
    }

    private static JSONObject getJSONObject(JSONArray objects, int index) {
        JSONObject obj = new JSONObject();
        try {
            obj = objects.getJSONObject(index);
        } catch (JSONException e) {
            Log.e(TAG, "getJSONObjectError - index " + index + " not found from " + objects.toString());
        }
        return obj;
    }

    private static JSONObject getJSONObject(JSONObject object, String node) {
        JSONObject obj = new JSONObject();
        try {
            if (!object.isNull(node))
                obj = object.getJSONObject(node);
        } catch (JSONException e) {
            Log.e(TAG, "getJSONObjectError - " + node + " not found from " + object.toString());
        }
        return obj;
    }

    private static JSONArray getJSONArray(JSONObject object, String node) {
        JSONArray arr = new JSONArray();
        try {
            if (!object.isNull(node))
                arr = object.getJSONArray(node);
        } catch (JSONException e) {
            Log.e(TAG, "getJSONArrayError - " + node + " not found from " + object.toString());
        }
        return arr;
    }

    private String decode(String value) {
        return value.replace("&amp;", "&");
    }
}
