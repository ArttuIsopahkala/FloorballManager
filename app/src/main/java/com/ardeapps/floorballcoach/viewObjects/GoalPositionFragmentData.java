package com.ardeapps.floorballcoach.viewObjects;

public class GoalPositionFragmentData {

    private Double positionPercentX;
    private Double positionPercentY;
    private String opponentName;
    private boolean isOpponentGoal;

    public GoalPositionFragmentData() {
    }

    public Double getPositionPercentX() {
        return positionPercentX;
    }

    public void setPositionPercentX(Double positionPercentX) {
        this.positionPercentX = positionPercentX;
    }

    public Double getPositionPercentY() {
        return positionPercentY;
    }

    public void setPositionPercentY(Double positionPercentY) {
        this.positionPercentY = positionPercentY;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public boolean isOpponentGoal() {
        return isOpponentGoal;
    }

    public void setOpponentGoal(boolean opponentGoal) {
        isOpponentGoal = opponentGoal;
    }
}
