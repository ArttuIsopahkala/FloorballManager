package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.handlers.GetSeasonsHandler;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class SeasonsResource extends FirebaseDatabaseService {
    private static SeasonsResource instance;
    private static DatabaseReference database;

    public static SeasonsResource getInstance() {
        if (instance == null) {
            instance = new SeasonsResource();
        }
        database = getDatabase().child(TEAMS_SEASONS).child(AppRes.getInstance().getSelectedTeam().getTeamId());
        return instance;
    }

    public void addSeason(Season season, final AddDataSuccessListener handler) {
        season.setSeasonId(database.push().getKey());
        addData(database.child(season.getSeasonId()), season, handler);
    }

    public void editSeason(Season season, final EditDataSuccessListener handler) {
        editData(database.child(season.getSeasonId()), season, handler);
    }

    public void removeSeason(String seasonId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(seasonId), handler);
    }

    public void removeAllSeasons(final DeleteDataSuccessListener handler) {
        deleteData(database, handler);
    }

    /**
     * Get seasons indexed by seasonId
     */
    public void getSeasons(final GetSeasonsHandler handler) {
        getData(database, new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                final Map<String, Season> seasons = new HashMap<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Season season = snapshot.getValue(Season.class);
                    if(season != null) {
                        seasons.put(season.getSeasonId(), season);
                    }
                }
                handler.onSeasonsLoaded(seasons);
            }
        });
    }
}
