package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetLinesHandler;
import com.ardeapps.floorballmanager.handlers.GetTeamLinesHandler;
import com.ardeapps.floorballmanager.handlers.SaveLinesHandler;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class GameLinesResource extends FirebaseDatabaseService {
    private static GameLinesResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static GameLinesResource getInstance() {
        if (instance == null) {
            instance = new GameLinesResource();
        }
        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_SEASONS_GAMES_LINES).child(teamId);
        return instance;
    }

    /**
     * Add or edit line ADD EVERY LINES AT ONE TIME
     */
    public void saveLines(String gameId, final Map<Integer, Line> lines, final SaveLinesHandler handler) {
        final Map<Integer, Line> savedLines = new HashMap<>();
        final ArrayList<Line> linesHandled = new ArrayList<>();
        for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
            Line line = entry.getValue();
            // Add, edit or remove line
            if (StringUtils.isEmptyString(line.getLineId())) {
                addLine(gameId, line, id -> {
                    line.setLineId(id);
                    savedLines.put(line.getLineNumber(), line);

                    linesHandled.add(line);
                    if (linesHandled.size() == lines.size()) {
                        handler.onLinesSaved(savedLines);
                    }
                });
            } else {
                if (line.getPlayerIdMap() == null || line.getPlayerIdMap().isEmpty()) {
                    removeLine(gameId, line.getLineId(), () -> {
                        linesHandled.add(line);
                        if (linesHandled.size() == lines.size()) {
                            handler.onLinesSaved(savedLines);
                        }
                    });
                } else {
                    editLine(gameId, line, () -> {
                        savedLines.put(line.getLineNumber(), line);
                        linesHandled.add(line);
                        if (linesHandled.size() == lines.size()) {
                            handler.onLinesSaved(savedLines);
                        }
                    });
                }
            }
        }
    }

    private void addLine(String gameId, Line line, final AddDataSuccessListener handler) {
        line.setLineId(database.child(seasonId).push().getKey());
        addData(database.child(seasonId).child(gameId).child(line.getLineId()), line, handler);
    }

    private void editLine(String gameId, Line line, final EditDataSuccessListener handler) {
        editData(database.child(seasonId).child(gameId).child(line.getLineId()), line, handler);
    }

    private void removeLine(String gameId, String lineId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId).child(lineId), handler);
    }

    public void removeLines(String gameId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId), handler);
    }

    public void removeAllLines(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get lines indexed by gameId
     */
    public void getAllLines(final GetTeamLinesHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, ArrayList<Line>> linesMap = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot game : season.getChildren()) {
                    String gameId = game.getKey();
                    if (gameId != null) {
                        ArrayList<Line> lines = new ArrayList<>();
                        for (DataSnapshot snapshot : game.getChildren()) {
                            Line line = snapshot.getValue(Line.class);
                            if (line != null) {
                                lines.add(line);
                            }
                        }
                        linesMap.put(gameId, lines);
                    }
                }
            }
            handler.onTeamLinesLoaded(linesMap);
        });
    }

    /**
     * Get lines indexed by line number
     */
    public void getLines(String gameId, final GetLinesHandler handler) {
        getData(database.child(seasonId).child(gameId), dataSnapshot -> {
            Map<Integer, Line> lines = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Line line = snapshot.getValue(Line.class);
                if (line != null) {
                    lines.put(line.getLineNumber(), line);
                }
            }
            handler.onLinesLoaded(lines);
        });
    }
}
