package com.ardeapps.floorballcoach.objects;


import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

public class Team {

    private String teamId;
    private String name;
    private boolean logoUploaded;
    @Exclude
    private Bitmap logo;

    public Team() {
    }

    public Team clone() {
        Team clone = new Team();
        clone.teamId = this.teamId;
        clone.name = this.name;
        return clone;
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

    public boolean isLogoUploaded() {
        return logoUploaded;
    }

    public void setLogoUploaded(boolean logoUploaded) {
        this.logoUploaded = logoUploaded;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }
}
