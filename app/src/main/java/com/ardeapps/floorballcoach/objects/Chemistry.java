package com.ardeapps.floorballcoach.objects;

public class Chemistry {

    public enum ChemistryConnection {
        C_LW,
        C_RW,
        C_LD,
        C_RD,
        LD_RD,
        LD_LW,
        RD_RW
    }

    private String playerId;
    private Player.Position comparePosition;
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

    public int getChemistryPoints() {
        return chemistryPoints;
    }

    public void setChemistryPoints(int chemistryPoints) {
        this.chemistryPoints = chemistryPoints;
    }
}
