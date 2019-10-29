package com.ardeapps.floorballmanager.objects;

public enum Event {
    GOAL,
    PENALTY;

    public static Event fromDatabaseName(String value) {
        return Enum.valueOf(Event.class, value);
    }

    public String toDatabaseName() {
        return this.name();
    }
}