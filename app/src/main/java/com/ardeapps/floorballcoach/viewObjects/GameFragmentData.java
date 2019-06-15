package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GameFragmentData {

    private Game game;
    private Map<Integer, Line> lines;
    private Map<String, Goal> goals;

    public GameFragmentData() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Map<Integer, Line> getLines() {
        if(lines == null) {
            this.lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public Map<String, Goal> getGoals() {
        if(goals == null) {
            this.goals = new HashMap<>();
        }
        return goals;
    }

    public void setGoals(Map<String, Goal> goals) {
        this.goals = goals;
    }
}
