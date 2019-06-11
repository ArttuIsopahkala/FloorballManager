package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.GameListAdapter;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.services.FragmentListeners;


public class GamesFragment extends Fragment implements GameListAdapter.Listener {

    ListView gameList;

    GameListAdapter adapter;

    public void update() {
        /*players = new ArrayList<>(AppRes.getInstance().getPlayers().values());
        bluetooth.setPlayers(new ArrayList<>(AppRes.getInstance().getPlayers().values()));
        bluetooth.refreshData();
        bluetooth.notifyDataSetChanged();*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GameListAdapter(getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);

        gameList = v.findViewById(R.id.gameList);

        adapter.refreshData();
        gameList.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onGameSelected(Game game) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToGameFragment(game);
    }
}
