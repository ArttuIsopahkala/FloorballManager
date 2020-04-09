package com.ardeapps.floorballmanager.tacticBoard.views;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;

import java.util.ArrayList;

public class TacticTeamSettingsDialogFragment extends DialogFragment {

    public interface TacticTeamSettingsDialogCloseListener {
        void onSave();
    }

    TacticTeamSettingsDialogCloseListener mListener = null;
    View colorPreview;
    SeekBar colorPicker;
    Button saveButton;

    private ArrayList<TacticBoardAnimation.TacticPlayer> players;
    private int colorProgress;

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
    }

    public int getColorProgress() {
        return colorProgress;
    }

    public void setPlayers(ArrayList<TacticBoardAnimation.TacticPlayer> players) {
        this.players = players;
    }

    public ArrayList<TacticBoardAnimation.TacticPlayer> getPlayers() {
        return players;
    }

    public void setListener(TacticTeamSettingsDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_tactic_team_settings, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        colorPicker = v.findViewById(R.id.colorPicker);
        colorPreview = v.findViewById(R.id.colorPreview);
        saveButton = v.findViewById(R.id.saveButton);

        colorPreview.post(() -> {
            colorPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        colorProgress = progress;
                    }
                    int selectedColor = TacticBoardHelper.getColorFromProgress(progress);
                    colorPreview.setBackgroundColor(selectedColor);
                    colorPreview.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // COLOR OF PEN
            colorPicker.post(() -> {
                LinearGradient colorGradient = new LinearGradient(0.f, 0.f, colorPicker.getMeasuredWidth() - colorPicker.getThumb().getIntrinsicWidth(), 0.f,
                        new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                                0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                        null, Shader.TileMode.CLAMP
                );
                ShapeDrawable shape = new ShapeDrawable(new RectShape());
                shape.getPaint().setShader(colorGradient);
                colorPicker.setProgressDrawable(shape);
                colorPicker.setMax(256 * 7 - 1);
                colorPicker.setProgress(colorProgress);
            });
        });

        saveButton.setOnClickListener(v1 -> {
            mListener.onSave();
            dismiss();
        });

        return v;
    }

    // This sets dialog full screen
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
        }
    }
}
