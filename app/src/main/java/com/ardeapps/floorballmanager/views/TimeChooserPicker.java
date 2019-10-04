package com.ardeapps.floorballmanager.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeChooserPicker extends LinearLayout {
    TimeEditText hoursText;
    TimeEditText minutesText;
    IconView chooserIcon;
    int hours;
    int minutes;

    public TimeChooserPicker(Context context) {
        super(context);
        createView(context);
    }

    public TimeChooserPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.time_chooser_picker, this);
        chooserIcon = findViewById(R.id.chooserIcon);
        hoursText = findViewById(R.id.hoursText);
        minutesText = findViewById(R.id.minutesText);
        clearFocus();

        chooserIcon.setOnClickListener(v -> {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            hours = c.get(Calendar.HOUR_OF_DAY);
            minutes = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(AppRes.getActivity(),
                    (view, hourOfDay, minute) -> {
                        hoursText.setText(String.valueOf(hourOfDay));
                        minutesText.setText(String.valueOf(minute));
                    }, hours, minutes, true);
            timePickerDialog.show();
        });
    }

    private void setTextListener(TimeEditText editText) {
        // ON FOCUS CHANGE
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(editText.getId() == hoursText.getId()) {
                    formatHoursText();
                } else if(editText.getId() == minutesText.getId()) {
                    formatMinutesText();
                }
            }
        });
        // ON TEXT CHANGE
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String valueString = s.toString();
                if (!StringUtils.isEmptyString(valueString)) {
                    int value = Integer.parseInt(valueString);

                    // Format to limits
                    if(editText.getId() == hoursText.getId()) {
                        if (value > 23) {
                            setHoursText(23);
                        }
                    } else if(editText.getId() == minutesText.getId()) {
                        if (value > 59) {
                            setMinutesText(59);
                        }
                    }

                    if (count == 2) {
                        // Change focus is max length
                        if (editText.getId() == minutesText.getId()) {
                            editText.clearFocus();
                            Helper.hideKeyBoard(editText);
                        } else {
                            TextView nextField = (TextView) editText.focusSearch(View.FOCUS_RIGHT);
                            nextField.requestFocus();
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        // IME
        editText.setKeyImeChangeListener((keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if(editText.getId() == hoursText.getId()) {
                    formatHoursText();
                } else if(editText.getId() == minutesText.getId()) {
                    formatMinutesText();
                }

                editText.clearFocus();
                Helper.hideKeyBoard(editText);
            }
        });
        // EDITOR
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                if(editText.getId() == hoursText.getId()) {
                    formatHoursText();
                }
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(editText.getId() == minutesText.getId()) {
                    formatMinutesText();
                    editText.clearFocus();
                    Helper.hideKeyBoard(editText);
                }
            }
            return false;
        });
    }

    private void formatHoursText() {
        int hour = hours;
        String hoursString = hoursText.getText().toString();
        if (!StringUtils.isEmptyString(hoursString)) {
            hour = Integer.parseInt(hoursString);
            if (hour > 23) {
                hour = hours;
            }
        }
        setHoursText(hour);
    }

    private void formatMinutesText() {
        int minute = minutes;
        String minutesString = minutesText.getText().toString();
        if (!StringUtils.isEmptyString(minutesString)) {
            minute = Integer.parseInt(minutesString);
            if (minute > 59) {
                minute = minutes;
            }
        }
        setMinutesText(minute);
    }

    private void setHoursText(int hours) {
        String hoursString = (hours < 10 ? "0" : "") + hours;
        hoursText.setText(hoursString);
    }

    private void setMinutesText(int minutes) {
        String minutesString = (minutes < 10 ? "0" : "") + minutes;
        minutesText.setText(minutesString);
    }

    public long getTimeInMillis() {
        try {
            int hours = Integer.parseInt(hoursText.getText().toString());
            int minutes = Integer.parseInt(minutesText.getText().toString());
            return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setTimeInMillis(long millis) {
        hours = (int) TimeUnit.MILLISECONDS.toHours(millis);
        minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        setHoursText(hours);
        setMinutesText(minutes);
        setTextListener(hoursText);
        setTextListener(minutesText);
    }
}