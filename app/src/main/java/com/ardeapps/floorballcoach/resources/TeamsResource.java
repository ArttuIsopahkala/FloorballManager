package com.ardeapps.floorballcoach.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballcoach.handlers.GetTeamHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamsHandler;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FirebaseStorageService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 19.1.2018.
 */

public class TeamsResource extends FirebaseDatabaseService {
    private static TeamsResource instance;
    private static DatabaseReference database;

    public static TeamsResource getInstance() {
        if (instance == null) {
            instance = new TeamsResource();
        }
        database = getDatabase().child(TEAMS);
        return instance;
    }

    public void addTeam(Team team, final AddDataSuccessListener handler) {
        team.setTeamId(database.push().getKey());
        addData(database.child(team.getTeamId()), team, handler);
    }

    public void editTeam(Team team, final EditDataSuccessListener handler) {
        editData(database.child(team.getTeamId()), team, handler);
    }

    public void removeTeam(String teamId, final DeleteDataSuccessListener handler) {
        deleteData(database.child(teamId), handler);
    }

    public void getTeam(String teamId, final GetTeamHandler handler) {
        getData(database.child(teamId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot snapshot) {
                Team team = snapshot.getValue(Team.class);
                handler.onTeamLoaded(team);
            }
        });
    }

    /**
     * Get teams and their logos indexed by teamId
     */
    public void getTeams(final List<String> teamIds, final GetTeamsHandler handler) {
        final Map<String, Team> teams = new HashMap<>();
        for(final String teamId : teamIds) {
            getData(database.child(teamId), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot snapshot) {
                    final Team team = snapshot.getValue(Team.class);
                    if(team != null) {
                        teams.put(team.getTeamId(), team);
                        if(team.isLogoUploaded()) {
                            LogoResource.getInstance().getLogo(teamId, new FirebaseStorageService.GetBitmapSuccessListener() {
                                @Override
                                public void onGetBitmapSuccess(Bitmap bitmap) {
                                    team.setLogo(bitmap);
                                    if(teamIds.size() == teams.size()) {
                                        handler.onTeamsLoaded(teams);
                                    }
                                }
                            });
                        } else {
                            if(teamIds.size() == teams.size()) {
                                handler.onTeamsLoaded(teams);
                            }
                        }
                    }
                }
            });
        }
    }
}
