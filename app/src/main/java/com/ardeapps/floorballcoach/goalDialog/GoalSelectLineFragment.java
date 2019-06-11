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

public class GoalSelectLineFragment extends Fragment {

    PlayerSelector lineSelector;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_select_line, container, false);

        lineSelector = v.findViewById(R.id.lineSelector);
        Map<Integer, Line> lines = AppRes.getInstance().getLines();
        lineSelector.createMultiSelectView(lines);
        lineSelector.setSelectedPlayerIds(selectedPlayerIds);
        lineSelector.setDisabledPlayerIds(disabledPlayerIds);

        return v;
    }
    public void updateSelection() {
        lineSelector.setSelectedPlayerIds(selectedPlayerIds);
        lineSelector.setDisabledPlayerIds(disabledPlayerIds);
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
        return lineSelector.getSelectedPlayerIds();
    }

    public boolean validate() {
        if(lineSelector.getSelectedPlayerIds().isEmpty()) {
            // TODO show error
            Logger.toast("Ei valittuja pelaajia");
            return false;
        }
        Logger.toast("ids: " + lineSelector.getSelectedPlayerIds());
        return true;
    }
}
