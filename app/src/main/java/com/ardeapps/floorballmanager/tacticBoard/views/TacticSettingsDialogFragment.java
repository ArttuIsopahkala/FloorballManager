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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;

import java.util.ArrayList;

public class TacticSettingsDialogFragment extends DialogFragment {

    public interface TacticSettingsDialogCloseListener {
        void onSave(Field field, int paintColorProgress);
    }

    TacticSettingsDialogCloseListener mListener = null;
    ImageView fieldHalfLeftPicture;
    ImageView fieldFullPicture;
    ImageView fieldHalfRightPicture;
    View colorPreview;
    SeekBar colorPicker;
    Button saveButton;

    private ArrayList<ImageView> fields = new ArrayList<>();
    private Field selectedField = null;
    private int paintColorProgress;

    public enum Field {
        HALF_LEFT,
        FULL,
        HALF_RIGHT
    }

    public void setSelectedField(Field selectedField) {
        this.selectedField = selectedField;
    }

    public void setPaintColorProgress(int paintColorProgress) {
        this.paintColorProgress = paintColorProgress;
    }

    public void setListener(TacticSettingsDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_tactic_settings, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        fieldHalfLeftPicture = v.findViewById(R.id.fieldHalfLeftPicture);
        fieldFullPicture = v.findViewById(R.id.fieldFullPicture);
        fieldHalfRightPicture = v.findViewById(R.id.fieldHalfRightPicture);
        colorPicker = v.findViewById(R.id.colorPicker);
        colorPreview = v.findViewById(R.id.colorPreview);
        saveButton = v.findViewById(R.id.saveButton);

        setField(fieldHalfLeftPicture, Field.HALF_LEFT);
        setField(fieldFullPicture, Field.FULL);
        setField(fieldHalfRightPicture, Field.HALF_RIGHT);

        if(selectedField == Field.FULL) {
            setFieldSelected(fieldFullPicture);
        } else if(selectedField == Field.HALF_LEFT) {
            setFieldSelected(fieldHalfLeftPicture);
        } else if(selectedField == Field.HALF_RIGHT) {
            setFieldSelected(fieldHalfRightPicture);
        }

        colorPreview.post(() -> {
            colorPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        paintColorProgress = progress;
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
                colorPicker.setProgress(paintColorProgress);
            });
        });

        saveButton.setOnClickListener(v1 -> {
            mListener.onSave(selectedField, paintColorProgress);
            dismiss();
        });

        return v;
    }

    private void setField(ImageView fieldImage, Field field) {
        fields.add(fieldImage);

        fieldImage.setOnClickListener(v -> {
            for(ImageView existsFields : fields) {
                existsFields.setBackgroundColor(Color.TRANSPARENT);
            }

            setFieldSelected(fieldImage);
            selectedField = field;
        });
    }

    private void setFieldSelected(ImageView fieldImage) {
        fieldImage.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light));
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
