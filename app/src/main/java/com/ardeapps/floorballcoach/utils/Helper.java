package com.ardeapps.floorballcoach.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.MainActivity;
import com.ardeapps.floorballcoach.PrefRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.views.DatePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.ardeapps.floorballcoach.PrefRes.APP_STARTED_FIRST_TIME;

/**
 * Created by Arttu on 29.1.2018.
 */

public class Helper {
    private static final String MON = "mon";
    private static final String TUE = "tue";
    private static final String WED = "wed";
    private static final String THU = "thu";
    private static final String FRI = "fri";
    private static final String SAT = "sat";
    private static final String SUN = "sun";

    private static List<String> days = Arrays.asList(MON, TUE, WED, THU, FRI, SAT, SUN);

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(float sp) {
        return (int) (sp / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static void showKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) AppRes.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static void hideKeyBoard(View tokenView) {
        InputMethodManager imm = (InputMethodManager) AppRes.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(tokenView.getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isLongNumber(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppRes.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void installShortcutIfNeeded() {
        if (PrefRes.getBoolean(APP_STARTED_FIRST_TIME)) {
            Context context = AppRes.getContext();
            Intent shortcutIntent = new Intent(context, MainActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();

            // Create Implicit intent and assign Shortcut Application Name, Icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(intent);

            PrefRes.putBoolean(APP_STARTED_FIRST_TIME, false);
        }
    }

    public static void setSpinnerAdapter(Spinner spinner, ArrayList<String> titles) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(AppRes.getActivity(), android.R.layout.simple_spinner_item, titles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setAdapter(spinnerAdapter);
    }

    public static void setSpinnerSelection(final Spinner spinner, final int position) {
        spinner.post(() -> spinner.setSelection(position));
    }

    public static void setEditTextValue(final EditText editText, final String value) {
        editText.post(() -> editText.setText(value));
    }

    public static void setRadioButtonChecked(final RadioButton radioButton, final boolean checked) {
        radioButton.post(() -> radioButton.setChecked(checked));
    }

    public static void setCheckBoxChecked(final CheckBox checkBox, final boolean checked) {
        checkBox.post(() -> checkBox.setChecked(checked));
    }

    public static void setDatePickerValue(final DatePicker datePicker, final Calendar calendar) {
        datePicker.post(() -> datePicker.setDate(calendar));
    }
}
