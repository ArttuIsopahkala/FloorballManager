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
import com.ardeapps.floorballcoach.dialogFragments.EditSeasonDialogFragment;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.resources.TeamsGamesLinesResource;
import com.ardeapps.floorballcoach.resources.TeamsGamesResource;
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
import java.util.Calendar;
import java.util.Map;

import static com.ardeapps.floorballcoach.PrefRes.SEASON_ID;


public class GameSettingsFragment extends Fragment implements DataView {

    TextView nameText;
    DatePicker datePicker;
    AutoCompleteTextView opponentEditText;
    IconView changeIcon;
    TextView noSeasonsText;
    IconView addSeasonIcon;
    Spinner seasonSpinner;
    LineUpSelector lineUpSelector;
    Button saveButton;

    private GameSettingsFragmentData data;
    ArrayList<String> seasonIds = new ArrayList<>();
    boolean isHomeGame = true;

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
        if(seasons.isEmpty()) {
            noSeasonsText.setVisibility(View.VISIBLE);
            seasonSpinner.setVisibility(View.GONE);
        } else {
            seasonSpinner.setVisibility(View.VISIBLE);
            noSeasonsText.setVisibility(View.GONE);
        }

        seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        for(Season season : seasons.values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);

        lineUpSelector.createView(this, false);

        resetField();

        if(data.getGame() != null) {
            isHomeGame = data.getGame().isHomeGame();
            setTeamSides(isHomeGame);
            Helper.setEditTextValue(opponentEditText, data.getGame().getOpponentName());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data.getGame().getDate());
            Helper.setDatePickerValue(datePicker, cal);
            int seasonPosition = seasonIds.indexOf(data.getGame().getSeasonId());
            if(seasonPosition > -1) {
                Helper.setSpinnerSelection(seasonSpinner, seasonPosition);
            }
            lineUpSelector.setLines(data.getLines());
        }
    }

    private void resetField() {
        isHomeGame = true;
        setTeamSides(isHomeGame);
        lineUpSelector.setLines(AppRes.getInstance().getLines());
        nameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        Helper.setEditTextValue(opponentEditText, "");
        Helper.setDatePickerValue(datePicker, Calendar.getInstance());
        int seasonPosition = seasonIds.indexOf(PrefRes.getString(SEASON_ID));
        Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_settings, container, false);

        nameText = v.findViewById(R.id.nameText);
        opponentEditText = v.findViewById(R.id.opponentEditText);
        datePicker = v.findViewById(R.id.datePicker);
        changeIcon = v.findViewById(R.id.changeIcon);
        noSeasonsText = v.findViewById(R.id.noSeasonsText);
        addSeasonIcon = v.findViewById(R.id.addSeasonIcon);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        saveButton = v.findViewById(R.id.saveButton);

        update();

        changeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHomeGame = !isHomeGame;
                setTeamSides(isHomeGame);
            }
        });

        addSeasonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditSeasonDialogFragment dialog = new EditSeasonDialogFragment();
                dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa kautta");
                dialog.setListener(new EditSeasonDialogFragment.EditSeasonDialogCloseListener() {
                    @Override
                    public void onSeasonSaved(Season season) {
                        AppRes.getInstance().setSeason(season.getSeasonId(), season);
                        PrefRes.putString(SEASON_ID, season.getSeasonId());
                        update();
                    }
                });
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

        if(seasonIds == null || seasonIds.isEmpty() || seasonSpinner.getSelectedItemPosition() >= seasonIds.size()) {
            Logger.toast(R.string.team_settings_season_not_selected);
            return;
        }

        final Map<Integer, Line> linesToSave = lineUpSelector.getLines();
        if(linesToSave == null || linesToSave.isEmpty()) {
            Logger.toast(R.string.team_settings_no_lines);
            return;
        }

        final Game gameToSave = data.getGame() != null ? data.getGame().clone() : new Game();
        gameToSave.setDate(datePicker.getDate().getTimeInMillis());
        gameToSave.setHomeGame(isHomeGame);
        gameToSave.setOpponentName(opponentName);
        String seasonId = seasonIds.get(seasonSpinner.getSelectedItemPosition());
        gameToSave.setSeasonId(seasonId);

        if(data.getGame() != null) {
            TeamsGamesResource.getInstance().editGame(gameToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);

                    TeamsGamesLinesResource.getInstance().saveLines(gameToSave.getGameId(), linesToSave, new SaveLinesHandler() {
                        @Override
                        public void onLinesSaved(final Map<Integer, Line> lines) {
                            mListener.onGameEdited(gameToSave, lines);
                        }
                    });
                }
            });
        } else {
            // Save seasonId to show later
            PrefRes.putString(SEASON_ID, seasonId);
            TeamsGamesResource.getInstance().addGame(gameToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                @Override
                public void onAddDataSuccess(String id) {
                    gameToSave.setGameId(id);
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);

                    TeamsGamesLinesResource.getInstance().saveLines(gameToSave.getGameId(), linesToSave, new SaveLinesHandler() {
                        @Override
                        public void onLinesSaved(final Map<Integer, Line> lines) {
                            mListener.onGameCreated(gameToSave, lines);
                        }
                    });
                }
            });
        }


    }
}
