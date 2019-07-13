package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.handlers.GetTeamGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamLinesHandler;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.resources.GoalsByTeamResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.resources.LinesTeamGameResource;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.LinesFragmentData;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.ArrayList;
import java.util.Map;


public class LinesFragment extends Fragment implements DataView {

    LineUpSelector lineUpSelector;
    Button analyzeChemistryButton;
    Button getBestLinesButton;
    Button saveButton;

    private LinesFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (LinesFragmentData) viewData;
        Map<Integer, Line> lines = data.getLines();
        for(Line line : lines.values()) {
            Logger.log("JJ line " + line.getLineNumber() + ": " + line.getPlayerIdMap());
        }
    }

    @Override
    public LinesFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        analyzeChemistryButton = v.findViewById(R.id.analyzeChemistryButton);
        getBestLinesButton = v.findViewById(R.id.getBestLinesButton);
        saveButton = v.findViewById(R.id.saveButton);

        lineUpSelector.createView(this);
        lineUpSelector.setLines(data.getLines());

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

        getBestLinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.toast("Hae parhaat kentät");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLines();
            }
        });

        // TODO TÄMÄ EI TOIMI, koska data.getLines päivittyy jotenkin automaattisesti aina kuin
        // kenttiä muutetaan. Miten se on mahdollista?
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Check if lines are changed -> ask to save
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if(isLinesChanged()) {
                        ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.lineup_lines_changed));
                        if(dialogFragment.isVisible()) {
                            dialogFragment.dismiss();
                        } else {
                            dialogFragment.show(getChildFragmentManager(), "Tallennetaanko muutokset?");
                            dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogYesNoListener() {
                                @Override
                                public void onDialogYesButtonClick() {
                                    Map<Integer, Line> linesToSave = lineUpSelector.getLines();
                                    LinesResource.getInstance().saveLines(linesToSave, new SaveLinesHandler() {
                                        @Override
                                        public void onLinesSaved(Map<Integer, Line> lines) {
                                            AppRes.getInstance().setLines(lines);
                                            Logger.toast(getString(R.string.lineup_lines_saved));
                                        }
                                    });
                                }

                                @Override
                                public void onDialogNoButtonClick() {
                                    FragmentActivity activity = getActivity();
                                    if(activity != null) {
                                        activity.onBackPressed();
                                    }
                                }
                            });
                        }
                    }
                }
                return false;
            }
        });

        return v;
    }

    private boolean isLinesChanged() {
        boolean changed = false;
        for(Line line : data.getLines().values()) {
            Logger.log("JJ lineCha " + line.getLineNumber() + ": " + line.getPlayerIdMap());
        }
        for(int lineNumber = 1; lineNumber <= 4; lineNumber++) {
            Line newLine = lineUpSelector.getLines().get(lineNumber);
            Line oldLine = data.getLines().get(lineNumber);

            // If other is null
            if((oldLine == null) != (newLine == null)) {
                changed = true;
            }

            if(oldLine != null && newLine != null && !oldLine.isSame(newLine)) {
                changed = true;
            }
        }
        return changed;
    }

    private void saveLines() {
        Map<Integer, Line> linesToSave = lineUpSelector.getLines();
        LinesResource.getInstance().saveLines(linesToSave, new SaveLinesHandler() {
            @Override
            public void onLinesSaved(Map<Integer, Line> lines) {
                AppRes.getInstance().setLines(lines);
                Logger.toast(getString(R.string.lineup_lines_saved));
            }
        });
    }
}
