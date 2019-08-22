package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Goal;

public class GoalDetailsFragmentData {

    private long time;
    private Goal.Mode gameMode;

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
}
