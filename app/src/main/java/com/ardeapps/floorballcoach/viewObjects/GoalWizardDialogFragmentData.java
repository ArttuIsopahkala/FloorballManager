package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GoalWizardDialogFragmentData {

    private Goal goal;
    private Game game;
    private Map<Integer, Line> lines;
    private boolean opponentGoal;

    public GoalWizardDialogFragmentData() {
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
}
