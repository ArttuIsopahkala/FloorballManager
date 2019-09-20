package com.ardeapps.floorballmanager.objects;


import java.util.ArrayList;
import java.util.List;

public class User {

    private String userId;
    private String email;
    private long creationTime;
    private long lastLoginTime;
    private boolean premium;
    private List<String> teamIds;
    private List<String> playerIds;

    public User() {
    }

    public User clone() {
        User clone = new User();
        clone.userId = this.userId;
        clone.email = this.email;
        clone.lastLoginTime = this.lastLoginTime;
        clone.creationTime = this.creationTime;
        clone.premium = this.premium;
        if (this.teamIds == null) {
            clone.teamIds = new ArrayList<>();
        } else {
            clone.teamIds = this.teamIds;
        }
        if (this.playerIds == null) {
            clone.playerIds = new ArrayList<>();
        } else {
            clone.playerIds = this.playerIds;
        }
        return clone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public List<String> getTeamIds() {
        if (teamIds == null) {
            teamIds = new ArrayList<>();
        }
        return teamIds;
    }

    public void setTeamIds(List<String> teamIds) {
        this.teamIds = teamIds;
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

    public enum Flag {
        PREMIUM,
        ADMIN;

        public static Flag fromDatabaseName(String value) {
            return Enum.valueOf(Flag.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
