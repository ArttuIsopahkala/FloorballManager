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
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.views.PlayerHolder;

import java.util.ArrayList;


public class PlayersFragment extends Fragment implements PlayerListAdapter.PlayerListSelectListener {

    Button createPlayerButton;
    ListView playerList;
    TextView noPlayersText;

    PlayerListAdapter adapter;

    public void update() {
        ArrayList<Player> players = new ArrayList<>();
        for(Player player : AppRes.getInstance().getPlayers().values()) {
            if(player.isActive()) {
                players.add(player);
            }
        }
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity(), PlayerHolder.ViewType.SELECT);
        adapter.setSelectListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players, container, false);

        createPlayerButton = v.findViewById(R.id.createPlayerButton);
        playerList = v.findViewById(R.id.playerList);
        noPlayersText = v.findViewById(R.id.noPlayersText);

        playerList.setEmptyView(noPlayersText);
        playerList.setAdapter(adapter);

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
