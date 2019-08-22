package com.ardeapps.floorballmanager.goalDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalDetailsFragmentData;
import com.ardeapps.floorballmanager.views.TimePicker;

public class GoalDetailsFragment extends Fragment implements DataView {

    public Listener mListener = null;
    RadioButton fullRadioButton;
    RadioButton yvRadioButton;
    RadioButton avRadioButton;
    RadioButton rlRadioButton;
    TimePicker timePicker;
    private GoalDetailsFragmentData data;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public GoalDetailsFragmentData getData() {
        data.setTime(timePicker.getTimeInMillis());
        Goal.Mode gameMode = Goal.Mode.FULL;
        if (yvRadioButton.isChecked()) {
            gameMode = Goal.Mode.YV;
        } else if (avRadioButton.isChecked()) {
            gameMode = Goal.Mode.AV;
        } else if (rlRadioButton.isChecked()) {
            gameMode = Goal.Mode.RL;
        }
        data.setGameMode(gameMode);
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalDetailsFragmentData) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_details, container, false);
        fullRadioButton = v.findViewById(R.id.fullRadioButton);
        yvRadioButton = v.findViewById(R.id.yvRadioButton);
        avRadioButton = v.findViewById(R.id.avRadioButton);
        rlRadioButton = v.findViewById(R.id.rlRadioButton);
        timePicker = v.findViewById(R.id.timePicker);

        timePicker.setTimeInMillis(data.getTime());

        if (data.getGameMode() == Goal.Mode.FULL) {
            Helper.setRadioButtonChecked(fullRadioButton, true);
        } else if (data.getGameMode() == Goal.Mode.YV) {
            Helper.setRadioButtonChecked(yvRadioButton, true);
        } else if (data.getGameMode() == Goal.Mode.AV) {
            Helper.setRadioButtonChecked(avRadioButton, true);
        } else if (data.getGameMode() == Goal.Mode.RL) {
            Helper.setRadioButtonChecked(rlRadioButton, true);
        }

        fullRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onFullRadioButtonChecked();
            }
        });
        yvRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onYvRadioButtonChecked();
            }
        });
        avRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onAvRadioButtonChecked();
            }
        });
        rlRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onRlRadioButtonChecked();
            }
        });
        return v;
    }

    public boolean validate() {
        if (timePicker.getTimeInMillis() == 0) {
            Logger.toast(R.string.add_event_error_time);
            return false;
        }

        if (!fullRadioButton.isChecked() && !yvRadioButton.isChecked() && !avRadioButton.isChecked() && !rlRadioButton.isChecked()) {
            Logger.toast(R.string.add_event_error_mode);
            return false;
        }

        return true;
    }

    public interface Listener {
        void onFullRadioButtonChecked();

        void onAvRadioButtonChecked();

        void onYvRadioButtonChecked();

        void onRlRadioButtonChecked();
    }
}
