package com.ardeapps.floorballmanager.eventPenaltyDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.views.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;

public class PenaltyDetailsFragment extends Fragment implements DataView {

    TimePicker penaltyTimePicker;
    Spinner penaltyLengthSpinner;
    private Penalty penalty;
    ArrayList<Long> penaltyLengths = new ArrayList<>(Arrays.asList(2L, 5L, 10L, 20L));
    int penaltyLengthSpinnerPosition = 0;

    @Override
    public Penalty getData() {
        penalty.setTime(penaltyTimePicker.getTimeInMillis());
        penalty.setLength(penaltyLengths.get(penaltyLengthSpinnerPosition));
        return penalty;
    }

    @Override
    public void setData(Object viewData) {
        penalty = (Penalty) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_penalty_details, container, false);
        penaltyTimePicker = v.findViewById(R.id.penaltyTimePicker);
        penaltyLengthSpinner = v.findViewById(R.id.penaltyLengthSpinner);

        ArrayList<String> penaltyLengthsTitles = new ArrayList<>();
        for(long penaltyLength : penaltyLengths) {
            penaltyLengthsTitles.add(penaltyLength + " min");
        }

        penaltyTimePicker.setTimeInMillis(penalty.getTime());

        Helper.setSpinnerAdapter(penaltyLengthSpinner, penaltyLengthsTitles);
        if (penalty.getLength() > 0) {
            penaltyLengthSpinnerPosition = penaltyLengths.indexOf(penalty.getLength());
            Helper.setSpinnerSelection(penaltyLengthSpinner, penaltyLengthSpinnerPosition);
        } else {
            penaltyLengthSpinnerPosition = 0;
            Helper.setSpinnerSelection(penaltyLengthSpinner, 0);
        }

        penaltyLengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                penaltyLengthSpinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }

    public boolean validate() {
        if (penaltyTimePicker.getTimeInMillis() == 0) {
            Logger.toast(R.string.add_event_error_time);
            return false;
        }

        if (penaltyLengthSpinnerPosition < 0) {
            Logger.toast(R.string.add_event_error_penalty);
            return false;
        }

        return true;
    }
}
