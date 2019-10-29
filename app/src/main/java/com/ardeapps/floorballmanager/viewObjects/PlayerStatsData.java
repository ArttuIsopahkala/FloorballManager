package com.ardeapps.floorballmanager.viewObjects;

import android.util.Pair;

import java.util.ArrayList;

public class PlayerStatsData {

    public int gamesCount;
    public int points;
    public double pointsPerGame;
    public int pluses;
    public int minuses;
    public int plusMinus;
    public int scores;
    public double scoresPerGame;
    public int yvScores;
    public int avScores;
    public int rlScores;
    public int assists;
    public double assistsPerGame;
    public int yvAssists;
    public int avAssists;
    public Pair<Integer, Integer> bestStats;
    public int longestStats;
    public int bestPlusMinus;
    public int penalties;
    public double penaltiesPerGame;
    public Pair<ArrayList<String>, Integer> bestAssists;
    public Pair<ArrayList<String>, Integer> bestScorers;
    public Pair<ArrayList<String>, Integer> bestLineMates;

    public PlayerStatsData() {
    }

}
