package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.TimePicker;

public class GoalDetailsFragment extends Fragment {

    public Listener mListener = null;

    public interface Listener {
        void onFullRadioButtonChecked();
        void onAvRadioButtonChecked();
        void onYvRadioButtonChecked();
        void onRlRadioButtonChecked();
    }

    public void setListener(Listener l) {
        mListener = l;
    }

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
            fullRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        mListener.onFullRadioButtonChecked();
                    }
                }
            });
        } else if(mode == Goal.Mode.YV) {
            Helper.setRadioButtonChecked(yvRadioButton, true);
            yvRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        mListener.onYvRadioButtonChecked();
                    }
                }
            });
        } else if(mode == Goal.Mode.AV) {
            Helper.setRadioButtonChecked(avRadioButton, true);
            avRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        mListener.onAvRadioButtonChecked();
                    }
                }
            });
        } else if(mode == Goal.Mode.RL) {
            Helper.setRadioButtonChecked(rlRadioButton, true);
            rlRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        mListener.onRlRadioButtonChecked();
                    }
                }
            });
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
        if(timePicker.getTimeInMillis() == 0) {
            Logger.toast(R.string.add_event_error_time);
            return false;
        }

        if(!fullRadioButton.isChecked() && !yvRadioButton.isChecked() && !avRadioButton.isChecked() && !rlRadioButton.isChecked()) {
            Logger.toast(R.string.add_event_error_mode);
            return false;
        }

        return true;
    }
}
