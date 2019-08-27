package com.ardeapps.floorballmanager.goalDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalSelectPlayerFragmentData;
import com.ardeapps.floorballmanager.views.PlayerSelector;

import java.util.ArrayList;
import java.util.List;

public class GoalSelectPlayerFragment extends Fragment implements DataView {

    public PlayerSelector.Listener mListener = null;
    PlayerSelector playerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();
    private GoalSelectPlayerFragmentData data;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    @Override
    public GoalSelectPlayerFragmentData getData() {
        if (!playerSelector.getSelectedPlayerIds().isEmpty()) {
            data.setPlayerId(playerSelector.getSelectedPlayerIds().get(0));
        } else {
            data.setPlayerId(null);
        }
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectPlayerFragmentData) viewData;

        disabledPlayerIds = new ArrayList<>();
        if (data.getDisabledPlayerId() != null) {
            disabledPlayerIds.add(data.getDisabledPlayerId());
        }

        selectedPlayerIds = new ArrayList<>();
        if (data.getPlayerId() != null) {
            selectedPlayerIds.add(data.getPlayerId());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

}