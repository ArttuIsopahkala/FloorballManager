package com.ardeapps.floorballcoach.fragments;


import android.os.Bundle;
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
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.resources.LinesTeamGameResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.views.DatePicker;
import com.ardeapps.floorballcoach.views.IconView;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import static com.ardeapps.floorballcoach.utils.Helper.setSpinnerSelection;


public class GameSettingsFragment extends Fragment implements DataView {

    TextView nameText;
    DatePicker datePicker;
    AutoCompleteTextView opponentEditText;
    IconView changeIcon;
    Spinner periodSpinner;
    LineUpSelector lineUpSelector;
    Button saveButton;

    private GameSettingsFragmentData data;

    ArrayAdapter<String> spinnerArrayAdapter;
    final ArrayList<Integer> durations = new ArrayList<>(Arrays.asList(20, 15, 10, 5));
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
    public Object getData() {
        return data;
    }

    public interface Listener {
        void onGameEdited(Game game, Map<Integer, Line> lines);
        void onGameCreated(Game game, Map<Integer, Line> lines);
    }

    private void resetField() {
        isHomeGame = true;
        setTeamSides(isHomeGame);
        lineUpSelector.setLines(AppRes.getInstance().getLines());
        nameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        Helper.setEditTextValue(opponentEditText, "");
        Helper.setDatePickerValue(datePicker, Calendar.getInstance());
        periodSpinner.setSelection(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> items = new ArrayList<>();
        for(Integer duration : durations) {
            items.add(duration + "min");
        }
        spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_settings, container, false);

        nameText = v.findViewById(R.id.nameText);
        opponentEditText = v.findViewById(R.id.opponentEditText);
        datePicker = v.findViewById(R.id.datePicker);
        changeIcon = v.findViewById(R.id.changeIcon);
        periodSpinner = v.findViewById(R.id.periodSpinner);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        saveButton = v.findViewById(R.id.saveButton);

        // All opponent names to hints
        ArrayList<String> displayHints = new ArrayList<>();
        for(Game game : AppRes.getInstance().getGames().values()) {
            if(game.getOpponentName() != null) {
                if(!displayHints.contains(game.getOpponentName().toLowerCase())) {
                    displayHints.add(game.getOpponentName());
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.popup_item, displayHints);
        opponentEditText.setAdapter(adapter);

        periodSpinner.setAdapter(spinnerArrayAdapter);

        lineUpSelector.createView(this);

        resetField();

        if(data.getGame() != null) {
            isHomeGame = data.getGame().isHomeGame();
            setTeamSides(isHomeGame);
            Helper.setEditTextValue(opponentEditText, data.getGame().getOpponentName());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data.getGame().getDate());
            Helper.setDatePickerValue(datePicker, cal);
            setSpinnerSelection(periodSpinner, durations.indexOf(data.getGame().getPeriodInMinutes()));
            lineUpSelector.setLines(data.getLines());
        }

        lineUpSelector.setListener(new LineUpSelector.Listener() {

            @Override
            public void onLinesChanged() {
                // Not needed
            }
        });

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

        if(StringUtils.isEmptyString(opponentName)) {
            Logger.toast(R.string.error_empty);
            return;
        }
        Helper.hideKeyBoard(opponentEditText);

        final Game gameToSave = data.getGame() != null ? data.getGame().clone() : new Game();
        gameToSave.setDate(datePicker.getDate().getTimeInMillis());
        gameToSave.setHomeGame(isHomeGame);
        gameToSave.setPeriodInMinutes(durations.get(periodSpinner.getSelectedItemPosition()));
        gameToSave.setOpponentName(opponentName);

        final Map<Integer, Line> linesToSave = lineUpSelector.getLines();

        if(linesToSave == null || linesToSave.isEmpty()) {
            Logger.toast(R.string.error_empty);
            return;
        }

        if(data.getGame() != null) {
            GamesResource.getInstance().editGame(gameToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);

                    LinesTeamGameResource.getInstance().saveLines(gameToSave.getGameId(), linesToSave, new SaveLinesHandler() {
                        @Override
                        public void onLinesSaved(final Map<Integer, Line> lines) {
                            mListener.onGameEdited(gameToSave, lines);
                        }
                    });
                }
            });
        } else {
            GamesResource.getInstance().addGame(gameToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                @Override
                public void onAddDataSuccess(String id) {
                    gameToSave.setGameId(id);
                    AppRes.getInstance().setGame(gameToSave.getGameId(), gameToSave);

                    LinesTeamGameResource.getInstance().saveLines(gameToSave.getGameId(), linesToSave, new SaveLinesHandler() {
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
