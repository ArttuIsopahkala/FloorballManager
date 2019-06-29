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

import java.util.ArrayList;
import java.util.List;

public class GoalSelectLineFragment extends Fragment implements DataView {

    PlayerSelector playerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    private GoalSelectLineFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectLineFragmentData) viewData;
    }

    @Override
    public GoalSelectLineFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.container_player_selector, container, false);

        playerSelector = v.findViewById(R.id.playerSelector);

        playerSelector.createMultiSelectView(data.getLines());
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);

        return v;
    }

    public void updateSelection() {
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);
    }

    public void setDisabledPlayerIds(List<String> playerIds) {
        disabledPlayerIds = new ArrayList<>();
        if(playerIds != null) {
            disabledPlayerIds.addAll(playerIds);
        }
    }

    public void setSelectedPlayerIds(List<String> playerIds) {
        selectedPlayerIds = new ArrayList<>();
        if(playerIds != null) {
            selectedPlayerIds.addAll(playerIds);
        }
    }

    public List<String> getSelectedPlayerIds() {
        return playerSelector.getSelectedPlayerIds();
    }

    public boolean validate() {
        if(playerSelector.getSelectedPlayerIds().size() > data.getMaxSelectPlayers()) {
            Logger.toast(R.string.add_event_error_line);
            return false;
        }

        return true;
    }

}
