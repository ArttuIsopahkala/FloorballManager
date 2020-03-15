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

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;

public class EditTagDialogFragment extends DialogFragment {

    EditTagDialogCloseListener mListener = null;
    EditText tagEditText;
    Button saveButton;
    String tag;

    public void setListener(EditTagDialogCloseListener l) {
        mListener = l;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_edit_tag, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        tagEditText = v.findViewById(R.id.tagEditText);
        saveButton = v.findViewById(R.id.saveButton);

        Helper.setEditTextValue(tagEditText, "");
        if (tag != null) {
            Helper.setEditTextValue(tagEditText, tag);
        }

        saveButton.setOnClickListener(v1 -> saveTag());
        return v;
    }

    private void saveTag() {
        String tag = tagEditText.getText().toString();
        if (StringUtils.isEmptyString(tag)) {
            Logger.toast(R.string.error_empty);
            return;
        }
        Helper.hideKeyBoard(tagEditText);
        dismiss();
        mListener.onTagSaved(tag);
    }

    public interface EditTagDialogCloseListener {
        void onTagSaved(String tag);
    }
}
