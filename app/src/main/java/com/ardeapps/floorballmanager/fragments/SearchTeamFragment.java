package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.TeamListAdapter;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserRequestsResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;


public class SearchTeamFragment extends Fragment implements TeamListAdapter.Listener {

    TextView noResultText;
    IconView searchIcon;
    EditText searchText;
    ListView teamList;
    TeamListAdapter adapter;

    private ArrayList<Team> teams = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeamListAdapter(AppRes.getActivity(), TeamListAdapter.Type.JOIN);
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_team, container, false);

        noResultText = v.findViewById(R.id.noResultText);
        searchIcon = v.findViewById(R.id.searchIcon);
        searchText = v.findViewById(R.id.searchText);
        teamList = v.findViewById(R.id.teamList);

        teamList.setEmptyView(noResultText);
        teamList.setAdapter(adapter);

        Helper.setEditTextValue(searchText, "");
        updateTeamsList(new ArrayList<>());

        searchIcon.setOnClickListener(v1 -> {
            String search = searchText.getText().toString();
            if(!StringUtils.isEmptyString(search)) {
                if(teams.isEmpty()) {
                    TeamsResource.getInstance().getAllTeams(teams -> {
                        this.teams = new ArrayList<>(teams.values());
                        searchTeams(search);
                    });
                } else {
                    searchTeams(search);
                }
            }
        });

        return v;
    }

    private void searchTeams(String search) {
        ArrayList<Team> result = new ArrayList<>();
        for (Team team : teams) {
            if (team.getName().toLowerCase().contains(search.toLowerCase())) {
                result.add(team);
            }
        }
        updateTeamsList(result);
    }

    private void updateTeamsList(ArrayList<Team> teams) {
        adapter.setTeams(teams);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTeamSelected(Team team) {
        ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.search_team_join_request));
        dialogFragment.show(getChildFragmentManager(), "Lähetetäänkö liittymispyyntö?");
        dialogFragment.setListener(() -> {
            TeamsResource.getInstance().getTeam(team.getTeamId(), false, existingTeam -> {
                // Check that team still exists
                if(existingTeam == null) {
                    Logger.toast(R.string.search_team_removed);
                    return;
                }

                User user = AppRes.getInstance().getUser();
                UserRequest userRequest = new UserRequest();
                userRequest.setEmail(user.getEmail());
                userRequest.setUserId(user.getUserId());
                userRequest.setTeamId(team.getTeamId());
                userRequest.setStatus(UserRequest.Status.PENDING.toDatabaseName());
                UserRequestsResource.getInstance().addUserRequest(userRequest, id -> {
                    userRequest.setUserConnectionId(id);
                    userRequest.setTeam(team);
                    AppRes.getInstance().setUserRequest(userRequest.getUserConnectionId(), userRequest);
                    adapter.notifyDataSetChanged();
                });
            });
        });
    }
}