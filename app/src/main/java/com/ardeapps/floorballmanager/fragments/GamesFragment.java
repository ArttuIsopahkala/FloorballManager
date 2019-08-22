package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.PrefRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.GameListAdapter;
import com.ardeapps.floorballmanager.dialogFragments.EditSeasonDialogFragment;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GamesFragment extends Fragment implements GameListAdapter.Listener {

    TextView noSeasonsText;
    IconView addSeasonIcon;
    Spinner seasonSpinner;
    Button newGameButton;
    ListView gameList;
    TextView noGamesText;

    GameListAdapter adapter;
    ArrayList<String> seasonIds = new ArrayList<>();

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

        noSeasonsText = v.findViewById(R.id.seasonText);
        addSeasonIcon = v.findViewById(R.id.addSeasonIcon);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        newGameButton = v.findViewById(R.id.newGameButton);
        gameList = v.findViewById(R.id.gameList);
        noGamesText = v.findViewById(R.id.noGamesText);

        gameList.setEmptyView(noGamesText);
        gameList.setAdapter(adapter);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.PLAYER) {
            addSeasonIcon.setVisibility(View.GONE);
            newGameButton.setVisibility(View.GONE);
        } else {
            addSeasonIcon.setVisibility(View.VISIBLE);
            newGameButton.setVisibility(View.VISIBLE);
        }

        setSeasonSpinner();
        // Set default spinner selection
        int seasonPosition = 0;
        if (!AppRes.getInstance().getSeasons().isEmpty()) {
            String seasonId = PrefRes.getSelectedSeasonId(AppRes.getInstance().getSelectedTeam().getTeamId());
            if (seasonId != null) {
                seasonPosition = seasonIds.indexOf(seasonId);
            }
            Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);
        }

        addSeasonIcon.setOnClickListener(v12 -> {
            final EditSeasonDialogFragment dialog = new EditSeasonDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Lisää tunniste");
            dialog.setListener(season -> {
                AppRes.getInstance().setSeason(season.getSeasonId(), season);
                setSeasonSpinner();
                loadGames(season.getSeasonId());
            });
        });

        newGameButton.setOnClickListener(v1 -> {
            // Force user to add season before add game
            if (AppRes.getInstance().getSeasons().isEmpty()) {
                final EditSeasonDialogFragment dialog = new EditSeasonDialogFragment();
                dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Lisää tunniste");
                dialog.setListener(season -> {
                    AppRes.getInstance().setSeason(season.getSeasonId(), season);
                    setSeasonSpinner();
                    loadGames(season.getSeasonId());
                    FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(new GameSettingsFragmentData());
                });
            } else {
                FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(new GameSettingsFragmentData());
            }


        });
        return v;
    }

    public void setSeasonSpinner() {
        Map<String, Season> seasons = AppRes.getInstance().getSeasons();

        if (seasons.isEmpty()) {
            noSeasonsText.setVisibility(View.VISIBLE);
            seasonSpinner.setVisibility(View.GONE);
        } else {
            noSeasonsText.setVisibility(View.GONE);
            seasonSpinner.setVisibility(View.VISIBLE);
        }
        seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        for (Season season : seasons.values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seasonId = seasonIds.get(position);
                loadGames(seasonId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadGames(final String seasonId) {
        if (seasonId == null) {
            AppRes.getInstance().setGames(new HashMap<>());
            refreshGames();
        } else {
            Map<String, Season> seasons = AppRes.getInstance().getSeasons();
            Season selectedSeason = seasons.get(seasonId);
            PrefRes.setSelectedSeasonId(AppRes.getInstance().getSelectedTeam().getTeamId(), seasonId);
            AppRes.getInstance().setSelectedSeason(selectedSeason);
            GamesResource.getInstance().getGames(seasonId, games -> {
                AppRes.getInstance().setGames(games);
                refreshGames();
            });
        }
    }

    public void refreshGames() {
        ArrayList<Game> games = new ArrayList<>(AppRes.getInstance().getGames().values());
        adapter.setGames(games);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGameSelected(Game game) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToGameFragment(game);
    }
}
