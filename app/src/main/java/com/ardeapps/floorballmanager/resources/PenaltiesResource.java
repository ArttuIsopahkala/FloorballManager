package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.handlers.GetGamePenaltiesHandler;
import com.ardeapps.floorballmanager.handlers.GetPenaltiesHandler;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 29.10.2019.
 */

public class PenaltiesResource extends FirebaseDatabaseService {
    private static PenaltiesResource instance;
    private static DatabaseReference database;
    private static String seasonId;

    public static PenaltiesResource getInstance() {
        if (instance == null) {
            instance = new PenaltiesResource();
        }
        String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        Season season = AppRes.getInstance().getSelectedSeason();
        if (season != null) {
            seasonId = season.getSeasonId();
        }
        database = getDatabase().child(TEAMS_SEASONS_GAMES_PENALTIES).child(teamId);
        return instance;
    }

    public void addPenalty(Penalty penalty, final AddDataSuccessListener handler) {
        penalty.setPenaltyId(database.push().getKey());
        addData(database.child(seasonId).child(penalty.getGameId()).child(penalty.getPenaltyId()), penalty, handler);
    }

    public void editPenalty(Penalty penalty, final EditDataSuccessListener handler) {
        editData(database.child(seasonId).child(penalty.getGameId()).child(penalty.getPenaltyId()), penalty, handler);
    }

    public void removePenalty(String gameId, String penaltyId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId).child(penaltyId), handler);
    }

    public void removePenalties(String gameId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId).child(gameId), handler);
    }

    public void removeAllPenalties(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get penalties of all seasons indexed by gameId
     */
    public void getAllPenalties(final GetPenaltiesHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, ArrayList<Penalty>> penaltiesMap = new HashMap<>();
            for (DataSnapshot season : dataSnapshot.getChildren()) {
                for (DataSnapshot game : season.getChildren()) {
                    String gameId = game.getKey();
                    if (gameId != null) {
                        ArrayList<Penalty> penalties = new ArrayList<>();
                        for (DataSnapshot snapshot : game.getChildren()) {
                            final Penalty penalty = snapshot.getValue(Penalty.class);
                            if (penalty != null) {
                                penalties.add(penalty);
                            }
                        }
                        penaltiesMap.put(gameId, penalties);
                    }
                }
            }
            handler.onPenaltiesLoaded(penaltiesMap);
        });
    }

    /**
     * Get penalties of season indexed by gameId
     */
    public void getPenalties(String seasonId, final GetPenaltiesHandler handler) {
        getData(database.child(seasonId), dataSnapshot -> {
            final Map<String, ArrayList<Penalty>> penaltiesMap = new HashMap<>();
            for (DataSnapshot game : dataSnapshot.getChildren()) {
                String gameId = game.getKey();
                if (gameId != null) {
                    ArrayList<Penalty> penalties = new ArrayList<>();
                    for (DataSnapshot snapshot : game.getChildren()) {
                        final Penalty penalty = snapshot.getValue(Penalty.class);
                        if (penalty != null) {
                            penalties.add(penalty);
                        }
                    }
                    penaltiesMap.put(gameId, penalties);
                }
            }
            handler.onPenaltiesLoaded(penaltiesMap);
        });
    }

    /**
     * Get penalties of game indexed by goalId
     */
    public void getPenalties(String gameId, final GetGamePenaltiesHandler handler) {
        getData(database.child(seasonId).child(gameId), dataSnapshot -> {
            final Map<String, Penalty> penalties = new HashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final Penalty penalty = snapshot.getValue(Penalty.class);
                if (penalty != null) {
                    penalties.put(penalty.getPenaltyId(), penalty);
                }
            }
            handler.onPenaltiesLoaded(penalties);
        });
    }
}
