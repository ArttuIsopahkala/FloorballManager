package com.ardeapps.floorballmanager.objects;

public class Penalty {

    private String penaltyId;
    private String gameId;
    private String seasonId;
    private String playerId;
    private boolean opponentPenalty;
    private long time;
    private long length;
    private String reason;

    public Penalty() {
    }

    public Penalty clone() {
        Penalty clone = new Penalty();
        clone.penaltyId = this.penaltyId;
        clone.gameId = this.gameId;
        clone.seasonId = this.seasonId;
        clone.playerId = this.playerId;
        clone.opponentPenalty = this.opponentPenalty;
        clone.time = this.time;
        clone.length = this.length;
        return clone;
    }

    public String getPenaltyId() {
        return penaltyId;
    }

    public void setPenaltyId(String penaltyId) {
        this.penaltyId = penaltyId;
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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean isOpponentPenalty() {
        return opponentPenalty;
    }

    public void setOpponentPenalty(boolean opponentPenalty) {
        this.opponentPenalty = opponentPenalty;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public enum Reason {

        ESTAMINEN;

        public static Reason fromDatabaseName(String value) {
            return Enum.valueOf(Reason.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
