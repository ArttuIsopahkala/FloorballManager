package com.ardeapps.floorballcoach.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.google.firebase.database.Exclude;

public class Player {

    public enum Position {
        LW,
        C,
        RW,
        LD,
        RD;

        public String toDatabaseName() {
            return this.name();
        }

        public static Position fromDatabaseName(String value) {
            return Enum.valueOf(Position.class, value);
        }
    }

    public enum Shoots {
        LEFT,
        RIGHT;

        public String toDatabaseName() {
            return this.name();
        }

        public static Shoots fromDatabaseName(String value) {
            return Enum.valueOf(Shoots.class, value);
        }
    }

    private String playerId;
    private String teamId;
    private String name;
    private Long number;
    private String shoots;
    private String position;
    private boolean pictureUploaded;
    @Exclude
    private transient Bitmap picture;

    public Player() {
    }

    public Player clone() {
        Player clone = new Player();
        clone.playerId = this.playerId;
        clone.teamId = this.teamId;
        clone.name = this.name;
        clone.number = this.number;
        clone.shoots = this.shoots;
        clone.position = this.position;
        clone.pictureUploaded = this.pictureUploaded;
        return clone;
    }

    public static String getPositionText(String value) {
        Position position = Position.fromDatabaseName(value);
        Context ctx = AppRes.getContext();
        if(position == Position.LW) {
            return ctx.getString(R.string.position_lw);
        } else if(position == Position.C) {
            return ctx.getString(R.string.position_c);
        } else if(position == Position.RW) {
            return ctx.getString(R.string.position_rw);
        } else if(position == Position.LD) {
            return ctx.getString(R.string.position_ld);
        } else if(position == Position.RD) {
            return ctx.getString(R.string.position_rd);
        } else {
            return "";
        }
    }

    public String getNameWithNumber(boolean numberFirst) {
        String name = "";
        if(numberFirst) {
            if (getNumber() != null) {
                name = "#" + getNumber() + " ";
            }
            name += getName();
        } else {
            name = getName();
            if (getNumber() != null) {
                name += " | #" + getNumber();
            }
        }
        return name;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getShoots() {
        return shoots;
    }

    public void setShoots(String shoots) {
        this.shoots = shoots;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isPictureUploaded() {
        return pictureUploaded;
    }

    public void setPictureUploaded(boolean pictureUploaded) {
        this.pictureUploaded = pictureUploaded;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
