package com.ardeapps.floorballcoach;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 4.5.2017.
 */
public class AppRes extends MultiDexApplication {

    public static String PRIVACY_POLICY_LINK;
    private static Context mContext;
    private Map<String, Team> teams = new HashMap<>();
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Game> games = new HashMap<>();
    private Map<String, Goal> goals = new HashMap<>();
    private Map<Integer, Line> lines = new HashMap<>();
    private Team selectedTeam;
    private String currentAppVersion;
    private User user;

    private static AppRes instance;

    public static AppRes getInstance() {
        if (instance == null) {
            instance = (AppRes)getContext();
        }
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    public void setCurrentAppVersion(String currentAppVersion) {
        this.currentAppVersion = currentAppVersion;
    }

    public String getCurrentAppVersion() {
        return currentAppVersion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team team) {
        selectedTeam = team;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    /**
     * TEAMS
     */
    public void setTeams(Map<String, Team> teams) {
        this.teams = teams;
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void setTeam(String teamId, Team team) {
        if(team != null) {
            teams.put(teamId, team);
        } else {
            teams.remove(teamId);
        }
    }

    /**
     * PLAYERS
     */
    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayer(String playerId, Player player) {
        if(player != null) {
            players.put(playerId, player);
        } else {
            players.remove(playerId);
        }
    }

    /**
     * GAMES
     */
    public void setGames(Map<String, Game> games) {
        this.games = games;
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public void setGame(String gameId, Game game) {
        if(game != null) {
            games.put(gameId, game);
        } else {
            games.remove(gameId);
        }
    }

    /**
     * GOALS
     */
    public void setGoals(Map<String, Goal> goals) {
        this.goals = goals;
    }

    public Map<String, Goal> getGoals() {
        return goals;
    }

    public void setGoal(String goalId, Goal goal) {
        if(goal != null) {
            goals.put(goalId, goal);
        } else {
            goals.remove(goalId);
        }
    }

    /**
     * LINES
     */
    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    /**
     * Get lines indexed by lineNumber
     */
    public Map<Integer, Line> getLines() {
        return lines;
    }

    public void setLine(Integer lineId, Line line) {
        if(line != null) {
            lines.put(lineId, line);
        } else {
            lines.remove(lineId);
        }
    }

}
