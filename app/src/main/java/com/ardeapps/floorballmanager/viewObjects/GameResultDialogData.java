package com.ardeapps.floorballmanager.viewObjects;

public class GameResultDialogData {

    private String homeName;
    private String awayName;
    private int homeGoals;
    private int awayGoals;
    private int markedHomeGoals;
    private int markedAwayGoals;

    public GameResultDialogData() {
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getAwayName() {
        return awayName;
    }

    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public int getMarkedHomeGoals() {
        return markedHomeGoals;
    }

    public void setMarkedHomeGoals(int markedHomeGoals) {
        this.markedHomeGoals = markedHomeGoals;
    }

    public int getMarkedAwayGoals() {
        return markedAwayGoals;
    }

    public void setMarkedAwayGoals(int markedAwayGoals) {
        this.markedAwayGoals = markedAwayGoals;
    }
}
