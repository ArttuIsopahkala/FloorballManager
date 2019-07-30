package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.PrefRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.GameListAdapter;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Helper;

import java.util.ArrayList;

import static com.ardeapps.floorballcoach.PrefRes.SEASON_ID;


public class GamesFragment extends Fragment implements GameListAdapter.Listener {

    ListView gameList;
    TextView noGamesText;
    LinearLayout seasonContainer;
    Spinner seasonSpinner;

    GameListAdapter adapter;
    private ArrayList<String> seasonIds = new ArrayList<>();

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
        seasonContainer = v.findViewById(R.id.seasonContainer);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        gameList.setEmptyView(noGamesText);

        /*ArrayList<Game> games = new ArrayList<>(AppRes.getInstance().getGames().values());
        adapter.setGames(games);
        adapter.notifyDataSetChanged();*/

        gameList.setAdapter(adapter);

        seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        seasonTitles.add(getString(R.string.player_stats_all_seasons));
        for(Season season : AppRes.getInstance().getSeasons().values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);

        // Triggers onItemSelectedListener and initializes game list
        int seasonPosition = seasonIds.indexOf(PrefRes.getString(SEASON_ID));
        Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Game> games = getFilteredGames(position);
                adapter.setGames(games);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return v;
    }

    private ArrayList<Game> getFilteredGames(int seasonSpinnerPosition) {
        ArrayList<Game> games = new ArrayList<>(AppRes.getInstance().getGames().values());
        ArrayList<Game> filteredGames = new ArrayList<>();
        if(seasonSpinnerPosition == 0) {
            filteredGames = games;
        } else {
            String compareSeasonId = seasonIds.get(seasonSpinnerPosition - 1); // -1 because first is all
            for(Game game : games) {
                if(game != null && game.getSeasonId().equals(compareSeasonId)) {
                    filteredGames.add(game);
                }
            }
        }
        return filteredGames;
    }

    @Override
    public void onGameSelected(Game game) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToGameFragment(game);
    }
}
