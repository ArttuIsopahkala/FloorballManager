package com.ardeapps.floorballcoach.objects;

public class UserConnection {

    public enum Role {
        PLAYER,
        ADMIN;

        public String toDatabaseName() {
            return this.name();
        }

        public static Role fromDatabaseName(String value) {
            return Enum.valueOf(Role.class, value);
        }
    }

    public enum Status {
        DENY,
        PENDING,
        CONNECTED;

        public String toDatabaseName() {
            return this.name();
        }

        public static Status fromDatabaseName(String value) {
            return Enum.valueOf(Status.class, value);
        }
    }

    private String userConnectionId;
    private String status;
    private String userId;
    private String playerId;
    private String email;
    private String role;

    public UserConnection() {
    }

    public UserConnection clone() {
        UserConnection clone = new UserConnection();
        clone.userConnectionId = this.userConnectionId;
        clone.status = this.status;
        clone.userId = this.userId;
        clone.playerId = this.playerId;
        clone.email = this.email;
        clone.role = this.role;
        return clone;
    }

    public String getUserConnectionId() {
        return userConnectionId;
    }

    public void setUserConnectionId(String userConnectionId) {
        this.userConnectionId = userConnectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
