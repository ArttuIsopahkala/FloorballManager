package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.handlers.GetLinesHandler;
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

public class LinesTeamGameResource extends FirebaseDatabaseService {
    private static LinesTeamGameResource instance;
    private static DatabaseReference database;

    public static LinesTeamGameResource getInstance() {
        if (instance == null) {
            instance = new LinesTeamGameResource();
        }
        database = getDatabase().child(LINES_TEAM_GAME).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    /**
     * Add or edit line ADD EVERY LINES AT ONE TIME
     */
    public void saveLines(String gameId, final Map<Integer, Line> lines, final SaveLinesHandler handler) {
        final Map<Integer, Line> savedLines = new HashMap<>();
        final ArrayList<Line> linesHandled = new ArrayList<>();
        for (Map.Entry<Integer, Line> entry : lines.entrySet()) {
            final Line line = entry.getValue();
            // Add, edit or remove line
            if(StringUtils.isEmptyString(line.getLineId())) {
                addLine(gameId, line, new FirebaseDatabaseService.AddDataSuccessListener() {
                    @Override
                    public void onAddDataSuccess(String id) {
                        line.setLineId(id);
                        savedLines.put(line.getLineNumber(), line);

                        linesHandled.add(line);
                        if(linesHandled.size() == lines.size()) {
                            handler.onLinesSaved(savedLines);
                        }
                    }
                });
            } else {
                if(line.getPlayerIdMap() == null || line.getPlayerIdMap().isEmpty()) {
                    removeLine(gameId, line.getLineId(), new DeleteDataSuccessListener() {
                        @Override
                        public void onDeleteDataSuccess() {
                            linesHandled.add(line);
                            if(linesHandled.size() == lines.size()) {
                                handler.onLinesSaved(savedLines);
                            }
                        }
                    });
                } else {
                    editLine(gameId, line, new FirebaseDatabaseService.EditDataSuccessListener() {
                        @Override
                        public void onEditDataSuccess() {
                            savedLines.put(line.getLineNumber(), line);
                            linesHandled.add(line);
                            if(linesHandled.size() == lines.size()) {
                                handler.onLinesSaved(savedLines);
                            }
                        }
                    });
                }
            }
        }
    }

    private void addLine(String gameId, Line line, final AddDataSuccessListener handler) {
        line.setLineId(database.push().getKey());
        addData(database.child(gameId).child(line.getLineId()), line, handler);
    }

    private void editLine(String gameId, Line line, final EditDataSuccessListener handler) {
        editData(database.child(gameId).child(line.getLineId()), line, handler);
    }

    private void removeLine(String gameId, String lineId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(gameId).child(lineId), handler);
    }

    /**
     * Get lines indexed by line number
     */
    public void getLines(String gameId, final GetLinesHandler handler) {
        getData(database.child(gameId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                Map<Integer, Line> lines = new HashMap<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Line line = snapshot.getValue(Line.class);
                    if(line != null) {
                        lines.put(line.getLineNumber(), line);
                    }
                }
                handler.onLinesLoaded(lines);
            }
        });
    }
}
