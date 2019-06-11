package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.TeamListAdapter;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Logger;

import java.util.ArrayList;


public class MainSelectionFragment extends Fragment implements TeamListAdapter.Listener {

    Button bluetoothButton;
    Button addTeamButton;
    Button addPlayerButton;
    ListView teamList;

    TeamListAdapter adapter;

    public void update() {
        //bluetooth.refreshData();
        adapter.setTeams(new ArrayList<>(AppRes.getInstance().getTeams().values()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeamListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_selection, container, false);

        bluetoothButton = v.findViewById(R.id.bluetoothButton);
        addTeamButton = v.findViewById(R.id.addTeamButton);
        addPlayerButton = v.findViewById(R.id.addPlayerButton);
        teamList = v.findViewById(R.id.teamList);

        teamList.setAdapter(adapter);

        update();

        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(null);
                Logger.toast("lisää joukkue");
            }
        });
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.toast("lisää pelaaja");
            }
        });
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToBluetoothFragment();
            }
        });
        return v;
    }

    @Override
    public void onTeamSelected(Team team) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToTeamDashboardFragment(team);
    }

    @Override
    public void onEditTeam(Team team) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(team);
    }
}
