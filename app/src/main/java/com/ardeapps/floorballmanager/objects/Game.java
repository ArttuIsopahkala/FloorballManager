package com.ardeapps.floorballmanager.objects;

public class Game {

    private String gameId;
    private String seasonId;
    private long date;
    private boolean homeGame;
    private String opponentName;
    private Integer homeGoals;
    private Integer awayGoals;
    private long periodInMinutes;

    public Game() {
    }

    public Game clone() {
        Game clone = new Game();
        clone.gameId = this.gameId;
        clone.seasonId = this.seasonId;
        clone.date = this.date;
        clone.homeGame = this.homeGame;
        clone.opponentName = this.opponentName;
        clone.homeGoals = this.homeGoals;
        clone.awayGoals = this.awayGoals;
        clone.periodInMinutes = this.periodInMinutes;
        return clone;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public void setHomeGame(boolean homeGame) {
        this.homeGame = homeGame;
    }

    public long getPeriodInMinutes() {
        return periodInMinutes;
    }

    public void setPeriodInMinutes(long periodInMinutes) {
        this.periodInMinutes = periodInMinutes;
    }
}
