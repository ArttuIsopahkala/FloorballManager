package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectAssistantFragmentData;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.List;

public class GoalSelectAssistantFragment extends Fragment implements DataView {

    public PlayerSelector.Listener mListener = null;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    PlayerSelector playerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    private GoalSelectAssistantFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalSelectAssistantFragmentData) viewData;

        disabledPlayerIds = new ArrayList<>();
        if(data.getDisabledPlayerId() != null) {
            disabledPlayerIds.add(data.getDisabledPlayerId());
        }

        selectedPlayerIds = new ArrayList<>();
        if(data.getAssistantPlayerId() != null) {
            selectedPlayerIds.add(data.getAssistantPlayerId());
        }
    }

    @Override
    public GoalSelectAssistantFragmentData getData() {
        if(playerSelector.getSelectedPlayerIds() != null) {
            data.setAssistantPlayerId(playerSelector.getSelectedPlayerIds().get(0));
        } else {
            data.setAssistantPlayerId(null);
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

}
