package com.ardeapps.floorballmanager.resources;

import android.graphics.Bitmap;

import com.ardeapps.floorballmanager.handlers.GetTeamHandler;
import com.ardeapps.floorballmanager.handlers.GetTeamsHandler;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
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
        deleteData(database.child(team.getTeamId()), () -> {
            if (team.isLogoUploaded()) {
                LogoResource.getInstance().removeLogo(team.getTeamId(), handler::onDeleteDataSuccess);
            } else {
                handler.onDeleteDataSuccess();
            }
        });
    }

    public void getTeam(String teamId, final boolean withLogo, final GetTeamHandler handler) {
        getData(database.child(teamId), snapshot -> {
            final Team team = snapshot.getValue(Team.class);
            if (team != null) {
                if (withLogo && team.isLogoUploaded()) {
                    LogoResource.getInstance().getLogo(team.getTeamId(), bitmap -> {
                        team.setLogo(bitmap);
                        handler.onTeamLoaded(team);
                    });
                } else {
                    handler.onTeamLoaded(team);
                }
            } else {
                handler.onTeamLoaded(null);
            }
        });
    }

    /**
     * Get all teams and their logos indexed by teamId
     */
    public void getAllTeams(final GetTeamsHandler handler) {
        getData(database, dataSnapshot -> {
            final Map<String, Team> teams = new HashMap<>();
            final ArrayList<String> teamsWithLogo = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final Team team = snapshot.getValue(Team.class);
                if (team != null) {
                    teams.put(team.getTeamId(), team);
                    if (team.isLogoUploaded()) {
                        teamsWithLogo.add(team.getTeamId());
                    }
                }
            }

            if (!teamsWithLogo.isEmpty()) {
                LogoResource.getInstance().getLogos(teamsWithLogo, bitmaps -> {
                    for (Map.Entry<String, Bitmap> entry : bitmaps.entrySet()) {
                        String teamId = entry.getKey();
                        Bitmap bitmap = entry.getValue();
                        Team team = teams.get(teamId);
                        if (team != null) {
                            team.setLogo(bitmap);
                        }
                    }
                    handler.onTeamsLoaded(teams);
                });
            } else {
                handler.onTeamsLoaded(teams);
            }
        });
    }

    /**
     * Get teams and their logos indexed by teamId
     */
    public void getTeams(final List<String> teamIds, final GetTeamsHandler handler) {
        getTeamsData(teamIds, teams -> {
            final ArrayList<String> teamIdsWithLogo = new ArrayList<>();
            for (final Team team : teams.values()) {
                if (team.isLogoUploaded()) {
                    teamIdsWithLogo.add(team.getTeamId());
                }
            }
            // Get logos
            if (!teamIdsWithLogo.isEmpty()) {
                LogoResource.getInstance().getLogos(teamIdsWithLogo, bitmaps -> {
                    for (Map.Entry<String, Bitmap> entry : bitmaps.entrySet()) {
                        String teamId = entry.getKey();
                        Bitmap bitmap = entry.getValue();
                        Team team = teams.get(teamId);
                        if (team != null) {
                            team.setLogo(bitmap);
                        }
                    }
                    handler.onTeamsLoaded(teams);
                });
            } else {
                handler.onTeamsLoaded(teams);
            }
        });
    }

    private void getTeamsData(final List<String> teamIds, final GetTeamsHandler handler) {
        final Map<String, Team> teams = new HashMap<>();
        ArrayList<String> failedTeams = new ArrayList<>();
        for (final String teamId : teamIds) {
            getData(database.child(teamId), snapshot -> {
                final Team team = snapshot.getValue(Team.class);
                if (team != null) {
                    teams.put(team.getTeamId(), team);
                } else {
                    failedTeams.add(teamId);
                }
                if (teams.size() + failedTeams.size() == teamIds.size()) {
                    handler.onTeamsLoaded(teams);
                }
            });
        }
    }

}
