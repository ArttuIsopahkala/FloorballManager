package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Penalty;

import java.util.HashMap;
import java.util.Map;

public class GoalWizardDialogData {

    private Goal goal;
    private Game game;
    private Map<Integer, Line> lines;
    private boolean opponentGoal;
    private Map<String, Penalty> penalties;
    private Map<String, Goal> goals;

    public GoalWizardDialogData() {
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

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public boolean isOpponentGoal() {
        return opponentGoal;
    }

    public void setOpponentGoal(boolean opponentGoal) {
        this.opponentGoal = opponentGoal;
    }

    public Map<String, Penalty> getPenalties() {
        return penalties;
    }

    public void setPenalties(Map<String, Penalty> penalties) {
        this.penalties = penalties;
    }

    public Map<String, Goal> getGoals() {
        return goals;
    }

    public void setGoals(Map<String, Goal> goals) {
        this.goals = goals;
    }
}
