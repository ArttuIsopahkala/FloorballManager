package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetGamesHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class GamesResource extends FirebaseDatabaseService {
    private static GamesResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static GamesResource getInstance() {
        if (instance == null) {
            instance = new GamesResource();
        }
        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_SEASONS_GAMES).child(teamId);
        return instance;
    }

    public void addGame(Game game, final AddDataSuccessListener handler) {
        game.setGameId(database.push().getKey());
        addData(database.child(seasonId).child(game.getGameId()), game, handler);
    }

    public void editGame(Game game, final EditDataSuccessListener handler) {
        editData(database.child(seasonId).child(game.getGameId()), game, handler);
    }

    public void removeGame(String gameId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId), handler);
    }

    public void removeAllGames(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get all games of all season indexed by gameId
     */
    public void getAllGames(final GetGamesHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, Game> games = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot snapshot : season.getChildren()) {
                    final Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        games.put(game.getGameId(), game);
                    }
                }
                handler.onGamesLoaded(games);
            }
        });
    }

    /**
     * Get games of season indexed by gameId
     */
    public void getGames(String seasonId, final GetGamesHandler handler) {
        getData(database.child(seasonId), dataSnapshot -> {
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
}
