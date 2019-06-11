package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.TimePicker;

public class GoalDetailsFragment extends Fragment {

    RadioButton fullRadioButton;
    RadioButton yvRadioButton;
    RadioButton avRadioButton;
    RadioButton rlRadioButton;
    TimePicker timePicker;

    Goal.Mode mode;
    long time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_details, container, false);
        fullRadioButton = v.findViewById(R.id.fullRadioButton);
        yvRadioButton = v.findViewById(R.id.yvRadioButton);
        avRadioButton = v.findViewById(R.id.avRadioButton);
        rlRadioButton = v.findViewById(R.id.rlRadioButton);
        timePicker = v.findViewById(R.id.timePicker);

        timePicker.setTimeInMillis(time);

        if(mode == Goal.Mode.FULL) {
            Helper.setRadioButtonChecked(fullRadioButton, true);
        } else if(mode == Goal.Mode.YV) {
            Helper.setRadioButtonChecked(yvRadioButton, true);
        } else if(mode == Goal.Mode.AV) {
            Helper.setRadioButtonChecked(avRadioButton, true);
        } else if(mode == Goal.Mode.RL) {
            Helper.setRadioButtonChecked(rlRadioButton, true);
        }
        return v;
    }

    public Goal.Mode getGameMode() {
        if (fullRadioButton.isChecked()) {
            return Goal.Mode.FULL;
        } else if (yvRadioButton.isChecked()) {
            return Goal.Mode.YV;
        } else if (avRadioButton.isChecked()) {
            return Goal.Mode.AV;
        } else if (rlRadioButton.isChecked()) {
            return Goal.Mode.RL;
        }
        return null;
    }

    public void setGameMode(Goal.Mode mode) {
        this.mode = mode;
    }

    public long getTime() {
        return timePicker.getTimeInMillis();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean validate() {
        Logger.log("time " + timePicker.getTimeInMillis());

        if(timePicker.getTimeInMillis() == 0) {
            // TODO show error
            return false;
        }

        if(!fullRadioButton.isChecked() && !yvRadioButton.isChecked() && !avRadioButton.isChecked() && !rlRadioButton.isChecked()) {
            // TODO show error
            return false;
        }

        return true;
    }
}
