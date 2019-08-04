package com.ardeapps.floorballcoach.dialogFragments;

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

import com.ardeapps.floorballcoach.R;

public class ActionMenuDialogFragment extends DialogFragment {

    public static ActionMenuDialogFragment newInstance(String editText) {
        ActionMenuDialogFragment f = new ActionMenuDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("editText", editText);
        f.setArguments(args);

        return f;
    }

    String editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() == null) {
            editText = getString(R.string.edit);
        } else {
            editText = getArguments().getString("editText", getString(R.string.edit));
        }
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_menu, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        editButton = v.findViewById(R.id.editButton);
        removeButton = v.findViewById(R.id.removeButton);
        cancelButton = v.findViewById(R.id.cancelButton);

        editButton.setText(editText);

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
