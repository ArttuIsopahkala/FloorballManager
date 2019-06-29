package com.ardeapps.floorballcoach.services;

import android.util.Log;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class JSONService extends FirebaseDatabaseService {

    private final static String PATH_TO_DB = System.getProperty("user.dir") + "/src/main/java/com/ardeapps/floorballcoach/database_dumbs/floorball-coach-export_28_6.json";

    // getPlayersOfLine("-LYDuaJHLYXZBTjVtWjK")
    protected ArrayList<String> getPlayersOfLine(String teamId, String lineId) {
        ArrayList<String> playerIds = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject linesObj = getJSONObject(root, LINES);

            Iterator<String> teams = linesObj.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(linesObj, teamKeyId);
                    Iterator<String> lines = teamObj.keys();
                    while (lines.hasNext()) {
                        String lineKeyId = lines.next();
                        if (lineId.equals(lineKeyId)) {
                            JSONObject lineObj = getJSONObject(teamObj, lineKeyId);
                            try {
                                Line line = new ObjectMapper().readValue(lineObj.toString(), Line.class);
                                if (line.getPlayerIdMap() != null) {
                                    playerIds.addAll(line.getPlayerIdMap().values());
                                }
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return playerIds;
    }

    protected ArrayList<Goal> getPlayerGoals(String playerId) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject statsPlayerGame = getJSONObject(root, STATS_PLAYER_GAME);

            Iterator<String> players = statsPlayerGame.keys();
            while (players.hasNext()) {
                String playerKeyId = players.next();
                if(playerKeyId.equals(playerId)) {
                    JSONObject gameObj = getJSONObject(statsPlayerGame, playerKeyId);

                    Iterator<String> games = gameObj.keys();
                    while (games.hasNext()) {
                        String gameId = games.next();
                        JSONObject goalsObj = getJSONObject(gameObj, gameId);

                        Iterator<String> goals = goalsObj.keys();
                        while (goals.hasNext()) {
                            String goalId = goals.next();
                            JSONObject value = getJSONObject(goalsObj, goalId);
                            try {
                                Goal goal = new ObjectMapper().readValue(value.toString(), Goal.class);
                                goalsList.add(goal);
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return goalsList;
    }

    protected ArrayList<Goal> getTeamGoals(String teamId) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject goalsTeamGame = getJSONObject(root, GOALS_TEAM_GAME);

            Iterator<String> teams = goalsTeamGame.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(goalsTeamGame, teamKeyId);

                    Iterator<String> games = teamObj.keys();
                    while (games.hasNext()) {
                        String gameId = games.next();
                        JSONObject gameObj = getJSONObject(teamObj, gameId);

                        Iterator<String> goals = gameObj.keys();
                        while (goals.hasNext()) {
                            String goalId = goals.next();
                            JSONObject value = getJSONObject(gameObj, goalId);

                            try {
                                Goal goal = new ObjectMapper().readValue(value.toString(), Goal.class);
                                goalsList.add(goal);
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return goalsList;
    }

    private static JSONObject convertToJSONObject(String json) {
        JSONObject obj;
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            obj = new JSONObject();
            Log.e("", "convertToJSONObject " + json);
        }
        return obj;
    }

    private static String readFileContent() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PATH_TO_DB));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                if(line != null) {
                    buffer.append(line+"\n");
                }
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String TAG = "JSONService";

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
