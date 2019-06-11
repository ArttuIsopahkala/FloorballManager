package com.ardeapps.floorballcoach.goalDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoalSelectAssistantFragment extends Fragment {

    public PlayerSelector.Listener mListener = null;

    public void setListener(PlayerSelector.Listener l) {
        mListener = l;
    }

    PlayerSelector assistantSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_select_assistant, container, false);
        assistantSelector = v.findViewById(R.id.assistantSelector);
        Map<Integer, Line> lines = AppRes.getInstance().getLines();
        assistantSelector.createSingleSelectView(lines, mListener);
        assistantSelector.setSelectedPlayerIds(selectedPlayerIds);
        assistantSelector.setDisabledPlayerIds(disabledPlayerIds);
        return v;
    }

    public void updateSelection() {
        assistantSelector.setSelectedPlayerIds(selectedPlayerIds);
        assistantSelector.setDisabledPlayerIds(disabledPlayerIds);
    }

    public void setDisabledPlayerId(String playerId) {
        disabledPlayerIds = new ArrayList<>();
        if(playerId != null) {
            disabledPlayerIds.add(playerId);
        }
    }

    public void setAssistantPlayerId(String playerId) {
        selectedPlayerIds = new ArrayList<>();
        if(playerId != null) {
            selectedPlayerIds.add(playerId);
        }
    }

    public String getAssistantPlayerId() {
        if(assistantSelector.getSelectedPlayerIds() == null || assistantSelector.getSelectedPlayerIds().isEmpty()) {
            return null;
        }
        return assistantSelector.getSelectedPlayerIds().get(0);
    }
}
