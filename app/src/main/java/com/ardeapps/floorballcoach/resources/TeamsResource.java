package com.ardeapps.floorballcoach.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballcoach.handlers.GetTeamHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamsHandler;
import com.ardeapps.floorballcoach.objects.Team;
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

    public void removeTeam(final Team team, final DeleteDataSuccessListener handler) {
        deleteData(database.child(team.getTeamId()), new DeleteDataSuccessListener() {
            @Override
            public void onDeleteDataSuccess() {
                if(team.isLogoUploaded()) {
                    LogoResource.getInstance().removeLogo(team.getTeamId(), new FirebaseStorageService.DeleteBitmapSuccessListener() {
                        @Override
                        public void onDeleteBitmapSuccess() {
                            handler.onDeleteDataSuccess();
                        }
                    });
                } else {
                    handler.onDeleteDataSuccess();
                }
            }
        });
    }

    public void getTeam(String teamId, final boolean withLogo, final GetTeamHandler handler) {
        getData(database.child(teamId), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot snapshot) {
                final Team team = snapshot.getValue(Team.class);
                if(team != null) {
                    if(withLogo && team.isLogoUploaded()) {
                        LogoResource.getInstance().getLogo(team.getTeamId(), new FirebaseStorageService.GetBitmapSuccessListener() {
                            @Override
                            public void onGetBitmapSuccess(Bitmap bitmap) {
                                team.setLogo(bitmap);
                                handler.onTeamLoaded(team);
                            }
                        });
                    } else {
                        handler.onTeamLoaded(team);
                    }
                }
            }
        });
    }

    /**
     * Get teams and their logos indexed by teamId
     */
    public void getTeams(final List<String> teamIds, final GetTeamsHandler handler) {
        getTeamsData(teamIds, new GetTeamsHandler() {
            @Override
            public void onTeamsLoaded(final Map<String, Team> teams) {
                final ArrayList<String> teamIdsWithLogo = new ArrayList<>();
                for (final Team team : teams.values()) {
                    if(team.isLogoUploaded()) {
                        teamIdsWithLogo.add(team.getTeamId());
                    }
                }
                // Get logos
                if(!teamIdsWithLogo.isEmpty()) {
                    LogoResource.getInstance().getLogos(teamIdsWithLogo, new FirebaseStorageService.GetBitmapsSuccessListener() {
                        @Override
                        public void onGetBitmapsSuccess(Map<String, Bitmap> bitmaps) {
                            for(Map.Entry<String, Bitmap> entry : bitmaps.entrySet()) {
                                String teamId = entry.getKey();
                                Bitmap bitmap = entry.getValue();
                                Team team = teams.get(teamId);
                                if(team != null) {
                                    team.setLogo(bitmap);
                                }
                            }
                            handler.onTeamsLoaded(teams);
                        }
                    });
                } else {
                    handler.onTeamsLoaded(teams);
                }
            }
        });
    }

    private void getTeamsData(final List<String> teamIds, final GetTeamsHandler handler) {
        final Map<String, Team> teams = new HashMap<>();
        for (final String teamId : teamIds) {
            getData(database.child(teamId), new GetDataSuccessListener() {
                @Override
                public void onGetDataSuccess(DataSnapshot snapshot) {
                    final Team team = snapshot.getValue(Team.class);
                    if (team != null) {
                        teams.put(team.getTeamId(), team);
                        if(teams.size() == teamIds.size()) {
                            handler.onTeamsLoaded(teams);
                        }
                    }
                }
            });
        }
    }

}
