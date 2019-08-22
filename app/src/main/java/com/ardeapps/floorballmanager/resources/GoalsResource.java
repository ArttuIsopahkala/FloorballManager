package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetGameGoalsHandler;
import com.ardeapps.floorballmanager.handlers.GetGoalsHandler;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class GoalsResource extends FirebaseDatabaseService {
    private static GoalsResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static GoalsResource getInstance() {
        if (instance == null) {
            instance = new GoalsResource();
        }
        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_SEASONS_GAMES_GOALS).child(teamId);
        return instance;
    }

    public void addGoal(Goal goal, final AddDataSuccessListener handler) {
        goal.setGoalId(database.push().getKey());
        addData(database.child(seasonId).child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void editGoal(Goal goal, final EditDataSuccessListener handler) {
        editData(database.child(seasonId).child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void removeGoal(String gameId, String goalId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId).child(goalId), handler);
    }

    public void removeGoals(String gameId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId), handler);
    }

    public void removeAllGoals(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get goals of all seasons indexed by gameId
     */
    public void getAllGoals(final GetGoalsHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, ArrayList<Goal>> goalsMap = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot game : season.getChildren()) {
                    String gameId = game.getKey();
                    if (gameId != null) {
                        ArrayList<Goal> goals = new ArrayList<>();
                        for (DataSnapshot snapshot : game.getChildren()) {
                            final Goal goal = snapshot.getValue(Goal.class);
                            if (goal != null) {
                                goals.add(goal);
                            }
                        }
                        goalsMap.put(gameId, goals);
                    }
                }
            }
            handler.onGoalsLoaded(goalsMap);
        });
    }

    /**
     * Get goals of season indexed by gameId
     */
    public void getGoals(String seasonId, final GetGoalsHandler handler) {
        getData(database.child(seasonId), dataSnapshot -> {
            final Map<String, ArrayList<Goal>> goalsMap = new HashMap<>();
            for (DataSnapshot game : dataSnapshot.getChildren()) {
                String gameId = game.getKey();
                if (gameId != null) {
                    ArrayList<Goal> goals = new ArrayList<>();
                    for (DataSnapshot snapshot : game.getChildren()) {
                        final Goal goal = snapshot.getValue(Goal.class);
                        if (goal != null) {
                            goals.add(goal);
                        }
                    }
                    goalsMap.put(gameId, goals);
                }
            }
            handler.onGoalsLoaded(goalsMap);
        });
    }

    /**
     * Get goals of game indexed by goalId
     */
    public void getGoals(String gameId, final GetGameGoalsHandler handler) {
        getData(database.child(seasonId).child(gameId), dataSnapshot -> {
            final Map<String, Goal> goals = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final Goal goal = snapshot.getValue(Goal.class);
                if (goal != null) {
                    goals.put(goal.getGoalId(), goal);
                }
            }
            handler.onGoalsLoaded(goals);
        });
    }
}
