package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.PlayerListAdapter;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.views.PlayerHolder;


public class PlayersFragment extends Fragment implements PlayerListAdapter.PlayerListManageListener {

    Button createPlayerButton;
    ListView playerList;

    PlayerListAdapter adapter;

    public void update() {
        /*players = new ArrayList<>(AppRes.getInstance().getPlayers().values());
        bluetooth.setPlayers(new ArrayList<>(AppRes.getInstance().getPlayers().values()));
        bluetooth.refreshData();
        bluetooth.notifyDataSetChanged();*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(getActivity(), PlayerHolder.ViewType.MANAGE);
        adapter.setManageListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players, container, false);

        createPlayerButton = v.findViewById(R.id.createPlayerButton);
        playerList = v.findViewById(R.id.playerList);

        playerList.setAdapter(adapter);

        adapter.refreshData();
        adapter.notifyDataSetChanged();

        createPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(null);
            }
        });

        return v;
    }

    @Override
    public void onStatisticsClick(Player player) {

    }

    @Override
    public void onEditClick(Player player) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(player);
    }
}
