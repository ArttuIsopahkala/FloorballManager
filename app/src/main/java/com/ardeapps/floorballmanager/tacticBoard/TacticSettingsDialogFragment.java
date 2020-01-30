package com.ardeapps.floorballmanager.tacticBoard;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;

import java.util.ArrayList;

public class TacticSettingsDialogFragment extends DialogFragment {

    TacticSettingsDialogCloseListener mListener = null;
    ImageView fieldHalfLeftPicture;
    ImageView fieldFullPicture;
    ImageView fieldHalfRightPicture;
    Button saveButton;

    private ArrayList<ImageView> fields = new ArrayList<>();
    private Field selectedField = null;

    public enum Field {
        HALF_LEFT,
        FULL,
        HALF_RIGHT
    }

    public void setSelectedField(Field selectedField) {
        this.selectedField = selectedField;
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

        saveButton.setOnClickListener(v1 -> {
            mListener.onSave(selectedField);
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

    public interface TacticSettingsDialogCloseListener {
        void onSave(Field field);
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
