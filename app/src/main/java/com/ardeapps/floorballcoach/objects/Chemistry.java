package com.ardeapps.floorballcoach.objects;

public class Chemistry {

    private String playerId;
    private String comparePosition;
    private String comparePlayerId;
    private int chemistryPoints;

    public Chemistry() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getComparePosition() {
        return comparePosition;
    }

    public void setComparePosition(String comparePosition) {
        this.comparePosition = comparePosition;
    }

    public String getComparePlayerId() {
        return comparePlayerId;
    }

    public void setComparePlayerId(String comparePlayerId) {
        this.comparePlayerId = comparePlayerId;
    }

    public int getChemistryPoints() {
        return chemistryPoints;
    }

    public void setChemistryPoints(int chemistryPoints) {
        this.chemistryPoints = chemistryPoints;
    }
}
