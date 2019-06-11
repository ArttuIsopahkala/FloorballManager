package com.ardeapps.floorballcoach.objects;

public class Game {

    private String gameId;
    private long date;
    private int periodInMinutes;
    private boolean homeGame;
    private String opponentName;
    private Integer homeGoals;
    private Integer awayGoals;

    public Game() {
    }

    public Game clone() {
        Game clone = new Game();
        clone.gameId = this.gameId;
        clone.date = this.date;
        clone.periodInMinutes = this.periodInMinutes;
        clone.homeGame = this.homeGame;
        clone.opponentName = this.opponentName;
        clone.homeGoals = this.homeGoals;
        clone.awayGoals = this.awayGoals;
        return clone;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public int getPeriodInMinutes() {
        return periodInMinutes;
    }

    public void setPeriodInMinutes(int periodInMinutes) {
        this.periodInMinutes = periodInMinutes;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public void setHomeGame(boolean homeGame) {
        this.homeGame = homeGame;
    }
}
