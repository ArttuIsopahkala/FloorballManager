package com.ardeapps.floorballmanager.objects;

import com.google.firebase.database.Exclude;

public class UserInvitation {

    // userConnectionId works as id
    private String userConnectionId;
    private String email;
    private String teamId;
    private String role;
    @Exclude
    private Team team;

    public UserInvitation() {
    }

    public UserInvitation clone() {
        UserInvitation clone = new UserInvitation();
        clone.userConnectionId = this.userConnectionId;
        clone.email = this.email;
        clone.teamId = this.teamId;
        clone.role = this.role;
        return clone;
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

    public String getUserConnectionId() {
        return userConnectionId;
    }

    public void setUserConnectionId(String userConnectionId) {
        this.userConnectionId = userConnectionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Exclude
    public Team getTeam() {
        return team;
    }

    @Exclude
    public void setTeam(Team team) {
        this.team = team;
    }
}
