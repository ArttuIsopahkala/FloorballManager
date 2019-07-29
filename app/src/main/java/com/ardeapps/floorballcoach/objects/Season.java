package com.ardeapps.floorballcoach.objects;

public class Season {

    private String seasonId;
    private String name;
    private int periodInMinutes;

    public Season() {
    }

    public Season clone() {
        Season clone = new Season();
        clone.seasonId = this.seasonId;
        clone.name = this.name;
        clone.periodInMinutes = this.periodInMinutes;
        return clone;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriodInMinutes() {
        return periodInMinutes;
    }

    public void setPeriodInMinutes(int periodInMinutes) {
        this.periodInMinutes = periodInMinutes;
    }

}
