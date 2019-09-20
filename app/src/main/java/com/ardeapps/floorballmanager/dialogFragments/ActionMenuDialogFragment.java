package com.ardeapps.floorballmanager.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.UserConnection;

public class ActionMenuDialogFragment extends DialogFragment {

    String editString;
    String removeString;
    GoalMenuDialogCloseListener mListener = null;
    Button editButton;
    Button cancelButton;
    TextView removeText;

    public static ActionMenuDialogFragment newInstance(String editText, String removeText) {
        ActionMenuDialogFragment f = new ActionMenuDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("editString", editText);
        args.putString("removeString", removeText);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            editString = getString(R.string.edit);
            removeString = getString(R.string.remove);
        } else {
            editString = getArguments().getString("editString", getString(R.string.edit));
            removeString = getArguments().getString("removeString", getString(R.string.remove));
        }
    }

    public void setListener(GoalMenuDialogCloseListener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_action_menu, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        editButton = v.findViewById(R.id.editButton);
        cancelButton = v.findViewById(R.id.cancelButton);
        removeText = v.findViewById(R.id.removeText);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            removeText.setVisibility(View.VISIBLE);
        } else {
            removeText.setVisibility(View.GONE);
        }

        editButton.setText(editString);
        removeText.setText(Html.fromHtml("<u>" + removeString + "</u>"));

        editButton.setOnClickListener(v13 -> mListener.onEditItem());

        removeText.setOnClickListener(v12 -> mListener.onRemoveItem());

        cancelButton.setOnClickListener(v1 -> mListener.onCancel());

        return v;
    }

    public interface GoalMenuDialogCloseListener {
        void onEditItem();

        void onRemoveItem();

        void onCancel();
    }

}
