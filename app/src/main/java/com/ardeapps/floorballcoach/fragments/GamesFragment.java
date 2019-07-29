package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.GameListAdapter;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.services.FragmentListeners;


public class GamesFragment extends Fragment implements GameListAdapter.Listener {

    ListView gameList;
    TextView noGamesText;

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
        adapter = new GameListAdapter(AppRes.getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);

        gameList = v.findViewById(R.id.gameList);
        noGamesText = v.findViewById(R.id.noGamesText);
        gameList.setEmptyView(noGamesText);

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
