package com.ardeapps.floorballmanager.objects;


import java.util.HashMap;
import java.util.Map;

public class User {

    private String userId;
    private String email;
    private long creationTime;
    private long lastLoginTime;
    private boolean premium;
    private Map<String, Boolean> teamIds; // Must be map due firebase security rules

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
            clone.teamIds = new HashMap<>();
        } else {
            clone.teamIds = this.teamIds;
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

    public Map<String, Boolean> getTeamIds() {
        if (teamIds == null) {
            teamIds = new HashMap<>();
        }
        return teamIds;
    }

    public void setTeamIds(Map<String, Boolean> teamIds) {
        this.teamIds = teamIds;
    }

    /*@Exclude
    public List<String> getTeamIdsList() {
        if (teamIds == null) {
            teamIds = new HashMap<>();
        }
        return new ArrayList<>(teamIds.keySet());
    }

    @Exclude
    public void setTeamIdsList(List<String> teamIds) {
        for(String teamId : teamIds) {
            this.teamIds.put(teamId, true);
        }
    }*/


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
