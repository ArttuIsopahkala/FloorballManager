package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Goal;

import java.util.Map;

public class GoalDetailsFragmentData {

    private long time;
    private Goal.Mode gameMode;
    private Map<String, Goal> goals;
    private String currentGoalId;

    public GoalDetailsFragmentData() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Goal.Mode getGameMode() {
        return gameMode;
    }

    public void setGameMode(Goal.Mode gameMode) {
        this.gameMode = gameMode;
    }

    public Map<String, Goal> getGoals() {
        return goals;
    }

    public void setGoals(Map<String, Goal> goals) {
        this.goals = goals;
    }

    public String getCurrentGoalId() {
        return currentGoalId;
    }

    public void setCurrentGoalId(String currentGoalId) {
        this.currentGoalId = currentGoalId;
    }
}
