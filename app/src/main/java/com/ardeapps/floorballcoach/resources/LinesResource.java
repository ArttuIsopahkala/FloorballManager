package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetLinesHandler;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class LinesResource extends FirebaseDatabaseService {
    private static LinesResource instance;
    private static DatabaseReference database;

    public static LinesResource getInstance() {
        if (instance == null) {
            instance = new LinesResource();
        }
        database = getDatabase().child(TEAMS_LINES).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    public void saveLines(final Map<Integer, Line> lines, final SaveLinesHandler handler) {
        final Map<Integer, Line> savedLines = new HashMap<>();
        final ArrayList<Line> linesHandled = new ArrayList<>();

        for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
            final Line line = entry.getValue();

            // Add, edit or remove line
            if(StringUtils.isEmptyString(line.getLineId())) {
                addLine(line, id -> {
                    line.setLineId(id);
                    savedLines.put(line.getLineNumber(), line);

                    linesHandled.add(line);
                    if(linesHandled.size() == lines.size()) {
                        handler.onLinesSaved(savedLines);
                    }
                });
            } else {
                if(line.getPlayerIdMap() == null || line.getPlayerIdMap().isEmpty()) {
                    removeLine(line.getLineId(), () -> {
                        linesHandled.add(line);
                        if(linesHandled.size() == lines.size()) {
                            handler.onLinesSaved(savedLines);
                        }
                    });
                } else {
                    editLine(line, () -> {
                        savedLines.put(line.getLineNumber(), line);
                        linesHandled.add(line);
                        if(linesHandled.size() == lines.size()) {
                            handler.onLinesSaved(savedLines);
                        }
                    });
                }
            }
        }
    }

    private void addLine(Line line, final AddDataSuccessListener handler) {
        line.setLineId(database.push().getKey());
        addData(database.child(line.getLineId()), line, handler);
    }

    private void editLine(Line line, final EditDataSuccessListener handler) {
        editData(database.child(line.getLineId()), line, handler);
    }

    private void removeLine(String lineId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(lineId), handler);
    }

    public void removeAllLines(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get lines indexed by line number
     */
    public void getLines(final GetLinesHandler handler) {
        getData(database, dataSnapshot -> {
            Map<Integer, Line> lines = new HashMap<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Line line = snapshot.getValue(Line.class);
                if(line != null) {
                    lines.put(line.getLineNumber(), line);
                }
            }
            handler.onLinesLoaded(lines);
        });
    }
}
