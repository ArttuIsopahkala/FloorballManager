package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GoalSelectAssistantFragmentData {

    private Map<Integer, Line> lines;
    private String assistantPlayerId;
    private String disabledPlayerId;

    public GoalSelectAssistantFragmentData() {
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

    public String getAssistantPlayerId() {
        return assistantPlayerId;
    }

    public void setAssistantPlayerId(String assistantPlayerId) {
        this.assistantPlayerId = assistantPlayerId;
    }

    public String getDisabledPlayerId() {
        return disabledPlayerId;
    }

    public void setDisabledPlayerId(String disabledPlayerId) {
        this.disabledPlayerId = disabledPlayerId;
    }
}
