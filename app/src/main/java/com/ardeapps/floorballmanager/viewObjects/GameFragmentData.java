package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Penalty;

import java.util.HashMap;
import java.util.Map;

public class GameFragmentData {

    private Game game;
    private Map<Integer, Line> lines;
    private Map<String, Goal> goals;
    private Map<String, Penalty> penalties;

    public GameFragmentData() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Map<Integer, Line> getLines() {
        if (lines == null) {
            this.lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public Map<String, Goal> getGoals() {
        if (goals == null) {
            this.goals = new HashMap<>();
        }
        return goals;
    }

    public void setGoals(Map<String, Goal> goals) {
        this.goals = goals;
    }

    public Map<String, Penalty> getPenalties() {
        return penalties;
    }

    public void setPenalties(Map<String, Penalty> penalties) {
        this.penalties = penalties;
    }
}
