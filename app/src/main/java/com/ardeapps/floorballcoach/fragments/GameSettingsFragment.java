package com.ardeapps.floorballcoach.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.PrefRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.resources.GameLinesResource;
import com.ardeapps.floorballcoach.resources.PlayerGamesResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballcoach.views.DatePicker;
import com.ardeapps.floorballcoach.views.IconView;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GameSettingsFragment extends Fragment implements DataView {

    TextView nameText;
    DatePicker datePicker;
    AutoCompleteTextView opponentEditText;
    IconView changeIcon;
    TextView seasonText;
    LineUpSelector lineUpSelector;
    Button saveButton;
    Spinner periodSpinner;

    private GameSettingsFragmentData data;
    boolean isHomeGame = true;
    final ArrayList<Long> durations = new ArrayList<>(Arrays.asList(20L, 15L, 10L, 5L));

    public Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void setData(Object viewData) {
        data = (GameSettingsFragmentData) viewData;
    }

    @Override
    public GameSettingsFragmentData getData() {
        return data;
    }

    public interface Listener {
        void onGameEdited(Game game, Map<Integer, Line> lines);
        void onGameCreated(Game game, Map<Integer, Line> lines);
    }

    public void update() {
        // All opponent names to hints
        ArrayList<String> displayHints = new ArrayList<>();
        for(Game game : AppRes.getInstance().getGames().values()) {
            if(game.getOpponentName() != null) {
                if(!displayHints.contains(game.getOpponentName().toLowerCase())) {
                    displayHints.add(game.getOpponentName());
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AppRes.getActivity(), R.layout.popup_item, displayHints);
        opponentEditText.setAdapter(adapter);

        Map<String, Season> seasons = AppRes.getInstance().getSeasons();
        String seasonId = PrefRes.getSelectedSeasonId(AppRes.getInstance().getSelectedTeam().getTeamId());
        Season season = seasons.get(seasonId);
        if(season != null) {
            seasonText.setText(season.getName());
        } else {
            seasonText.setText("-");
        }

        lineUpSelector.createView(this, false);

        resetField();

        Game game = data.getGame();
        if(game != null) {
            isHomeGame = game.isHomeGame();
            setTeamSides(isHomeGame);
            Helper.setEditTextValue(opponentEditText, game.getOpponentName());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(game.getDate());
            Helper.setDatePickerValue(datePicker, cal);
            lineUpSelector.setLines(data.getLines());
            Helper.setSpinnerSelection(periodSpinner, durations.indexOf(game.getPeriodInMinutes()));
        }
    }

    private void resetField() {
        isHomeGame = true;
        setTeamSides(isHomeGame);
        lineUpSelector.setLines(AppRes.getInstance().getLines());
        nameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        Helper.setEditTextValue(opponentEditText, "");
        Helper.setDatePickerValue(datePicker, Calendar.getInstance());

        Long duration = PrefRes.getPeriodDuration(AppRes.getInstance().getSelectedSeason().getSeasonId());
        if(duration != null) {
            Helper.setSpinnerSelection(periodSpinner, durations.indexOf(duration));
        } else {
            Helper.setSpinnerSelection(periodSpinner, 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_settings, container, false);

        nameText = v.findViewById(R.id.nameText);
        opponentEditText = v.findViewById(R.id.opponentEditText);
        datePicker = v.findViewById(R.id.datePicker);
        changeIcon = v.findViewById(R.id.changeIcon);
        seasonText = v.findViewById(R.id.seasonText);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        saveButton = v.findViewById(R.id.saveButton);
        periodSpinner = v.findViewById(R.id.periodSpinner);

        ArrayList<String> durationTitles = new ArrayList<>();
        for(Long duration : durations) {
            durationTitles.add(duration + "min");
        }
        Helper.setSpinnerAdapter(periodSpinner, durationTitles);

        update();

        changeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHomeGame = !isHomeGame;
                setTeamSides(isHomeGame);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGame();
            }
        });
        return v;
    }

    private void setTeamSides(boolean isHomeGame) {
        final RelativeLayout.LayoutParams homeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams awayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        homeParams.addRule(RelativeLayout.LEFT_OF, changeIcon.getId());
        homeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        awayParams.addRule(RelativeLayout.RIGHT_OF, changeIcon.getId());
        awayParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        if(isHomeGame) {
            nameText.setLayoutParams(homeParams);
            opponentEditText.setLayoutParams(awayParams);
        } else {
            nameText.setLayoutParams(awayParams);
            opponentEditText.setLayoutParams(homeParams);
        }
    }

    private void saveGame() {
        String opponentName = opponentEditText.getText().toString();
        Helper.hideKeyBoard(opponentEditText);

        if(StringUtils.isEmptyString(opponentName)) {
            Logger.toast(R.string.error_empty);
            return;
        }

        final Map<Integer, Line> linesToSave = lineUpSelector.getLines();
        if(linesToSave == null || linesToSave.isEmpty()) {
            Logger.toast(R.string.team_settings_no_lines);
            return;
        }

        if(AppRes.getInstance().getSelectedSeason() == null || periodSpinner.getSelectedItemPosition() < 0) {
            Logger.toast(R.string.error_empty);
            return;
        }

        String seasonId = AppRes.getInstance().getSelectedSeason().getSeasonId();
        long periodDuration = durations.get(periodSpinner.getSelectedItemPosition());
        // Save duration for season
        PrefRes.setPeriodDuration(seasonId, periodDuration);

        final Game gameToSave = data.getGame() != null ? data.getGame().clone() : new Game();
        gameToSave.setDate(datePicker.getDate().getTimeInMillis());
        gameToSave.setHomeGame(isHomeGame);
        gameToSave.setOpponentName(opponentName);
        gameToSave.setSeasonId(seasonId);
        gameToSave.setPeriodInMinutes(periodDuration);

        if(data.getGame() != null) {
            GamesResource.getInstance().editGame(gameToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);
                    saveLinesAndPlayerGames(false, gameToSave, linesToSave);
                }
            });
        } else {
            GamesResource.getInstance().addGame(gameToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                @Override
                public void onAddDataSuccess(String id) {
                    gameToSave.setGameId(id);
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);
                    saveLinesAndPlayerGames(true, gameToSave, linesToSave);
                }
            });
        }
    }

    private void saveLinesAndPlayerGames(final boolean gameCreated, final Game gameToSave, final Map<Integer, Line> linesToSave) {
        GameLinesResource.getInstance().saveLines(gameToSave.getGameId(), linesToSave, new SaveLinesHandler() {
            @Override
            public void onLinesSaved(final Map<Integer, Line> lines) {
                final Set<String> playerIdsInLines = new HashSet<>();
                for(Line line : lines.values()) {
                    playerIdsInLines.addAll(line.getPlayerIdMap().values());
                }

                Set<String> playerIdsInRemove = new HashSet<>();
                for(String playerId : AppRes.getInstance().getPlayers().keySet()) {
                    if(!playerIdsInLines.contains(playerId)) {
                        playerIdsInRemove.add(playerId);
                    }
                }

                // First remove existing games
                PlayerGamesResource.getInstance().removeGame(playerIdsInRemove, gameToSave.getGameId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                    @Override
                    public void onDeleteDataSuccess() {

                        // Add new games
                        PlayerGamesResource.getInstance().editGame(playerIdsInLines, gameToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                if(gameCreated) {
                                    mListener.onGameCreated(gameToSave, lines);
                                } else {
                                    mListener.onGameEdited(gameToSave, lines);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
