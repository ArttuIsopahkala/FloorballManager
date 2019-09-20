package com.ardeapps.floorballmanager;

import android.content.Context;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 4.5.2017.
 */
public class AppRes extends MultiDexApplication {

    private static Context mContext;
    private static FragmentActivity mActivity;
    private static AppRes instance;
    private Map<String, Team> teams;
    // User info
    private User user;
    private Map<String, UserInvitation> userInvitations;
    private UserConnection.Role selectedRole;
    private String selectedPlayerId;
    // Selected team's info
    private Team selectedTeam;
    private Season selectedSeason;
    private Map<String, Player> players;
    private Map<String, Season> seasons;
    private Map<Integer, Line> lines;
    private Map<String, UserConnection> userConnections;
    private Map<String, Game> games; // Loaded in TeamDashboardFragment
    // For analyzer service
    private Map<String, ArrayList<Goal>> goalsByGame;
    private Map<String, ArrayList<Line>> linesByGame;

    public static AppRes getInstance() {
        if (instance == null) {
            instance = (AppRes) getContext();
        }
        return instance;
    }

    public static boolean isDeveloper() {
        if (BuildConfig.DEBUG) {
            String android_id = Settings.Secure.getString(AppRes.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            return android_id != null && android_id.equals("15c7a9f3626aad4a");
        }
        return false;
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
        selectedPlayerId = null;
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

    public String getSelectedPlayerId() {
        return selectedPlayerId;
    }

    public void setSelectedPlayerId(String selectedPlayerId) {
        this.selectedPlayerId = selectedPlayerId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public Map<String, Team> getTeams() {
        if (teams == null) {
            teams = new HashMap<>();
        }
        return teams;
    }

    /**
     * TEAMS
     */
    public void setTeams(Map<String, Team> teams) {
        this.teams = teams;
    }

    public void setTeam(String teamId, Team team) {
        if (teams == null) {
            teams = new HashMap<>();
        }
        if (team != null) {
            teams.put(teamId, team);
        } else {
            teams.remove(teamId);
        }
    }

    public Map<String, Season> getSeasons() {
        if (seasons == null) {
            seasons = new HashMap<>();
        }
        return seasons;
    }

    /**
     * SEASONS
     */
    public void setSeasons(Map<String, Season> seasons) {
        this.seasons = seasons;
    }

    public void setSeason(String seasonId, Season season) {
        if (seasons == null) {
            seasons = new HashMap<>();
        }
        if (season != null) {
            seasons.put(seasonId, season);
        } else {
            seasons.remove(seasonId);
        }
    }

    public Map<String, UserInvitation> getUserInvitations() {
        if (userInvitations == null) {
            userInvitations = new HashMap<>();
        }
        return userInvitations;
    }

    /**
     * USER INVITATIONS
     */
    public void setUserInvitations(Map<String, UserInvitation> userInvitations) {
        this.userInvitations = userInvitations;
    }

    public void setUserInvitation(String userConnectionId, UserInvitation userInvitation) {
        if (userInvitations == null) {
            userInvitations = new HashMap<>();
        }
        if (userInvitation != null) {
            userInvitations.put(userConnectionId, userInvitation);
        } else {
            userInvitations.remove(userConnectionId);
        }
    }

    public Map<String, Player> getPlayers() {
        if (players == null) {
            players = new HashMap<>();
        }
        return players;
    }

    /**
     * PLAYERS
     */
    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getInActivePlayers() {
        if (players == null) {
            players = new HashMap<>();
        }
        ArrayList<Player> inActivePlayers = new ArrayList<>();
        for (Player player : players.values()) {
            if (!player.isActive()) {
                inActivePlayers.add(player);
            }
        }
        Collections.sort(inActivePlayers, (player1, player2) -> player1.getName().compareTo(player2.getName()));
        return inActivePlayers;
    }

    public ArrayList<Player> getActivePlayers() {
        if (players == null) {
            players = new HashMap<>();
        }
        ArrayList<Player> activePlayers = new ArrayList<>();
        for (Player player : players.values()) {
            if (player.isActive()) {
                activePlayers.add(player);
            }
        }
        Collections.sort(activePlayers, (player1, player2) -> player1.getName().compareTo(player2.getName()));
        return activePlayers;
    }

    public Map<String, Player> getActivePlayersMap() {
        if (players == null) {
            players = new HashMap<>();
        }
        Map<String, Player> activePlayers = new HashMap<>();
        for (Player player : players.values()) {
            if (player.isActive()) {
                activePlayers.put(player.getPlayerId(), player);
            }
        }
        return activePlayers;
    }

    public void setPlayer(String playerId, Player player) {
        if (players == null) {
            players = new HashMap<>();
        }
        if (player != null) {
            players.put(playerId, player);
        } else {
            players.remove(playerId);
        }
    }

    public Map<String, Game> getGames() {
        if (games == null) {
            games = new HashMap<>();
        }
        return games;
    }

    /**
     * GAMES
     */
    public void setGames(Map<String, Game> games) {
        this.games = games;
    }

    public void setGame(String gameId, Game game) {
        if (games == null) {
            games = new HashMap<>();
        }
        if (game != null) {
            games.put(gameId, game);
        } else {
            games.remove(gameId);
        }
    }

    /**
     * Get lines indexed by lineNumber
     */
    public Map<Integer, Line> getLines() {
        if (lines == null) {
            lines = new HashMap<>();
        }
        return lines;
    }

    /**
     * LINES
     */
    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public void setLine(Integer lineId, Line line) {
        if (lines == null) {
            lines = new HashMap<>();
        }
        if (line != null) {
            lines.put(lineId, line);
        } else {
            lines.remove(lineId);
        }
    }

    /**
     * Get lines indexed by userConnectionId
     */
    public Map<String, UserConnection> getUserConnections() {
        if (userConnections == null) {
            userConnections = new HashMap<>();
        }
        return userConnections;
    }

    /**
     * USER CONNECTIONS
     */
    public void setUserConnections(Map<String, UserConnection> userConnections) {
        this.userConnections = userConnections;
    }

    public void setUserConnection(String userConnectionId, UserConnection userConnection) {
        if (userConnections == null) {
            userConnections = new HashMap<>();
        }
        if (userConnectionId != null) {
            userConnections.put(userConnectionId, userConnection);
        } else {
            userConnections.remove(userConnectionId);
        }
    }

    /**
     * Get goals indexed by gameId
     */
    public Map<String, ArrayList<Goal>> getGoalsByGame() {
        if (goalsByGame == null) {
            goalsByGame = new HashMap<>();
        }
        return goalsByGame;
    }

    /**
     * GOALS BY GAME
     */
    public void setGoalsByGame(Map<String, ArrayList<Goal>> goalsByGame) {
        this.goalsByGame = goalsByGame;
    }

    /**
     * Get lines indexed by gameId
     */
    public Map<String, ArrayList<Line>> getLinesByGame() {
        if (linesByGame == null) {
            linesByGame = new HashMap<>();
        }
        return linesByGame;
    }

    /**
     * LINES BY GAME
     */
    public void setLinesByGame(Map<String, ArrayList<Line>> linesByGame) {
        this.linesByGame = linesByGame;
    }

}
