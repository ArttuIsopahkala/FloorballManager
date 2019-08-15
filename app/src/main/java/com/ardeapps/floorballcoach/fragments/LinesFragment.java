package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.GetGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamLinesHandler;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.resources.GameLinesResource;
import com.ardeapps.floorballcoach.resources.GoalsResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.ArrayList;
import java.util.Map;


public class LinesFragment extends Fragment {

    LineUpSelector lineUpSelector;
    Button analyzeChemistryButton;
    Button getBestLinesButton;
    TextView teamChemistryValueText;
    ProgressBar teamChemistryBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        analyzeChemistryButton = v.findViewById(R.id.analyzeChemistryButton);
        getBestLinesButton = v.findViewById(R.id.getBestLinesButton);
        teamChemistryValueText = v.findViewById(R.id.teamChemistryValueText);
        teamChemistryBar = v.findViewById(R.id.teamChemistryBar);

        refreshTeamChemistry();
        lineUpSelector.createView(this, true);
        final Map<Integer, Line> lines = AppRes.getInstance().getLines();
        lineUpSelector.setLines(lines);
        lineUpSelector.setListener(() -> {
            Map<Integer, Line> linesToSave = lineUpSelector.getLines();
            LinesResource.getInstance().saveLines(linesToSave, lines12 -> {
                AppRes.getInstance().setLines(lines12);
                lineUpSelector.setLines(lines12);
                refreshTeamChemistry();
            });
        });

        analyzeChemistryButton.setOnClickListener(v12 -> GoalsResource.getInstance().getAllGoals(goals -> {
            AppRes.getInstance().setGoalsByGame(goals);
            GameLinesResource.getInstance().getLines(lines1 -> {
                AppRes.getInstance().setLinesByGame(lines1);
                lineUpSelector.updateLineFragments();
                refreshTeamChemistry();
            });
        }));

        getBestLinesButton.setOnClickListener(v1 -> Logger.toast("Hae parhaat kentät"));

        return v;
    }

    private void refreshTeamChemistry() {
        Map<Integer, Line> lines = AppRes.getInstance().getLines();
        int percent = AnalyzerService.getInstance().getTeamChemistryPercent(lines);
        teamChemistryValueText.setText(String.valueOf(percent));
        teamChemistryBar.setProgress(percent);
    }

}
