package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.PlayerListAdapter;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.services.FragmentListeners;

import java.util.ArrayList;


public class PlayersFragment extends Fragment implements PlayerListAdapter.PlayerListSelectListener {

    Button createPlayerButton;
    ListView playerList;
    TextView noPlayersText;

    PlayerListAdapter adapter;

    public void update() {
        ArrayList<Player> players = AppRes.getInstance().getActivePlayers();
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity());
        adapter.setSelectListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players, container, false);

        createPlayerButton = v.findViewById(R.id.createPlayerButton);
        playerList = v.findViewById(R.id.playerList);
        noPlayersText = v.findViewById(R.id.noPlayersText);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if(role == UserConnection.Role.PLAYER) {
            createPlayerButton.setVisibility(View.GONE);
        } else {
            createPlayerButton.setVisibility(View.VISIBLE);
        }

        playerList.setEmptyView(noPlayersText);
        playerList.setAdapter(adapter);

        ArrayList<Player> players = AppRes.getInstance().getActivePlayers();
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();

        update();

        createPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(null);
            }
        });

        return v;
    }

    @Override
    public void onPlayerSelected(Player player) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(player);
    }
}
