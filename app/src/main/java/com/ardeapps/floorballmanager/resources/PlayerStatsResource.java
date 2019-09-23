package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetStatsHandler;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PlayerStatsResource extends FirebaseDatabaseService {
    private static PlayerStatsResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static PlayerStatsResource getInstance() {
        if (instance == null) {
            instance = new PlayerStatsResource();
        }

        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_PLAYERS_SEASONS_GAMES_STATS).child(teamId);
        return instance;
    }

    public void editStat(String playerId, Goal goal, final EditDataSuccessListener handler) {
        editData(database.child(playerId).child(seasonId).child(goal.getGameId()).child(goal.getGoalId()), goal, handler);
    }

    public void removeStat(String playerId, String gameId, String goalId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId).child(seasonId).child(gameId).child(goalId), handler);
    }

    public void editStats(final List<String> playerIds, Goal goal, final EditDataSuccessListener handler) {
        if (!playerIds.isEmpty()) {
            final ArrayList<String> removedStats = new ArrayList<>();
            for (final String playerId : playerIds) {
                editData(database.child(playerId).child(seasonId).child(goal.getGameId()).child(goal.getGoalId()), goal, () -> {
                    removedStats.add(playerId);
                    if (playerIds.size() == removedStats.size()) {
                        handler.onEditDataSuccess();
                    }
                });
            }
        } else {
            handler.onEditDataSuccess();
        }
    }

    public void removeStats(final List<String> playerIds, String gameId, String goalId, final DeleteDataSuccessListener handler) {
        if (!playerIds.isEmpty()) {
            final ArrayList<String> removedStats = new ArrayList<>();
            for (final String playerId : playerIds) {
                deleteData(database.child(playerId).child(seasonId).child(gameId).child(goalId), () -> {
                    removedStats.add(playerId);
                    if (playerIds.size() == removedStats.size()) {
                        handler.onDeleteDataSuccess();
                    }
                });
            }
        } else {
            handler.onDeleteDataSuccess();
        }
    }

    public void removeStats(final Set<String> playerIds, String gameId, final DeleteDataSuccessListener handler) {
        if (!playerIds.isEmpty()) {
            final ArrayList<String> removedStats = new ArrayList<>();
            for (final String playerId : playerIds) {
                deleteData(database.child(playerId).child(seasonId).child(gameId), () -> {
                    removedStats.add(playerId);
                    if (playerIds.size() == removedStats.size()) {
                        handler.onDeleteDataSuccess();
                    }
                });
            }
        } else {
            handler.onDeleteDataSuccess();
        }
    }

    public void removeAllStats(String playerId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId), handler);
    }

    /**
     * Get stats by player in season indexed by gameId
     */
    public void getStats(String playerId, String seasonId, final GetStatsHandler handler) {
        getData(database.child(playerId).child(seasonId), dataSnapshot -> {
            final Map<String, ArrayList<Goal>> stats = new HashMap<>();
            for (DataSnapshot game : dataSnapshot.getChildren()) {
                String gameId = game.getKey();
                if (gameId != null) {
                    ArrayList<Goal> goals = new ArrayList<>();
                    for (DataSnapshot snapshot : game.getChildren()) {
                        final Goal goal = snapshot.getValue(Goal.class);
                        if (goal != null) {
                            goals.add(goal);
                        }
                    }
                    stats.put(gameId, goals);
                }
            }
            handler.onStatsLoaded(stats);
        });
    }

    /**
     * Get all stats by player indexed by gameId
     */
    public void getAllStats(String playerId, final GetStatsHandler handler) {
        getData(database.child(playerId), dataSnapshot -> {
            final Map<String, ArrayList<Goal>> stats = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot game : season.getChildren()) {
                    String gameId = game.getKey();
                    if (gameId != null) {
                        ArrayList<Goal> goals = new ArrayList<>();
                        for (DataSnapshot snapshot : game.getChildren()) {
                            final Goal goal = snapshot.getValue(Goal.class);
                            if (goal != null) {
                                goals.add(goal);
                            }
                        }
                        stats.put(gameId, goals);
                    }
                }
            }
            handler.onStatsLoaded(stats);
        });
    }
}
