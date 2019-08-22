package com.ardeapps.floorballmanager.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Player {

    private String playerId;
    private String teamId;
    private String name;
    private Long number;
    private String shoots;
    private String position;
    private List<String> strengths;
    private boolean active;
    private boolean pictureUploaded;
    @Exclude
    private transient Bitmap picture;

    public Player() {
    }

    /**
     * @param playerId not null
     * @return player name or 'Poistettu pelaaja'
     */
    @Exclude
    public static String getPlayerName(String playerId) {
        Player player = AppRes.getInstance().getPlayers().get(playerId);
        if (player != null) {
            return player.getName();
        } else {
            return AppRes.getContext().getString(R.string.removed_player);
        }
    }

    @Exclude
    public static String getPositionText(String value, boolean shorten) {
        if (value == null) {
            return "";
        }
        Position position = Position.fromDatabaseName(value);
        if (position == Position.LW) {
            return AppRes.getContext().getString(shorten ? R.string.position_lw_short : R.string.position_lw);
        } else if (position == Position.C) {
            return AppRes.getContext().getString(shorten ? R.string.position_c_short : R.string.position_c);
        } else if (position == Position.RW) {
            return AppRes.getContext().getString(shorten ? R.string.position_rw_short : R.string.position_rw);
        } else if (position == Position.LD) {
            return AppRes.getContext().getString(shorten ? R.string.position_ld_short : R.string.position_ld);
        } else if (position == Position.RD) {
            return AppRes.getContext().getString(shorten ? R.string.position_rd_short : R.string.position_rd);
        } else {
            return "";
        }
    }

    @Exclude
    public static Map<Skill, String> getStrengthTextsMap() {
        Context ctx = AppRes.getContext();
        Map<Skill, String> strengthsMap = new TreeMap<>();
        strengthsMap.put(Player.Skill.SPEED, ctx.getString(R.string.strengths_speed));
        strengthsMap.put(Player.Skill.PASSING, ctx.getString(R.string.strengths_passing));
        strengthsMap.put(Player.Skill.SHOOTING, ctx.getString(R.string.strengths_shooting));
        strengthsMap.put(Player.Skill.BALL_HANDLING, ctx.getString(R.string.strengths_ball_handling));
        strengthsMap.put(Player.Skill.GAME_SENSE, ctx.getString(R.string.strengths_game_sense));
        strengthsMap.put(Player.Skill.BALL_PROTECTION, ctx.getString(R.string.strengths_ball_protection));
        strengthsMap.put(Player.Skill.INTERCEPTION, ctx.getString(R.string.strengths_interception));
        strengthsMap.put(Player.Skill.BLOCKING, ctx.getString(R.string.strengths_blocking));
        strengthsMap.put(Player.Skill.PHYSICALITY, ctx.getString(R.string.strengths_physicality));
        return strengthsMap;
    }

    public Player clone() {
        Player clone = new Player();
        clone.playerId = this.playerId;
        clone.teamId = this.teamId;
        clone.name = this.name;
        clone.number = this.number;
        clone.shoots = this.shoots;
        clone.position = this.position;
        clone.strengths = this.strengths;
        clone.active = this.active;
        clone.pictureUploaded = this.pictureUploaded;
        return clone;
    }

    @Exclude
    public String getNameWithNumber(boolean numberFirst) {
        String name = "";
        if (numberFirst) {
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

    @Exclude
    public boolean hasSomeOfSkills(ArrayList<Skill> skills) {
        for (String strength : getStrengths()) {
            Player.Skill skill = Player.Skill.fromDatabaseName(strength);
            if (skills.contains(skill)) {
                return true;
            }
        }
        return false;
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

    public List<String> getStrengths() {
        if (strengths == null) {
            strengths = new ArrayList<>();
        }
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPictureUploaded() {
        return pictureUploaded;
    }

    public void setPictureUploaded(boolean pictureUploaded) {
        this.pictureUploaded = pictureUploaded;
    }

    @Exclude
    public Bitmap getPicture() {
        return picture;
    }

    @Exclude
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public enum Position {
        LW,
        C,
        RW,
        LD,
        RD;

        public static Position fromDatabaseName(String value) {
            return Enum.valueOf(Position.class, value);
        }

        public String toDatabaseName() {
            return this.name();
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

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }

    public enum Skill {
        SPEED,
        PASSING,
        SHOOTING,
        BALL_HANDLING,
        GAME_SENSE,
        BALL_PROTECTION,
        INTERCEPTION,
        BLOCKING,
        PHYSICALITY;

        public static Skill fromDatabaseName(String value) {
            return Enum.valueOf(Skill.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }

    public enum Shoots {
        LEFT,
        RIGHT;

        public static Shoots fromDatabaseName(String value) {
            return Enum.valueOf(Shoots.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
