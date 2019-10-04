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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GameResultDialogData;

public class GameResultDialogFragment extends DialogFragment implements DataView {

    GameResultDialogListener mListener = null;
    TextView homeNameText;
    NumberPicker homeResultPicker;
    TextView awayNameText;
    NumberPicker awayResultPicker;
    Button saveButton;

    GameResultDialogData data;

    @Override
    public GameResultDialogData getData() {
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GameResultDialogData) viewData;
    }

    public void setListener(GameResultDialogListener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_game_result, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        homeNameText = v.findViewById(R.id.homeNameText);
        homeResultPicker = v.findViewById(R.id.homeResultPicker);
        awayNameText = v.findViewById(R.id.awayNameText);
        awayResultPicker = v.findViewById(R.id.awayResultPicker);
        saveButton = v.findViewById(R.id.saveButton);

        homeResultPicker.setMinValue(0);
        homeResultPicker.setMaxValue(50);
        awayResultPicker.setMinValue(0);
        awayResultPicker.setMaxValue(50);
        homeResultPicker.setWrapSelectorWheel(false);
        awayResultPicker.setWrapSelectorWheel(false);

        homeNameText.setText(data.getHomeName());
        awayNameText.setText(data.getAwayName());
        homeResultPicker.setValue(data.getHomeGoals());
        awayResultPicker.setValue(data.getAwayGoals());

        saveButton.setOnClickListener(v1 -> saveGameResult());

        return v;
    }

    private void saveGameResult() {
        int homeGoals = homeResultPicker.getValue();
        int awayGoals = awayResultPicker.getValue();
        if(homeGoals < data.getMarkedHomeGoals() || awayGoals < data.getMarkedAwayGoals()) {
            Logger.toast(R.string.game_result_too_low);
            return;
        }

        dismiss();
        mListener.onSaveGameResult(homeGoals, awayGoals);
    }

    public interface GameResultDialogListener {
        void onSaveGameResult(int homeGoals, int awayGoals);
    }
}
