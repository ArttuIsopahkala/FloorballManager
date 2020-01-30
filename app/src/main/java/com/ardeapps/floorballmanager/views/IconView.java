package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

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
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconView, 0, 0);
            try {
                int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
                String fontInAssets = ta.getString(R.styleable.IconView_iconFont);
                int colorInAssets = ta.getColor(R.styleable.IconView_iconColor, ContextCompat.getColor(context, R.color.color_text_light));
                int sizeInAssets = ta.getDimensionPixelSize(R.styleable.IconView_iconSize, defaultSize);
                setGravity(CENTER);
                setTextColor(colorInAssets);
                setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInAssets);
                if (fontInAssets == null) {
                    fontInAssets = context.getResources().getString(R.string.icon_solid);
                }
                setTypeface(Typefaces.get(context, fontInAssets));
                // Override if set in XML
                boolean clickable = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "clickable", true);
                setClickable(clickable);
            } finally {
                ta.recycle();
            }
        }
    }
}