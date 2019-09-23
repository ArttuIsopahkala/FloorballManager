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
import android.widget.EditText;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Feedback;
import com.ardeapps.floorballmanager.resources.FeedbackResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;

public class FeedbackDialogFragment extends DialogFragment {

    EditText feedbackEditText;
    Button saveButton;
    Button cancelButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_feedback, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        cancelButton = v.findViewById(R.id.cancelButton);
        saveButton = v.findViewById(R.id.saveButton);
        feedbackEditText = v.findViewById(R.id.feedbackEditText);
        feedbackEditText.setHorizontallyScrolling(false);
        feedbackEditText.setMaxLines(10);

        saveButton.setOnClickListener(v1 -> saveFeedback());
        cancelButton.setOnClickListener(v2 -> {
            Helper.hideKeyBoard(feedbackEditText);
            dismiss();
        });
        return v;
    }

    private void saveFeedback() {
        String feedbackText = feedbackEditText.getText().toString();

        if (StringUtils.isEmptyString(feedbackText)) {
            Logger.toast(R.string.error_empty);
            return;
        }

        Helper.hideKeyBoard(feedbackEditText);

        Feedback feedback = new Feedback();
        feedback.setTime(System.currentTimeMillis());
        feedback.setUserId(AppRes.getInstance().getUser().getUserId());
        feedback.setFeedback(feedbackText);

        FeedbackResource.getInstance().addFeedback(feedback, id -> {
            dismiss();
            Logger.toast(R.string.feedback_sent);
        });
    }
}
