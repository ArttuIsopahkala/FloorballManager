package com.ardeapps.floorballcoach.objects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Line {

    private String lineId;
    private int lineNumber;
    private Map<String, String> playerIdMap;

    public Line() {
    }

    public Line clone() {
        Line clone = new Line();
        clone.lineId = this.lineId;
        clone.lineNumber = this.lineNumber;
        clone.playerIdMap = this.playerIdMap;
        return clone;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Map<String, String> getPlayerIdMap() {
        if(playerIdMap == null) {
            playerIdMap = new HashMap<>();
        }
        return playerIdMap;
    }

    public void setPlayerIdMap(Map<String, String> playerIdMap) {
        this.playerIdMap = playerIdMap;
    }

    public Map<String, String> getSortedPlayers() {
        TreeMap<String, String> sorted = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Player.Position position1 = Player.Position.valueOf(o1);
                Player.Position position2 = Player.Position.valueOf(o2);
                if(position1 == Player.Position.LW) {
                    return -1;
                } else if (position1 == Player.Position.C && position2 != Player.Position.LW) {
                    return -1;
                } else if (position1 == Player.Position.RW && (position2 != Player.Position.C && position2 != Player.Position.LW)) {
                    return -1;
                } else if (position1 == Player.Position.LD && (position2 != Player.Position.C && position2 != Player.Position.LW && position2 == Player.Position.RW)) {
                    return -1;
                } else {
                    return -1;
                }
            }
        });
        sorted.putAll(getPlayerIdMap());
        return sorted;
    }

}
