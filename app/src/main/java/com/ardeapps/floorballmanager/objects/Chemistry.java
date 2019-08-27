package com.ardeapps.floorballmanager.objects;

public class Chemistry {

    private String playerId;
    private Player.Position comparePosition;
    private String comparePlayerId;
    private double chemistryPercent;

    public Chemistry() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Player.Position getComparePosition() {
        return comparePosition;
    }

    public void setComparePosition(Player.Position comparePosition) {
        this.comparePosition = comparePosition;
    }

    public String getComparePlayerId() {
        return comparePlayerId;
    }

    public void setComparePlayerId(String comparePlayerId) {
        this.comparePlayerId = comparePlayerId;
    }

    public double getChemistryPercent() {
        return chemistryPercent;
    }

    public void setChemistryPercent(double chemistryPercent) {
        this.chemistryPercent = chemistryPercent;
    }
}
