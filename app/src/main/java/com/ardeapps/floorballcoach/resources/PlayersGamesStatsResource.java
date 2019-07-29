package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.handlers.GetGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetStatsHandler;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PlayersGamesStatsResource extends FirebaseDatabaseService {
    private static PlayersGamesStatsResource instance;
    private static DatabaseReference database;

    public static PlayersGamesStatsResource getInstance() {
        if (instance == null) {
            instance = new PlayersGamesStatsResource();
        }
        database = getDatabase().child(PLAYERS_GAMES_STATS);
        return instance;
    }

    public void addStat(String playerId, Goal goal, final AddDataSuccessListener handler) {
        goal.setGoalId(database.push().getKey());
        addData(database.child(playerId).child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void editStat(String playerId, Goal goal, final EditDataSuccessListener handler) {
        editData(database.child(playerId).child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void removeStat(String playerId, String gameId, String goalId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId).child(gameId).child(goalId), handler);
    }

    public void removeAllStats(String playerId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId), handler);
    }

    /**
     * Get all stats by player indexed by gameId
     */
    public void getStats(String playerId, final GetStatsHandler handler) {
        getData(database.child(playerId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, ArrayList<Goal>> stats = new HashMap<>();
                for(DataSnapshot game : dataSnapshot.getChildren()) {
                    String gameId = game.getKey();
                    if(gameId != null) {
                        ArrayList<Goal> goals = new ArrayList<>();
                        for(DataSnapshot snapshot : game.getChildren()) {
                            final Goal goal = snapshot.getValue(Goal.class);
                            if(goal != null) {
                                goals.add(goal);
                            }
                        }
                        stats.put(gameId, goals);
                    }
                }
                handler.onStatsLoaded(stats);
            }
        });
    }

    /**
     * Get goals of game indexed by goalId
     */
    public void getStats(String playerId, String gameId, final GetGoalsHandler handler) {
        getData(database.child(playerId).child(gameId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, Goal> goals = new HashMap<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Goal goal = snapshot.getValue(Goal.class);
                    if(goal != null) {
                        goals.put(goal.getGoalId(), goal);
                    }
                }
                handler.onGoalsLoaded(goals);
            }
        });
    }
}
