package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.analyzer.AnalyzerService;
import com.ardeapps.floorballmanager.dialogFragments.BestLinesDialogFragment;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.LinesResource;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.LineUpSelector;

import java.util.Map;


public class LinesFragment extends Fragment {

    LineUpSelector lineUpSelector;
    Button analyzeChemistryButton;
    Button getBestLinesButton;
    Button saveButton;
    TextView infoText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        analyzeChemistryButton = v.findViewById(R.id.analyzeChemistryButton);
        getBestLinesButton = v.findViewById(R.id.getBestLinesButton);
        saveButton = v.findViewById(R.id.saveButton);
        infoText = v.findViewById(R.id.infoText);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            saveButton.setVisibility(View.VISIBLE);
            infoText.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.GONE);
            infoText.setVisibility(View.GONE);
        }

        lineUpSelector.createView(this, true, () -> {
            final Map<Integer, Line> lines = AppRes.getInstance().getLines();
            lineUpSelector.setLines(lines);
            lineUpSelector.refreshLines(false);
        });

        analyzeChemistryButton.setOnClickListener(button -> {
            if(AppRes.getInstance().getGoalsByGame().isEmpty()) {
                GoalsResource.getInstance().getAllGoals(goals -> {
                    AppRes.getInstance().setGoalsByGame(goals);
                    GameLinesResource.getInstance().getAllLines(lines1 -> {
                        AppRes.getInstance().setLinesByGame(lines1);
                        lineUpSelector.refreshLines(true);
                    });
                });
            } else {
                lineUpSelector.refreshLines(true);
            }
        });

        getBestLinesButton.setOnClickListener(button -> {
            if(AppRes.getInstance().getActivePlayers(false).size() < 5) {
                Logger.toast(AppRes.getContext().getString(R.string.lines_too_few_players));
                return;
            }
            final BestLinesDialogFragment dialog = new BestLinesDialogFragment();
            dialog.show(getChildFragmentManager(), "Valitse analysointitapa");
            dialog.setListener((allowedPlayerPosition, bestLineType, gameCount) -> {
                if(AppRes.getInstance().getGoalsByGame().isEmpty()) {
                    GoalsResource.getInstance().getAllGoals(goals -> {
                        AppRes.getInstance().setGoalsByGame(goals);
                        GameLinesResource.getInstance().getAllLines(lines1 -> {
                            AppRes.getInstance().setLinesByGame(lines1);
                            AnalyzerService.getInstance().getBestLines(allowedPlayerPosition, bestLineType, gameCount, bestLines -> AppRes.getActivity().runOnUiThread(() -> {
                                lineUpSelector.setLines(bestLines);
                                lineUpSelector.refreshLines(true);
                            }));
                        });
                    });
                } else {
                    AnalyzerService.getInstance().getBestLines(allowedPlayerPosition, bestLineType, gameCount, bestLines -> AppRes.getActivity().runOnUiThread(() -> {
                        lineUpSelector.setLines(bestLines);
                        lineUpSelector.refreshLines(true);
                    }));
                }
            });
        });

        saveButton.setOnClickListener(button -> {
            Map<Integer, Line> linesToSave = lineUpSelector.getLines();
            LinesResource.getInstance().saveLines(linesToSave, lines12 -> {
                Logger.toast(getString(R.string.lines_saved));
                AppRes.getInstance().setLines(lines12);
            });
        });

        return v;
    }
}
