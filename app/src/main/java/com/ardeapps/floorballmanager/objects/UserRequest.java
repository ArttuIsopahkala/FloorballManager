package com.ardeapps.floorballmanager.objects;

import com.google.firebase.database.Exclude;

public class UserRequest {

    // userConnectionId works as id
    private String userConnectionId;
    private String status;
    private String userId;
    private String teamId;
    private String email;
    @Exclude
    private Team team;


    public UserRequest() {
    }

    public UserRequest clone() {
        UserRequest clone = new UserRequest();
        clone.userConnectionId = this.userConnectionId;
        clone.status = this.status;
        clone.userId = this.userId;
        clone.teamId = this.teamId;
        clone.email = this.email;
        return clone;
    }

    public String getUserConnectionId() {
        return userConnectionId;
    }

    public void setUserConnectionId(String userConnectionId) {
        this.userConnectionId = userConnectionId;
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

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public Team getTeam() {
        return team;
    }

    @Exclude
    public void setTeam(Team team) {
        this.team = team;
    }

    public enum Status {
        PENDING,
        ACCEPTED;

        public static Status fromDatabaseName(String value) {
            return Enum.valueOf(Status.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
