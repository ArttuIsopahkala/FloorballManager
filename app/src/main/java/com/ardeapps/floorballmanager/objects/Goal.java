package com.ardeapps.floorballmanager.objects;

import java.util.ArrayList;
import java.util.List;

public class Goal {

    private String goalId;
    private String gameId;
    private String seasonId;
    private long time;
    private boolean opponentGoal;
    private String scorerId;
    private String assistantId;
    private Double positionPercentX;
    private Double positionPercentY;
    private String gameMode;
    private List<String> playerIds;

    public Goal() {
    }

    public Goal clone() {
        Goal clone = new Goal();
        clone.goalId = this.goalId;
        clone.gameId = this.gameId;
        clone.seasonId = this.seasonId;
        clone.time = this.time;
        clone.opponentGoal = this.opponentGoal;
        clone.scorerId = this.scorerId;
        clone.assistantId = this.assistantId;
        clone.positionPercentX = this.positionPercentX;
        clone.positionPercentY = this.positionPercentY;
        clone.gameMode = this.gameMode;
        if (this.playerIds == null) {
            this.playerIds = new ArrayList<>();
        }
        clone.playerIds = this.playerIds;
        return clone;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isOpponentGoal() {
        return opponentGoal;
    }

    public void setOpponentGoal(boolean opponentGoal) {
        this.opponentGoal = opponentGoal;
    }

    public String getScorerId() {
        return scorerId;
    }

    public void setScorerId(String scorerId) {
        this.scorerId = scorerId;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public List<String> getPlayerIds() {
        if (playerIds == null) {
            playerIds = new ArrayList<>();
        }
        return playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public enum Mode {
        AV,
        YV,
        FULL,
        RL;

        public static Mode fromDatabaseName(String value) {
            return Enum.valueOf(Mode.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
