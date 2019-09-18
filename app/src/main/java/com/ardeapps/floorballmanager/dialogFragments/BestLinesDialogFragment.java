package com.ardeapps.floorballmanager.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.analyzer.AllowedPlayerPosition;
import com.ardeapps.floorballmanager.analyzer.BestLineType;
import com.ardeapps.floorballmanager.utils.Helper;

import java.util.ArrayList;
import java.util.Arrays;

public class BestLinesDialogFragment extends DialogFragment {

    BestLinesDialogCloseListener mListener = null;
    RadioButton bestPositionRadioButton;
    RadioButton ownPositionRadioButton;
    RadioButton bestTeamChemistryRadioButton;
    RadioButton bestLineChemistryRadioButton;
    Spinner gamesSpinner;
    Button analyzeButton;

    public void setListener(BestLinesDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_best_lines, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        bestPositionRadioButton = v.findViewById(R.id.bestPositionRadioButton);
        ownPositionRadioButton = v.findViewById(R.id.ownPositionRadioButton);
        bestTeamChemistryRadioButton = v.findViewById(R.id.bestTeamChemistryRadioButton);
        bestLineChemistryRadioButton = v.findViewById(R.id.bestLineChemistryRadioButton);
        gamesSpinner = v.findViewById(R.id.gamesSpinner);
        analyzeButton = v.findViewById(R.id.analyzeButton);

        ArrayList<Integer> gameCounts = new ArrayList<>(Arrays.asList(1, 3, 5, 10, null));
        ArrayList<String> gameTitles = new ArrayList<>();
        for(Integer gameCount : gameCounts) {
            if(gameCount == null) {
                gameTitles.add(getString(R.string.best_lines_games_all));
            } else {
                gameTitles.add(AppRes.getContext().getString(R.string.best_lines_games_last_games, String.valueOf(gameCount)));
            }
        }
        Helper.setSpinnerAdapter(gamesSpinner, gameTitles);
        Helper.setSpinnerSelection(gamesSpinner, 0);

        Helper.setRadioButtonChecked(bestPositionRadioButton, true);
        Helper.setRadioButtonChecked(bestTeamChemistryRadioButton, true);

        analyzeButton.setOnClickListener(v1 -> {
            AllowedPlayerPosition allowedPlayerPosition;
            if(bestPositionRadioButton.isChecked()) {
                allowedPlayerPosition = AllowedPlayerPosition.BEST_POSITION;
            } else {
                allowedPlayerPosition = AllowedPlayerPosition.PLAYERS_OWN_POSITION;
            }

            BestLineType bestLineType;
            // default in version 1
            bestLineType = BestLineType.BEST_LINE_CHEMISTRY;
            /*if(bestTeamChemistryRadioButton.isChecked()) {
                bestLineType = BestLineType.BEST_TEAM_CHEMISTRY;
            } else {
                bestLineType = BestLineType.BEST_LINE_CHEMISTRY;
            }*/
            int spinnerPos = gamesSpinner.getSelectedItemPosition();
            Integer gameCount = gameCounts.get(spinnerPos);

            dismiss();
            mListener.onAnalyze(allowedPlayerPosition, bestLineType, gameCount);
        });

        return v;
    }

    public interface BestLinesDialogCloseListener {
        void onAnalyze(AllowedPlayerPosition allowedPlayerPosition, BestLineType bestLineType, Integer gameCount);
    }
}
