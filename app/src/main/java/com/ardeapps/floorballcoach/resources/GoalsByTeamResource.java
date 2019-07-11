package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamGoalsHandler;
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

public class GoalsByTeamResource extends FirebaseDatabaseService {
    private static GoalsByTeamResource instance;
    private static DatabaseReference database;

    public static GoalsByTeamResource getInstance() {
        if (instance == null) {
            instance = new GoalsByTeamResource();
        }
        database = getDatabase().child(GOALS_TEAM_GAME).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    public void addGoal(Goal goal, final AddDataSuccessListener handler) {
        goal.setGoalId(database.push().getKey());
        addData(database.child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void editGoal(Goal goal, final EditDataSuccessListener handler) {
        editData(database.child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void removeGoal(String gameId, String goalId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(gameId).child(goalId), handler);
    }

    /**
     * Get all goals by team indexed by gameId
     */
    public void getGoals(final GetTeamGoalsHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, ArrayList<Goal>> goalsMap = new HashMap<>();
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
                        goalsMap.put(gameId, goals);
                    }
                }
                handler.onTeamGoalsLoaded(goalsMap);
            }
        });
    }

    /**
     * Get goals of game indexed by goalId
     */
    public void getGoals(String gameId, final GetGoalsHandler handler) {
        getData(database.child(gameId), new GetDataSuccessListener() {
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
