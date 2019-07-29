package com.ardeapps.floorballcoach.dialogFragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.StringUtils;

/**
 * Created by Arttu on 29.11.2015.
 */
public class InfoDialogFragment extends DialogFragment {

    TextView title;
    TextView description;
    Button ok_button;
    String title_text;
    String desc_text;

    public static InfoDialogFragment newInstance(String title, String desc) {
        InfoDialogFragment f = new InfoDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("desc", desc);
        f.setArguments(args);

        return f;
    }

    public static InfoDialogFragment newInstance(String desc) {
        return newInstance("", desc);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_text = getArguments().getString("title", "");
        desc_text = getArguments().getString("desc", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_info, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);

        if(!StringUtils.isEmptyString(title_text)) {
            title.setVisibility(View.VISIBLE);
            title.setText(title_text);
        } else {
            title.setVisibility(View.GONE);
        }

        description.setText(desc_text);

        ok_button = v.findViewById(R.id.btn_ok);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = AppRes.getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
