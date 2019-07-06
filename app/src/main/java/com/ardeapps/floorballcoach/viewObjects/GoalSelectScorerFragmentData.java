package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GoalSelectScorerFragmentData {

    private Map<Integer, Line> lines;
    private String scorerPlayerId;
    private String disabledPlayerId;

    public GoalSelectScorerFragmentData() {
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

    public String getScorerPlayerId() {
        return scorerPlayerId;
    }

    public void setScorerPlayerId(String scorerPlayerId) {
        this.scorerPlayerId = scorerPlayerId;
    }

    public String getDisabledPlayerId() {
        return disabledPlayerId;
    }

    public void setDisabledPlayerId(String disabledPlayerId) {
        this.disabledPlayerId = disabledPlayerId;
    }
}
