package com.ardeapps.floorballcoach.services;

import android.util.Log;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONService extends FirebaseDatabaseService {

    private final static String PATH_TO_DB = System.getProperty("user.dir") + "/src/main/java/com/ardeapps/floorballcoach/json-dump.json";

    public static Line getLine(String teamId, String lineId) {
        Line line = new Line();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject linesObj = getJSONObject(root, TEAMS_LINES);

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
                                line = new ObjectMapper().readValue(lineObj.toString(), Line.class);
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return line;
    }

    // getPlayersOfLine("-LYDuaJHLYXZBTjVtWjK")
    public static Map<String, String> getPlayersOfLine(String teamId, String lineId) {
        Map<String, String> playerIdMap = new HashMap<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject linesObj = getJSONObject(root, TEAMS_LINES);

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
                                    playerIdMap = line.getPlayerIdMap();
                                }
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return playerIdMap;
    }

    public static Map<String, ArrayList<Line>> getLinesOfGames(String teamId) {
        Map<String, ArrayList<Line>> linesGameIdMap = new HashMap<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsSeasonsGamesLines = getJSONObject(root, TEAMS_SEASONS_GAMES_LINES);

            Iterator<String> teams = teamsSeasonsGamesLines.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(teamsSeasonsGamesLines, teamKeyId);
                    Iterator<String> seasons = teamObj.keys();
                    while (seasons.hasNext()) {
                        String seasonId = seasons.next();
                        JSONObject seasonObj = getJSONObject(teamObj, seasonId);
                        Iterator<String> games = seasonObj.keys();
                        while (games.hasNext()) {
                            String gameKeyId = games.next();
                            JSONObject gameObj = getJSONObject(seasonObj, gameKeyId);
                            Iterator<String> lines = gameObj.keys();
                            ArrayList<Line> linesList = new ArrayList<>();
                            while (lines.hasNext()) {
                                String lineKeyId = lines.next();
                                JSONObject lineObj = getJSONObject(gameObj, lineKeyId);
                                try {
                                    Line line = new ObjectMapper().readValue(lineObj.toString(), Line.class);
                                    linesList.add(line);
                                } catch (IOException e) {}
                            }
                            linesGameIdMap.put(gameKeyId, linesList);
                        }
                    }
                }
            }
        }
        return linesGameIdMap;
    }

    public static Player getPlayer(String playerId) {
        return getPlayers(Arrays.asList(playerId)).get(0);
    }

    public static ArrayList<Player> getPlayers(List<String> playerIds) {
        ArrayList<Player> playerList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsNode = getJSONObject(root, TEAMS_PLAYERS);

            Iterator<String> teams = teamsNode.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                JSONObject teamObj = getJSONObject(teamsNode, teamKeyId);

                Iterator<String> players = teamObj.keys();
                while (players.hasNext()) {
                    String playerKeyId = players.next();
                    if(playerIds.contains(playerKeyId)) {
                        JSONObject value = getJSONObject(teamObj, playerKeyId);
                        try {
                            Player player = new ObjectMapper().readValue(value.toString(), Player.class);
                            playerList.add(player);
                        } catch (IOException e) {}
                    }
                }
            }
        }
        return playerList;
    }

    public static ArrayList<Player> getPlayers(String teamId) {
        ArrayList<Player> playerList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsNode = getJSONObject(root, TEAMS_PLAYERS);

            Iterator<String> teams = teamsNode.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(teamsNode, teamKeyId);

                    Iterator<String> players = teamObj.keys();
                    while (players.hasNext()) {
                        String playerKeyId = players.next();
                        JSONObject value = getJSONObject(teamObj, playerKeyId);
                        try {
                            Player player = new ObjectMapper().readValue(value.toString(), Player.class);
                            playerList.add(player);
                        } catch (IOException e) {}
                    }
                    break;
                }
            }
        }
        return playerList;
    }

    public static ArrayList<Goal> getPlayerGoals(String playerId) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsPlayersSeasonsGamesStats = getJSONObject(root, TEAMS_PLAYERS_SEASONS_GAMES_STATS);

            Iterator<String> teams = teamsPlayersSeasonsGamesStats.keys();
            while (teams.hasNext()) {
                String teamId = teams.next();
                JSONObject teamObj = getJSONObject(teamsPlayersSeasonsGamesStats, teamId);
                Iterator<String> players = teamObj.keys();
                while (players.hasNext()) {
                    String playerKeyId = players.next();
                    if(playerKeyId.equals(playerId)) {
                        JSONObject playerObj = getJSONObject(teamsPlayersSeasonsGamesStats, playerKeyId);
                        Iterator<String> seasons = playerObj.keys();
                        while (seasons.hasNext()) {
                            String seasonId = seasons.next();
                            JSONObject seasonObj = getJSONObject(playerObj, seasonId);

                            Iterator<String> games = seasonObj.keys();
                            while (games.hasNext()) {
                                String gameId = games.next();
                                JSONObject gameObj = getJSONObject(seasonObj, gameId);

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
            }
        }
        return goalsList;
    }

    public static ArrayList<Goal> getTeamGoals(String teamId) {
        ArrayList<Goal> goalsList = new ArrayList<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsSeasonsGamesGoals = getJSONObject(root, TEAMS_SEASONS_GAMES_GOALS);

            Iterator<String> teams = teamsSeasonsGamesGoals.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(teamsSeasonsGamesGoals, teamKeyId);
                    Iterator<String> seasons = teamObj.keys();
                    while (seasons.hasNext()) {
                        String seasonId = seasons.next();
                        JSONObject seasonObj = getJSONObject(teamObj, seasonId);
                        Iterator<String> games = seasonObj.keys();
                        while (games.hasNext()) {
                            String gameId = games.next();
                            JSONObject gameObj = getJSONObject(seasonObj, gameId);
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
        }
        return goalsList;
    }

    public static Map<String, ArrayList<Goal>> getTeamGoalsByGameId(String teamId) {
        Map<String, ArrayList<Goal>> goalsMap = new HashMap<>();
        String result = readFileContent();
        if(result != null) {
            JSONObject json = convertToJSONObject(result);
            JSONObject root = getJSONObject(json, DEBUG);
            JSONObject teamsSeasonsGamesGoals = getJSONObject(root, TEAMS_SEASONS_GAMES_GOALS);

            Iterator<String> teams = teamsSeasonsGamesGoals.keys();
            while (teams.hasNext()) {
                String teamKeyId = teams.next();
                if(teamKeyId.equals(teamId)) {
                    JSONObject teamObj = getJSONObject(teamsSeasonsGamesGoals, teamKeyId);
                    Iterator<String> seasons = teamObj.keys();
                    while (seasons.hasNext()) {
                        String seasonId = seasons.next();
                        JSONObject seasonObj = getJSONObject(teamObj, seasonId);
                        Iterator<String> games = seasonObj.keys();
                        while (games.hasNext()) {
                            String gameId = games.next();
                            JSONObject gameObj = getJSONObject(teamObj, gameId);

                            ArrayList<Goal> goalsList = new ArrayList<>();
                            Iterator<String> goals = gameObj.keys();
                            while (goals.hasNext()) {
                                String goalId = goals.next();
                                JSONObject value = getJSONObject(gameObj, goalId);

                                try {
                                    Goal goal = new ObjectMapper().readValue(value.toString(), Goal.class);
                                    goalsList.add(goal);
                                } catch (IOException e) {}
                            }
                            goalsMap.put(gameId, goalsList);
                        }
                    }
                }
            }
        }
        return goalsMap;
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
