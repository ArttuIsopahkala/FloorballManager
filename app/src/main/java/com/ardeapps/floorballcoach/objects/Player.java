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

    public enum Type {
        GRINDER_FORWARD,
        PLAY_MAKER_FORWARD,
        POWER_FORWARD,
        SNIPER_FORWARD,
        TWO_WAY_FORWARD,
        DEFENSIVE_DEFENDER,
        POWER_DEFENDER,
        OFFENSIVE_DEFENDER,
        TWO_WAY_DEFENDER;

        public String toDatabaseName() {
            return this.name();
        }

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
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
    private String type;
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

    /**
     * @param playerId not null
     * @return player name or 'Poistettu pelaaja'
     */
    public static String getPlayerName(String playerId) {
        Player player = AppRes.getInstance().getPlayers().get(playerId);
        if (player != null) {
            return player.getName();
        } else {
            return AppRes.getContext().getString(R.string.removed_player);
        }
    }

    public static String getPositionText(String value, boolean shorten) {
        if(value == null) {
            return "";
        }
        Position position = Position.fromDatabaseName(value);
        if(position == Position.LW) {
            return AppRes.getContext().getString(shorten ? R.string.position_lw_short : R.string.position_lw);
        } else if(position == Position.C) {
            return AppRes.getContext().getString(shorten ? R.string.position_c_short : R.string.position_c);
        } else if(position == Position.RW) {
            return AppRes.getContext().getString(shorten ? R.string.position_rw_short : R.string.position_rw);
        } else if(position == Position.LD) {
            return AppRes.getContext().getString(shorten ? R.string.position_ld_short : R.string.position_ld);
        } else if(position == Position.RD) {
            return AppRes.getContext().getString(shorten ? R.string.position_rd_short : R.string.position_rd);
        } else {
            return "";
        }
    }

    public static String getTypeText(String value) {
        if(value == null) {
            return "";
        }
        Type type = Type.fromDatabaseName(value);
        Context ctx = AppRes.getContext();
        if(type == Type.GRINDER_FORWARD) {
            return ctx.getString(R.string.grinder_forward);
        } else if(type == Type.PLAY_MAKER_FORWARD) {
            return ctx.getString(R.string.play_maker_forward);
        } else if(type == Type.POWER_FORWARD) {
            return ctx.getString(R.string.power_forward);
        } else if(type == Type.SNIPER_FORWARD) {
            return ctx.getString(R.string.sniper_forward);
        } else if(type == Type.TWO_WAY_FORWARD) {
            return ctx.getString(R.string.two_way_forward);
        } else if(type == Type.DEFENSIVE_DEFENDER) {
            return ctx.getString(R.string.defensive_defender);
        } else if(type == Type.POWER_DEFENDER) {
            return ctx.getString(R.string.power_defender);
        } else if(type == Type.OFFENSIVE_DEFENDER) {
            return ctx.getString(R.string.offensive_defender);
        } else if(type == Type.TWO_WAY_DEFENDER) {
            return ctx.getString(R.string.two_way_defender);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
