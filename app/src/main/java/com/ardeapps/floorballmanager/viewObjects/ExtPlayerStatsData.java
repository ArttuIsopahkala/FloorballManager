package com.ardeapps.floorballmanager.viewObjects;

import android.util.Pair;

import java.util.ArrayList;

public class ExtPlayerStatsData extends PlayerStatsData {

    public Pair<Integer, Integer> bestStats;
    public int longestStats;
    public int bestPlusMinus;
    public Pair<ArrayList<String>, Integer> bestAssists;
    public Pair<ArrayList<String>, Integer> bestScorers;
    public Pair<ArrayList<String>, Integer> bestLineMates;

    public ExtPlayerStatsData() {
    }

    public ExtPlayerStatsData(PlayerStatsData data) {
        this.gamesCount = data.gamesCount;
        this.points = data.points;
        this.pointsPerGame = data.pointsPerGame;
        this.pluses = data.pluses;
        this.minuses = data.minuses;
        this.plusMinus = data.plusMinus;
        this.scores = data.scores;
        this.scoresPerGame = data.scoresPerGame;
        this.yvScores = data.yvScores;
        this.avScores = data.avScores;
        this.rlScores = data.rlScores;
        this.assists = data.assists;
        this.assistsPerGame = data.assistsPerGame;
        this.yvAssists = data.yvAssists;
        this.avAssists = data.avAssists;
    }
    
}
