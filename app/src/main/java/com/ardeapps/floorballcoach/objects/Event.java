package com.ardeapps.floorballcoach.objects;

public class Event {

    public enum Type {
        HOME_GOAL,
        HOME_SAVE,
        HOME_MISS,
        HOME_BLOCK;

        public String toDatabaseName() {
            return this.name();
        }

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
        }
    }

    private String eventId;
    private String teamId;
    private long time;
    private String type;

    public Event() {
    }

    public Event clone() {
        Event clone = new Event();

        return clone;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
