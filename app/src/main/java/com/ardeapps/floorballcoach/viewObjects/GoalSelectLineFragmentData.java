package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalSelectLineFragmentData {

    private Map<Integer, Line> lines;
    private int maxSelectPlayers;
    private List<String> selectedPlayerIds;
    private List<String> disabledPlayerIds;

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

    public List<String> getSelectedPlayerIds() {
        if(selectedPlayerIds == null) {
            selectedPlayerIds = new ArrayList<>();
        }
        return selectedPlayerIds;
    }

    public void setSelectedPlayerIds(List<String> selectedPlayerIds) {
        this.selectedPlayerIds = selectedPlayerIds;
    }

    public List<String> getDisabledPlayerIds() {
        if(disabledPlayerIds == null) {
            disabledPlayerIds = new ArrayList<>();
        }
        return disabledPlayerIds;
    }

    public void setDisabledPlayerIds(List<String> disabledPlayerIds) {
        this.disabledPlayerIds = disabledPlayerIds;
    }
}
