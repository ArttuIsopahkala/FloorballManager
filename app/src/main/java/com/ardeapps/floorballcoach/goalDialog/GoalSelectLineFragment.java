package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectLineFragmentData;
import com.ardeapps.floorballcoach.views.PlayerSelector;

public class GoalSelectLineFragment extends Fragment implements DataView {

    PlayerSelector playerSelector;
    private GoalSelectLineFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectLineFragmentData) viewData;
    }

    @Override
    public GoalSelectLineFragmentData getData() {
        data.setSelectedPlayerIds(playerSelector.getSelectedPlayerIds());
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.container_player_selector, container, false);

        playerSelector = v.findViewById(R.id.playerSelector);

        playerSelector.createMultiSelectView(data.getLines());
        playerSelector.setSelectedPlayerIds(data.getSelectedPlayerIds());
        playerSelector.setDisabledPlayerIds(data.getDisabledPlayerIds());

        return v;
    }

    public void updateSelection() {
        playerSelector.setSelectedPlayerIds(data.getSelectedPlayerIds());
        playerSelector.setDisabledPlayerIds(data.getDisabledPlayerIds());
    }

    public boolean validate() {
        if(playerSelector.getSelectedPlayerIds().size() > data.getMaxSelectPlayers()) {
            Logger.toast(R.string.add_event_error_line);
            return false;
        }

        return true;
    }

}
