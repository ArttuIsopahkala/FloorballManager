package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectPlayerFragmentData;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.List;

public class GoalSelectPlayerFragment extends Fragment implements DataView {

    public PlayerSelector.Listener mListener = null;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    PlayerSelector playerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    private GoalSelectPlayerFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectPlayerFragmentData) viewData;

        disabledPlayerIds = new ArrayList<>();
        if(data.getDisabledPlayerId() != null) {
            disabledPlayerIds.add(data.getDisabledPlayerId());
        }

        selectedPlayerIds = new ArrayList<>();
        if(data.getPlayerId() != null) {
            selectedPlayerIds.add(data.getPlayerId());
        }
    }

    @Override
    public GoalSelectPlayerFragmentData getData() {
        if(playerSelector.getSelectedPlayerIds() != null) {
            data.setPlayerId(playerSelector.getSelectedPlayerIds().get(0));
        } else {
            data.setPlayerId(null);
        }
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.container_player_selector, container, false);
        playerSelector = v.findViewById(R.id.playerSelector);

        playerSelector.createSingleSelectView(data.getLines(), mListener);
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);
        return v;
    }

    public void updateSelection() {
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);
    }

    public boolean validate() {
        if(playerSelector.getSelectedPlayerIds().isEmpty()) {
            Logger.toast(R.string.add_event_error_scorer);
            return false;
        }

        return true;
    }

}
