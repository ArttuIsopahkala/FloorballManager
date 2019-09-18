package com.ardeapps.floorballmanager.objects;


import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

public class Team {

    private String teamId;
    private String name;
    private String founder;
    private long creationTime;
    private boolean logoUploaded;
    @Exclude
    private Bitmap logo;

    public Team() {
    }

    public Team clone() {
        Team clone = new Team();
        clone.teamId = this.teamId;
        clone.name = this.name;
        clone.founder = this.founder;
        clone.creationTime = this.creationTime;
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

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isLogoUploaded() {
        return logoUploaded;
    }

    public void setLogoUploaded(boolean logoUploaded) {
        this.logoUploaded = logoUploaded;
    }

    @Exclude
    public Bitmap getLogo() {
        return logo;
    }

    @Exclude
    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }
}
