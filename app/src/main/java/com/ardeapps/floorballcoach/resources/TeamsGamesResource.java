package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetGamesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arttu on 19.1.2018.
 */

public class TeamsGamesResource extends FirebaseDatabaseService {
    private static TeamsGamesResource instance;
    private static DatabaseReference database;

    public static TeamsGamesResource getInstance() {
        if (instance == null) {
            instance = new TeamsGamesResource();
        }
        database = getDatabase().child(TEAMS_GAMES).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    public void addGame(Game game, final AddDataSuccessListener handler) {
        game.setGameId(database.push().getKey());
        addData(database.child(game.getGameId()), game, handler);
    }

    public void editGame(Game game, final EditDataSuccessListener handler) {
        editData(database.child(game.getGameId()), game, handler);
    }

    public void removeGame(String gameId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(gameId), handler);
    }

    public void removeGames(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    public void getGames(final Set<String> gameIds, final GetGamesHandler handler) {
        final Map<String, Game> games = new HashMap<>();
        if(!gameIds.isEmpty()) {
            for(String gameId : gameIds) {
                getData(database.child(gameId), new GetDataSuccessListener() {
                    @Override
                    public void onGetDataSuccess(DataSnapshot snapshot) {
                        final Game game = snapshot.getValue(Game.class);
                        if(game != null) {
                            games.put(game.getGameId(), game);
                        }
                        if(gameIds.size() == games.size()) {
                            handler.onGamesLoaded(games);
                        }
                    }
                });
            }
        } else {
            handler.onGamesLoaded(games);
        }
    }

    /**
     * Get games indexed by gameId
     */
    public void getGames(final GetGamesHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, Game> games = new HashMap<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Game game = snapshot.getValue(Game.class);
                    if(game != null) {
                        games.put(game.getGameId(), game);
                    }
                }
                handler.onGamesLoaded(games);
            }
        });
    }
}
