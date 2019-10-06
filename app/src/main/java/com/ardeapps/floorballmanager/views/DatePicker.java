package com.ardeapps.floorballmanager.views;

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

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by Arttu on 23.2.2018.
 */

public class DatePicker extends LinearLayout {
    TimeEditText dayText;
    TimeEditText monthText;
    TimeEditText yearText;
    Calendar date;

    public DatePicker(Context context) {
        super(context);
        createView(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.date_picker, this);
        dayText = findViewById(R.id.dayText);
        monthText = findViewById(R.id.monthText);
        yearText = findViewById(R.id.yearText);
        clearFocus();
    }

    private void setTextListener(TimeEditText editText) {
        // ON FOCUS CHANGE
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(editText.getId() == dayText.getId()) {
                    formatDayText();
                } else if(editText.getId() == monthText.getId()) {
                    formatMonthText();
                } else if(editText.getId() == yearText.getId()) {
                    formatYearText();
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
                    if(editText.getId() == dayText.getId()) {
                        if (value < 1) {
                            setDayText(1);
                        }
                        if (value > 31) {
                            setDayText(31);
                        }
                    } else if(editText.getId() == monthText.getId()) {
                        if (value < 1) {
                            setMonthText(1);
                        }
                        if (value > 12) {
                            setMonthText(12);
                        }
                    } else if(editText.getId() == yearText.getId()) {
                        if(count == 4) {
                            if (value < 2010) {
                                setYearText(2010);
                            }
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            if (value > year + 1) {
                                setYearText(year + 1);
                            }
                        }
                    }

                    if (editText.getId() == yearText.getId()) {
                        if(count == 4) {
                            TextView nextField = (TextView)editText.focusSearch(View.FOCUS_RIGHT);
                            if(nextField != null) {
                                nextField.requestFocus();
                            }
                        }
                    } else {
                        if (count == 2) {
                            TextView nextField = (TextView) editText.focusSearch(View.FOCUS_RIGHT);
                            if(nextField != null) {
                                nextField.requestFocus();
                            }
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
                if(editText.getId() == dayText.getId()) {
                    formatDayText();
                } else if(editText.getId() == monthText.getId()) {
                    formatMonthText();
                } else if(editText.getId() == yearText.getId()) {
                    formatYearText();
                }

                editText.clearFocus();
                Helper.hideKeyBoard(editText);
            }
        });
        // EDITOR
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                if(editText.getId() == dayText.getId()) {
                    formatDayText();
                } else if(editText.getId() == monthText.getId()) {
                    formatMonthText();
                }
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(editText.getId() == yearText.getId()) {
                    formatYearText();
                    editText.clearFocus();
                    Helper.hideKeyBoard(editText);
                }
            }
            return false;
        });
    }

    private void formatDayText() {
        int day = date.get(Calendar.DAY_OF_MONTH);
        String dayString = dayText.getText().toString();
        if (!StringUtils.isEmptyString(dayString)) {
            day = Integer.parseInt(dayText.getText().toString());
            if (day < 1 || day > 31) {
                day = date.get(Calendar.DAY_OF_MONTH);
            }
        }
        setDayText(day);
    }

    private void formatMonthText() {
        int month = date.get(Calendar.MONTH) + 1;
        String monthString = monthText.getText().toString();
        if (!StringUtils.isEmptyString(monthString)) {
            month = Integer.parseInt(monthText.getText().toString());
            if (month < 1 || month > 12) {
                month = date.get(Calendar.MONTH) + 1;
            }
        }
        setMonthText(month);
    }

    public void formatYearText() {
        int year = date.get(Calendar.YEAR);
        String yearString = yearText.getText().toString();
        if (!StringUtils.isEmptyString(yearString)) {
            year = Integer.parseInt(yearText.getText().toString());
            if (year < 2010 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                year = date.get(Calendar.YEAR);
            }
        }
        setYearText(year);
    }

    private void setDayText(int day) {
        String dayString = (day < 10 ? "0" : "") + day;
        dayText.setText(dayString);
    }

    private void setMonthText(int month) {
        String monthString = (month < 10 ? "0" : "") + month;
        monthText.setText(monthString);
    }

    private void setYearText(int year) {
        yearText.setText(String.valueOf(year));
    }

    public Calendar getDate() {
        try {
            int day = Integer.parseInt(dayText.getText().toString());
            int month = Integer.parseInt(monthText.getText().toString());
            int year = Integer.parseInt(yearText.getText().toString());
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);

            cal.getTime();
            return cal;
        } catch (Exception e) {
            return null;
        }
    }

    public void setDate(Calendar date) {
        this.date = date;
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);
        setDayText(day);
        setMonthText(month);
        setYearText(year);
        setTextListener(dayText);
        setTextListener(monthText);
        setTextListener(yearText);
    }
}
