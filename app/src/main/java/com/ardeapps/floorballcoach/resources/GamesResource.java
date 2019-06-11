package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetGamesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
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

    public static GamesResource getInstance() {
        if (instance == null) {
            instance = new GamesResource();
        }
        database = getDatabase().child(GAMES).child(AppRes.getInstance().getSelectedTeam().getTeamId());
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

    // TODO not used?
    /*public void getGame(String gameId, final GetTeamHandler handler) {
        getData(database.child(gameId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot snapshot) {
                Game team = snapshot.getValue(Game.class);
                handler.onTeamLoaded(team);
            }
        });
    }*/

    /**
     * Get teams and their logos indexed by teamId
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
