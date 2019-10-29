package com.ardeapps.floorballmanager.eventGoalDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalDetailsFragmentData;
import com.ardeapps.floorballmanager.views.TimePicker;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GoalDetailsFragment extends Fragment implements DataView {

    public interface Listener {
        void onGameModeChanged(Goal.Mode gameMode);
    }

    public Listener mListener = null;
    TimePicker timePicker;
    Spinner gameModeSpinner;
    private ArrayList<Goal.Mode> gameModes;
    private int gameModeSpinnerPosition;
    private GoalDetailsFragmentData data;

    @Override
    public GoalDetailsFragmentData getData() {
        data.setTime(timePicker.getTimeInMillis());
        data.setGameMode(gameModes.get(gameModeSpinnerPosition));
        return data;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalDetailsFragmentData) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_details, container, false);
        timePicker = v.findViewById(R.id.timePicker);
        gameModeSpinner = v.findViewById(R.id.gameModeSpinner);
        timePicker.setTimeInMillis(data.getTime());

        Map<Goal.Mode, String> gameModeMap = new TreeMap<>();
        gameModeMap.put(Goal.Mode.FULL, getString(R.string.add_event_full));
        gameModeMap.put(Goal.Mode.AV, getString(R.string.add_event_av));
        gameModeMap.put(Goal.Mode.YV, getString(R.string.add_event_yv));
        gameModeMap.put(Goal.Mode.SR, getString(R.string.add_event_sr));
        gameModeMap.put(Goal.Mode.IM, getString(R.string.add_event_im));
        gameModeMap.put(Goal.Mode.TM, getString(R.string.add_event_tm));
        gameModeMap.put(Goal.Mode.OM, getString(R.string.add_event_om));
        gameModeMap.put(Goal.Mode.RL, getString(R.string.add_event_rl));
        ArrayList<String> gameModeTitles = new ArrayList<>(gameModeMap.values());
        gameModes = new ArrayList<>(gameModeMap.keySet());
        Helper.setSpinnerAdapter(gameModeSpinner, gameModeTitles);
        if (data.getGameMode() != null) {
            gameModeSpinnerPosition = gameModes.indexOf(data.getGameMode());
            Helper.setSpinnerSelection(gameModeSpinner, gameModeSpinnerPosition);
        } else {
            gameModeSpinnerPosition = 0;
            Helper.setSpinnerSelection(gameModeSpinner, 0);
        }

        gameModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameModeSpinnerPosition = position;
                Goal.Mode gameMode = gameModes.get(position);
                mListener.onGameModeChanged(gameMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }

    public boolean validate() {
        if (timePicker.getTimeInMillis() == 0) {
            Logger.toast(R.string.add_event_error_time);
            return false;
        }

        if (gameModeSpinnerPosition < 0) {
            Logger.toast(R.string.add_event_error_mode);
            return false;
        }

        for(Goal goal : data.getGoals().values()) {
            if(!goal.getGoalId().equals(data.getCurrentGoalId()) && goal.getTime() == timePicker.getTimeInMillis()) {
                Logger.toast(R.string.add_event_error_goal_same_time);
                return false;
            }
        }
        
        return true;
    }

    public void askForWrongGameMode(Goal.Mode gameModeToChange, String description, GoalWizardDialogFragment.NextClickChangesListener handler) {
        ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(description);
        dialogFragment.show(getChildFragmentManager(), "Muutetaanko pelimuoto?");
        dialogFragment.setCancelable(false);
        dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogYesNoListener() {
            @Override
            public void onDialogYesButtonClick() {
                gameModeSpinnerPosition = gameModes.indexOf(gameModeToChange);
                Helper.setSpinnerSelection(gameModeSpinner, gameModeSpinnerPosition);
                handler.onNextClickChangesHandled();
            }

            @Override
            public void onDialogNoButtonClick() {
                handler.onNextClickChangesHandled();
            }
        });
    }
}
