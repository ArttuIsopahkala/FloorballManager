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
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalDetailsFragmentData;
import com.ardeapps.floorballcoach.views.TimePicker;

public class GoalDetailsFragment extends Fragment implements DataView {

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

    private GoalDetailsFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalDetailsFragmentData) viewData;
    }

    @Override
    public GoalDetailsFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_details, container, false);
        fullRadioButton = v.findViewById(R.id.fullRadioButton);
        yvRadioButton = v.findViewById(R.id.yvRadioButton);
        avRadioButton = v.findViewById(R.id.avRadioButton);
        rlRadioButton = v.findViewById(R.id.rlRadioButton);
        timePicker = v.findViewById(R.id.timePicker);

        timePicker.setTimeInMillis(data.getTime());

        if(data.getGameMode() == Goal.Mode.FULL) {
            Helper.setRadioButtonChecked(fullRadioButton, true);
        } else if(data.getGameMode() == Goal.Mode.YV) {
            Helper.setRadioButtonChecked(yvRadioButton, true);
        } else if(data.getGameMode() == Goal.Mode.AV) {
            Helper.setRadioButtonChecked(avRadioButton, true);
        } else if(data.getGameMode() == Goal.Mode.RL) {
            Helper.setRadioButtonChecked(rlRadioButton, true);
        }

        fullRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setGameMode(Goal.Mode.FULL);
                    mListener.onFullRadioButtonChecked();
                }
            }
        });
        yvRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setGameMode(Goal.Mode.YV);
                    mListener.onYvRadioButtonChecked();
                }
            }
        });
        avRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setGameMode(Goal.Mode.AV);
                    mListener.onAvRadioButtonChecked();
                }
            }
        });
        rlRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setGameMode(Goal.Mode.RL);
                    mListener.onRlRadioButtonChecked();
                }
            }
        });
        return v;
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
