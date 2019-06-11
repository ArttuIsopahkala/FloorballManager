package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoalSelectScorerFragment extends Fragment {

    public PlayerSelector.Listener mListener = null;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    PlayerSelector scorerSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_select_scorer, container, false);
        scorerSelector = v.findViewById(R.id.scorerSelector);
        Map<Integer, Line> lines = AppRes.getInstance().getLines();
        scorerSelector.createSingleSelectView(lines, mListener);
        scorerSelector.setSelectedPlayerIds(selectedPlayerIds);
        scorerSelector.setDisabledPlayerIds(disabledPlayerIds);
        return v;
    }

    public void updateSelection() {
        scorerSelector.setSelectedPlayerIds(selectedPlayerIds);
        scorerSelector.setDisabledPlayerIds(disabledPlayerIds);
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
        if(scorerSelector.getSelectedPlayerIds() == null) {
            return null;
        }
        return scorerSelector.getSelectedPlayerIds().get(0);
    }

    public boolean validate() {
        if(scorerSelector.getSelectedPlayerIds() == null || scorerSelector.getSelectedPlayerIds().isEmpty()) {
            // TODO show error
            Logger.toast("Ei valittuja pelaajia");
            return false;
        }
        Logger.toast("ids: " + scorerSelector.getSelectedPlayerIds());
        return true;
    }
}
