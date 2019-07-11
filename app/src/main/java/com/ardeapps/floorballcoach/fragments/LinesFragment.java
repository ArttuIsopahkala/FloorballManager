package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.GetTeamGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamLinesHandler;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.resources.GoalsByTeamResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.resources.LinesTeamGameResource;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.ArrayList;
import java.util.Map;


public class LinesFragment extends Fragment {

    LineUpSelector lineUpSelector;
    Button analyzeChemistryButton;

    Map<Integer, Line> lines;

    public void refreshData() {
        lines = AppRes.getInstance().getLines();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        analyzeChemistryButton = v.findViewById(R.id.analyzeChemistryButton);

        lineUpSelector.createView(this);
        lineUpSelector.setLines(lines);
        lineUpSelector.setListener(new LineUpSelector.Listener() {

            @Override
            public void onLinesChanged() {
                Map<Integer, Line> linesToSave = lineUpSelector.getLines();
                LinesResource.getInstance().saveLines(linesToSave, new SaveLinesHandler() {
                    @Override
                    public void onLinesSaved(Map<Integer, Line> lines) {
                        AppRes.getInstance().setLines(lines);

                    }
                });
            }
        });

        analyzeChemistryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoalsByTeamResource.getInstance().getGoals(new GetTeamGoalsHandler() {
                    @Override
                    public void onTeamGoalsLoaded(Map<String, ArrayList<Goal>> goals) {
                        AppRes.getInstance().setGoalsByGame(goals);
                        LinesTeamGameResource.getInstance().getLines(new GetTeamLinesHandler() {
                            @Override
                            public void onTeamLinesLoaded(Map<String, ArrayList<Line>> lines) {
                                AppRes.getInstance().setLinesByGame(lines);
                                lineUpSelector.updateLineFragments();
                            }
                        });
                    }
                });
            }
        });
        return v;
    }

}
