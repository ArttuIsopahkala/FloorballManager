package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalSelectScorerFragment extends Fragment implements DataView {

    public PlayerSelector.Listener mListener = null;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    PlayerSelector playerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();
    Map<Integer, Line> lines = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object viewData) {
        lines = (Map<Integer, Line>) viewData;
    }

    @Override
    public Map<Integer, Line> getData() {
        return lines;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.container_player_selector, container, false);
        playerSelector = v.findViewById(R.id.playerSelector);

        playerSelector.createSingleSelectView(lines, mListener);
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);
        return v;
    }

    public void updateSelection() {
        playerSelector.setSelectedPlayerIds(selectedPlayerIds);
        playerSelector.setDisabledPlayerIds(disabledPlayerIds);
    }

    public void setDisabledPlayerId(String playerId) {
        disabledPlayerIds = new ArrayList<>();
        if(playerId != null) {
            disabledPlayerIds.add(playerId);
        }
    }

    public void setScorerPlayerId(String playerId) {
        selectedPlayerIds = new ArrayList<>();
        if(playerId != null) {
            selectedPlayerIds.add(playerId);
        }
    }

    public String getScorerPlayerId() {
        if(playerSelector.getSelectedPlayerIds() == null) {
            return null;
        }
        return playerSelector.getSelectedPlayerIds().get(0);
    }

    public boolean validate() {
        if(playerSelector.getSelectedPlayerIds().isEmpty()) {
            Logger.toast(R.string.add_event_error_scorer);
            return false;
        }

        return true;
    }
}
