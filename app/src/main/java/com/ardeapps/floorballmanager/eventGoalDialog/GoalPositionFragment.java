package com.ardeapps.floorballmanager.eventGoalDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalPositionFragmentData;
import com.ardeapps.floorballmanager.views.ShootMap;

public class GoalPositionFragment extends Fragment implements DataView {

    TextView awayNameText;
    TextView homeNameText;
    ShootMap shootMap;
    GoalPositionFragmentData data;

    @Override
    public GoalPositionFragmentData getData() {
        data.setPositionPercentX(getPositionPercentX());
        data.setPositionPercentY(getPositionPercentY());
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalPositionFragmentData) viewData;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_position, container, false);

        awayNameText = v.findViewById(R.id.awayNameText);
        homeNameText = v.findViewById(R.id.homeNameText);
        shootMap = v.findViewById(R.id.shootMap);

        if (data.isOpponentGoal()) {
            homeNameText.setText(data.getOpponentName());
            awayNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        } else {
            homeNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
            awayNameText.setText(data.getOpponentName());
        }

        shootMap.initialize(true, () -> {
            shootMap.drawShootPoint(data.getPositionPercentX(), data.getPositionPercentY());
        });

        return v;
    }

    private Double getPositionPercentX() {
        return shootMap.getPositionPercentX();
    }

    private Double getPositionPercentY() {
        return shootMap.getPositionPercentY();
    }

}
