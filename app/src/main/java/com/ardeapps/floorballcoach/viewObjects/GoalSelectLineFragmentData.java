package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GoalSelectLineFragmentData {

    private Map<Integer, Line> lines;
    private int maxSelectPlayers;

    public GoalSelectLineFragmentData() {
    }

    public Map<Integer, Line> getLines() {
        if(lines == null) {
            lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public int getMaxSelectPlayers() {
        return maxSelectPlayers;
    }

    public void setMaxSelectPlayers(int maxSelectPlayers) {
        this.maxSelectPlayers = maxSelectPlayers;
    }
}
