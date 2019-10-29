package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class SelectPlayerFragmentData {

    private Map<Integer, Line> lines;
    private String playerId;
    private String disabledPlayerId;

    public SelectPlayerFragmentData() {
    }

    public Map<Integer, Line> getLines() {
        if (lines == null) {
            lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getDisabledPlayerId() {
        return disabledPlayerId;
    }

    public void setDisabledPlayerId(String disabledPlayerId) {
        this.disabledPlayerId = disabledPlayerId;
    }
}
