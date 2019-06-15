package com.ardeapps.floorballcoach.views;

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

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.StringUtils;

import java.util.concurrent.TimeUnit;

public class TimePicker extends LinearLayout {
    TimeEditText minutesText;
    TimeEditText secondsText;
    int minutes;
    int seconds;

    public TimePicker(Context context) {
        super(context);
        createView(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.time_picker, this);
        minutesText = findViewById(R.id.minutesText);
        secondsText = findViewById(R.id.secondsText);
        clearFocus();
    }

    private void setTextListeners() {
        // ON FOCUS CHANGE
        minutesText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatMinutesText();
                }
            }
        });
        secondsText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatSecondsText();
                }
            }
        });
        // ON TEXT CHANGE
        minutesText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String minutes = s.toString();
                if (!StringUtils.isEmptyString(minutes)) {
                    int value = Integer.parseInt(minutes);
                    if (count == 2 || value > 5) {
                        secondsText.setFocusableInTouchMode(true);
                        secondsText.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        secondsText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String seconds = s.toString();
                if (!StringUtils.isEmptyString(seconds)) {
                    int value = Integer.parseInt(seconds);
                    if (value > 59) {
                        secondsText.setText(String.valueOf(59));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        // IME
        minutesText.setKeyImeChangeListener(new TimeEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    formatMinutesText();
                    minutesText.clearFocus();
                    Helper.hideKeyBoard(minutesText);
                }
            }
        });
        secondsText.setKeyImeChangeListener(new TimeEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    formatSecondsText();
                    secondsText.clearFocus();
                    Helper.hideKeyBoard(secondsText);
                }
            }
        });
        // EDITOR
        minutesText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    formatMinutesText();
                    minutesText.clearFocus();
                    Helper.hideKeyBoard(minutesText);
                }
                return false;
            }
        });
        secondsText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    formatSecondsText();
                }
                return false;
            }
        });

    }

    private void formatMinutesText() {
        int newMinutes = minutes;
        String minutesString = minutesText.getText().toString();
        if (!StringUtils.isEmptyString(minutesString)) {
            newMinutes = Integer.parseInt(minutesString);
            if (newMinutes > 59) {
                newMinutes = minutes;
            }
        }
        setMinutesText(newMinutes);
    }

    private void formatSecondsText() {
        int newSeconds = seconds;
        String secondsString = secondsText.getText().toString();
        if (!StringUtils.isEmptyString(secondsString)) {
            newSeconds = Integer.parseInt(secondsString);
            if (newSeconds > 59) {
                newSeconds = seconds;
            }
        }
        setSecondsText(newSeconds);
    }

    private void setMinutesText(int minutes) {
        String minutesString = (minutes < 10 ? "0" : "") + minutes;
        minutesText.setText(minutesString);
    }

    private void setSecondsText(int seconds) {
        String secondsString = (seconds < 10 ? "0" : "") + seconds;
        secondsText.setText(secondsString);
    }

    public long getTimeInMillis() {
        try {
            int minutes = Integer.parseInt(minutesText.getText().toString());
            int seconds = Integer.parseInt(secondsText.getText().toString());
            return TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setTimeInMillis(long millis) {
        minutes = (int)TimeUnit.MILLISECONDS.toMinutes(millis);
        seconds = (int)(TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        setMinutesText(minutes);
        setSecondsText(seconds);
        setTextListeners();
    }
}