package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetGamesHandler;
import com.ardeapps.floorballmanager.handlers.GetSeasonGamesHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PlayerGamesResource extends FirebaseDatabaseService {
    private static PlayerGamesResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static PlayerGamesResource getInstance() {
        if (instance == null) {
            instance = new PlayerGamesResource();
        }
        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_PLAYERS_SEASONS_GAMES).child(teamId);
        return instance;
    }

    public void editGame(final Set<String> playerIds, Game game, final EditDataSuccessListener handler) {
        if (!playerIds.isEmpty()) {
            final ArrayList<String> playerIdsEdited = new ArrayList<>();
            for (final String playerId : playerIds) {
                editData(database.child(playerId).child(seasonId).child(game.getGameId()), game, () -> {
                    playerIdsEdited.add(playerId);
                    if (playerIds.size() == playerIdsEdited.size()) {
                        handler.onEditDataSuccess();
                    }
                });
            }
        } else {
            handler.onEditDataSuccess();
        }
    }

    public void removeGame(final Set<String> playerIds, String gameId, final DeleteDataSuccessListener handler) {
        if (!playerIds.isEmpty()) {
            final ArrayList<String> playerIdsRemoved = new ArrayList<>();
            for (final String playerId : playerIds) {
                deleteData(database.child(playerId).child(seasonId).child(gameId), () -> {
                    playerIdsRemoved.add(playerId);
                    if (playerIds.size() == playerIdsRemoved.size()) {
                        handler.onDeleteDataSuccess();
                    }
                });
            }
        } else {
            handler.onDeleteDataSuccess();
        }
    }

    public void removeAllGames(String playerId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId), handler);
    }

    /**
     * Get games where player has been played indexed by gameId
     */
    public void getGames(String playerId, String seasonId, final GetGamesHandler handler) {
        getData(database.child(playerId).child(seasonId), dataSnapshot -> {
            final Map<String, Game> games = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final Game game = snapshot.getValue(Game.class);
                if (game != null) {
                    games.put(game.getGameId(), game);
                }
            }
            handler.onGamesLoaded(games);
        });
    }

    /**
     * Get games of season by playerIds
     */
    public void getSeasonGames(Set<String> playerIds, String seasonId, final GetSeasonGamesHandler handler) {
        final Map<String, ArrayList<Game>> result = new HashMap<>();
        if (!playerIds.isEmpty()) {
            final ArrayList<String> playerIdsSucceed = new ArrayList<>();
            for (final String playerId : playerIds) {
                getData(database.child(playerId).child(seasonId), dataSnapshot -> {
                    final ArrayList<Game> games = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Game game = snapshot.getValue(Game.class);
                        if (game != null) {
                            games.add(game);
                        }
                    }
                    result.put(playerId, games);
                    playerIdsSucceed.add(playerId);
                    if (playerIds.size() == playerIdsSucceed.size()) {
                        handler.onSeasonGamesLoaded(result);
                    }
                });
            }
        } else {
            handler.onSeasonGamesLoaded(result);
        }
    }

    /**
     * Get all games of seasons where player has been played indexed by gameId
     */
    public void getAllGames(String playerId, final GetGamesHandler handler) {
        getData(database.child(playerId), dataSnapshot -> {
            final Map<String, Game> games = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot snapshot : season.getChildren()) {
                    final Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        games.put(game.getGameId(), game);
                    }
                }
            }
            handler.onGamesLoaded(games);
        });
    }

}
