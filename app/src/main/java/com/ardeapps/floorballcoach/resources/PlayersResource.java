package com.ardeapps.floorballcoach.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetPlayerHandler;
import com.ardeapps.floorballcoach.handlers.GetPlayersHandler;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FirebaseStorageService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class PlayersResource extends FirebaseDatabaseService {
    private static PlayersResource instance;
    private static DatabaseReference database;

    public static PlayersResource getInstance() {
        if (instance == null) {
            instance = new PlayersResource();
        }
        database = getDatabase().child(PLAYERS).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    public void addPlayer(Player player, final AddDataSuccessListener handler) {
        player.setPlayerId(database.push().getKey());
        addData(database.child(player.getPlayerId()), player, handler);
    }

    public void editPlayer(Player player, final EditDataSuccessListener handler) {
        editData(database.child(player.getPlayerId()), player, handler);
    }

    public void removePlayer(String playerId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(playerId), handler);
    }

    public void getPlayer(String playerId, final GetPlayerHandler handler) {
        getData(database.child(playerId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot snapshot) {
                Player player = snapshot.getValue(Player.class);
                handler.onPlayerLoaded(player);
            }
        });
    }

    /**
     * Get players and their pictures indexed by playerId
     */
    public void getPlayers(final GetPlayersHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, Player> players = new HashMap<>();
                List<String> playersWithImages = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Player player = snapshot.getValue(Player.class);
                    if(player != null) {
                        players.put(player.getPlayerId(), player);
                        if(player.isPictureUploaded()) {
                            playersWithImages.add(player.getPlayerId());
                        }
                    }
                }

                if(!playersWithImages.isEmpty()) {
                    PictureResource.getInstance().getPictures(playersWithImages, new FirebaseStorageService.GetBitmapsSuccessListener() {
                        @Override
                        public void onGetBitmapsSuccess(Map<String, Bitmap> bitmaps) {
                            for(Map.Entry<String, Bitmap> entry : bitmaps.entrySet()) {
                                String key = entry.getKey();
                                Bitmap value = entry.getValue();
                                players.get(key).setPicture(value);
                            }
                            handler.onPlayersLoaded(players);
                        }
                    });
                } else {
                    handler.onPlayersLoaded(players);
                }

            }
        });
    }
}
