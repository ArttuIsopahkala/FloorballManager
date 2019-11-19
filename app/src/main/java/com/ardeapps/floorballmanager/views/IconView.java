package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;

import static android.view.Gravity.CENTER;

/**
 * Created by Arttu on 13.1.2019.
 */

public class IconView extends AppCompatTextView {

    Context context;

    public IconView(Context context) {
        super(context);
        init(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        // Load attributes
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconView, 0, 0);
        try {
            int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
            String fontInAssets = ta.getString(R.styleable.IconView_iconFont);
            int colorInAssets = ta.getColor(R.styleable.IconView_iconColor, ContextCompat.getColor(context, R.color.color_text_light));
            int sizeInAssets = ta.getDimensionPixelSize(R.styleable.IconView_iconSize, defaultSize);
            setGravity(CENTER);
            setTextColor(colorInAssets);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInAssets);
            if(fontInAssets == null) {
                fontInAssets = context.getResources().getString(R.string.icon_solid);
            }
            setTypeface(Typefaces.get(context, fontInAssets));
            setClickable(true);
            //setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        } finally {
            ta.recycle();
        }
    }

    public void setFont(String font) {
        setTypeface(Typefaces.get(context, font));
    }

    public void setSize(int size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setColor(int color) {
        setTextColor(color);
    }

    boolean isSelected = false;
    public void setSelected(boolean selected) {
        isSelected = selected;
        if(selected) {
            setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));
        } else {
            setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        }
    }

    public boolean isSelected() {
        return isSelected;
    }
}