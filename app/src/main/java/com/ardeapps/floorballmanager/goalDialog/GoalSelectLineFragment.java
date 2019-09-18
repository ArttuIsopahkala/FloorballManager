package com.ardeapps.floorballmanager.goalDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalSelectLineFragmentData;
import com.ardeapps.floorballmanager.views.PlayerSelector;

public class GoalSelectLineFragment extends Fragment implements DataView {

    PlayerSelector playerSelector;
    private GoalSelectLineFragmentData data;

    @Override
    public GoalSelectLineFragmentData getData() {
        data.setSelectedPlayerIds(playerSelector.getSelectedPlayerIds());
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectLineFragmentData) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        if (playerSelector.getSelectedPlayerIds().size() > data.getMaxSelectPlayers()) {
            Logger.toast(R.string.add_event_error_line_less);
            return false;
        }

        if (playerSelector.getSelectedPlayerIds().size() < data.getMinSelectPlayers()) {
            Logger.toast(R.string.add_event_error_line_more);
            return false;
        }

        return true;
    }

}
