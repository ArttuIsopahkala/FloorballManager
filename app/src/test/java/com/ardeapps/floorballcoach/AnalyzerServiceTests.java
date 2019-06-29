package com.ardeapps.floorballcoach;

import android.util.Log;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class AnalyzerServiceTests {
    @Test
    public void isChemistryPointsCorrect() {
        /*
        for(Goal goal : getGoals()) {
            System.out.println(goal.getGoalId());
        }
        */

        int testChemistry = AnalyzerService.getChemistryPoints("-LYDueLQnDlNS3iny3r2", "-LZf423Q01lgFW8yD5Dl", getGoals());
        System.out.println(testChemistry);

        String bestAssister = AnalyzerService.getBestAssistantForScorer("-LYDueLQnDlNS3iny3r2", getGoals());
        System.out.println(bestAssister);

    }

    // getPlayersOfLine("-LYDuaJHLYXZBTjVtWjK")
    public ArrayList<String> getPlayersOfLine(String lineId) {
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, "DEBUG");
            JSONObject linesObj = getJSONObject(root, "lines");

            Iterator<String> teams = linesObj.keys();
            while (teams.hasNext()) {
                String teamId = teams.next();
                JSONObject teamObj = getJSONObject(linesObj, teamId);

                Iterator<String> lines = teamObj.keys();
                while (lines.hasNext()) {
                    String id = lines.next();
                    if(lineId.equals(id)) {
                        JSONObject lineObj = getJSONObject(teamObj, id);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            Line line = objectMapper.readValue(lineObj.toString(), Line.class);
                            if(line.getPlayerIdMap() != null) {
                                return new ArrayList<>(line.getPlayerIdMap().values());
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public ArrayList<Goal> getGoals() {
        ArrayList<Goal> goalsList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, "DEBUG");
            JSONObject goalsTeamGame = getJSONObject(root, "goalsTeamGame");

            Iterator<String> teams = goalsTeamGame.keys();
            while (teams.hasNext()) {
                String teamId = teams.next();
                JSONObject teamObj = getJSONObject(goalsTeamGame, teamId);

                Iterator<String> games = teamObj.keys();
                while (games.hasNext()) {
                    String gameId = games.next();
                    JSONObject gameObj = getJSONObject(teamObj, gameId);

                    Iterator<String> goals = gameObj.keys();
                    while (goals.hasNext()) {
                        String goalId = goals.next();
                        JSONObject value = getJSONObject(gameObj, goalId);

                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            Goal goal = objectMapper.readValue(value.toString(), Goal.class);
                            goalsList.add(goal);
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        return goalsList;
    }

    public static JSONObject convertToJSONObject(String json) {
        JSONObject obj;
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            obj = new JSONObject();
            Log.e("", "convertToJSONObject " + json);
        }
        return obj;
    }

    private static String PATH_TO_DB = System.getProperty("user.dir") + "/src/main/java/com/ardeapps/floorballcoach/database_dumbs/floorball-coach-export_28_6.json";

    public static String readFileContent() {
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

    private static String TAG = "AnalyzerServiceTest";

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

