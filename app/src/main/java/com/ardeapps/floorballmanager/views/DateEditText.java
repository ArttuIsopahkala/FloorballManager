package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by Arttu on 23.2.2018.
 */

public class DateEditText extends android.support.v7.widget.AppCompatEditText {

    private KeyImeChange keyImeChangeListener;

    public DateEditText(Context context, AttributeSet attrs) {
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
