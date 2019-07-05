package com.ardeapps.floorballcoach.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.ardeapps.floorballcoach.R;

public class ActionMenuDialogFragment extends DialogFragment {

    public interface GoalMenuDialogCloseListener
    {
        void onEditItem();
        void onRemoveItem();
        void onCancel();
    }

    GoalMenuDialogCloseListener mListener = null;

    public void setListener(GoalMenuDialogCloseListener l) {
        mListener = l;
    }

    Button editButton;
    Button removeButton;
    Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_menu, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        editButton = v.findViewById(R.id.editButton);
        removeButton = v.findViewById(R.id.removeButton);
        cancelButton = v.findViewById(R.id.cancelButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mListener.onEditItem();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveItem();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
            }
        });

        return v;
    }
}
