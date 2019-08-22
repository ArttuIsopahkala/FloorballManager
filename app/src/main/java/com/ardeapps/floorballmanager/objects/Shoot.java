package com.ardeapps.floorballmanager.objects;

import java.util.List;

public class Shoot {

    private String shootId;
    private String teamId;
    private String playerId;
    private String gameId;
    private String type;
    private double positionX;
    private double positionY;
    private String gameMode;
    private List<String> playerIds;

    public Shoot() {
    }

    public Shoot clone() {
        Shoot clone = new Shoot();

        return clone;
    }

    public enum Type {
        SAVE,
        MISS,
        BLOCK;

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }

}
