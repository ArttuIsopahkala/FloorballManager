package com.ardeapps.floorballcoach;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.objects.UserInvitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 4.5.2017.
 */
public class AppRes extends MultiDexApplication {

    private static Context mContext;
    private static FragmentActivity mActivity;
    private User user;
    private Map<String, Team> teams;
    private Map<String, UserInvitation> userInvitations;
    // Selected team's info
    private Team selectedTeam;
    private Season selectedSeason;
    private UserConnection.Role selectedRole;
    private Map<String, Player> players;
    private Map<String, Season> seasons;
    private Map<Integer, Line> lines;
    private Map<String, UserConnection> userConnections;
    private Map<String, Game> games; // Loaded in TeamDashboardFragment
    // For analyzer service
    private Map<String, ArrayList<Goal>> goalsByGame;
    private Map<String, ArrayList<Line>> linesByGame;

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

    public static FragmentActivity getActivity() {
        return mActivity;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    public void resetData() {
        user = null;
        selectedTeam = null;
        selectedSeason = null;
        selectedRole = null;
        teams = null;
        userInvitations = null;
        players = null;
        games = null;
        lines = null;
        userConnections = null;
        goalsByGame = null;
        linesByGame = null;
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

    public Season getSelectedSeason() {
        return selectedSeason;
    }

    public void setSelectedSeason(Season selectedSeason) {
        this.selectedSeason = selectedSeason;
    }

    public UserConnection.Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(UserConnection.Role selectedRole) {
        this.selectedRole = selectedRole;
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
        if(teams == null) {
            teams = new HashMap<>();
        }
        return teams;
    }

    public void setTeam(String teamId, Team team) {
        if(teams == null) {
            teams = new HashMap<>();
        }
        if(team != null) {
            teams.put(teamId, team);
        } else {
            teams.remove(teamId);
        }
    }

    /**
     * SEASONS
     */
    public void setSeasons(Map<String, Season> seasons) {
        this.seasons = seasons;
    }

    public Map<String, Season> getSeasons() {
        if(seasons == null) {
            seasons = new HashMap<>();
        }
        return seasons;
    }

    public void setSeason(String seasonId, Season season) {
        if(seasons == null) {
            seasons = new HashMap<>();
        }
        if(season != null) {
            seasons.put(seasonId, season);
        } else {
            seasons.remove(seasonId);
        }
    }

    /**
     * USER INVITATIONS
     */
    public void setUserInvitations(Map<String, UserInvitation> userInvitations) {
        this.userInvitations = userInvitations;
    }

    public Map<String, UserInvitation> getUserInvitations() {
        if(userInvitations == null) {
            userInvitations = new HashMap<>();
        }
        return userInvitations;
    }

    public void setUserInvitation(String userConnectionId, UserInvitation userInvitation) {
        if(userInvitations == null) {
            userInvitations = new HashMap<>();
        }
        if(userInvitation != null) {
            userInvitations.put(userConnectionId, userInvitation);
        } else {
            userInvitations.remove(userConnectionId);
        }
    }

    /**
     * PLAYERS
     */
    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Map<String, Player> getPlayers() {
        if(players == null) {
            players = new HashMap<>();
        }
        return players;
    }

    public void setPlayer(String playerId, Player player) {
        if(players == null) {
            players = new HashMap<>();
        }
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
        if(games == null) {
            games = new HashMap<>();
        }
        return games;
    }

    public void setGame(String gameId, Game game) {
        if(games == null) {
            games = new HashMap<>();
        }
        if(game != null) {
            games.put(gameId, game);
        } else {
            games.remove(gameId);
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
        if(lines == null) {
            lines = new HashMap<>();
        }
        return lines;
    }

    public void setLine(Integer lineId, Line line) {
        if(lines == null) {
            lines = new HashMap<>();
        }
        if(line != null) {
            lines.put(lineId, line);
        } else {
            lines.remove(lineId);
        }
    }

    /**
     * USER CONNECTIONS
     */
    public void setUserConnections(Map<String, UserConnection> userConnections) {
        this.userConnections = userConnections;
    }

    /**
     * Get lines indexed by lineNumber
     */
    public Map<String, UserConnection> getUserConnections() {
        if(userConnections == null) {
            userConnections = new HashMap<>();
        }
        return userConnections;
    }

    public void setUserConnection(String userConnectionId, UserConnection userConnection) {
        if(userConnections == null) {
            userConnections = new HashMap<>();
        }
        if(userConnectionId != null) {
            userConnections.put(userConnectionId, userConnection);
        } else {
            userConnections.remove(userConnectionId);
        }
    }

    /**
     * GOALS BY GAME
     */
    public void setGoalsByGame(Map<String, ArrayList<Goal>> goalsByGame) {
        this.goalsByGame = goalsByGame;
    }

    /**
     * Get goals indexed by gameId
     */
    public Map<String, ArrayList<Goal>> getGoalsByGame() {
        if(goalsByGame == null) {
            goalsByGame = new HashMap<>();
        }
        return goalsByGame;
    }

    /**
     * LINES BY GAME
     */
    public void setLinesByGame(Map<String, ArrayList<Line>> linesByGame) {
        this.linesByGame = linesByGame;
    }

    /**
     * Get lines indexed by gameId
     */
    public Map<String, ArrayList<Line>> getLinesByGame() {
        if(linesByGame == null) {
            linesByGame = new HashMap<>();
        }
        return linesByGame;
    }

}
