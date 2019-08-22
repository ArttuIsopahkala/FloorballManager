package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class TimeEditText extends AppCompatEditText {

    private KeyImeChange keyImeChangeListener;

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeChangeListener(KeyImeChange listener) {
        keyImeChangeListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyImeChangeListener != null) {
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }

    public interface KeyImeChange {
        void onKeyIme(int keyCode, KeyEvent event);
    }
}