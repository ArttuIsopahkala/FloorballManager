package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;

public class PlayerCard extends RelativeLayout {

    public interface PlayerCircleListener {
        void onContainerClick();
    }

    public enum ViewStyle {
        LINE,
        STATS,
        BOARD
    }

    private ImageView chemistryBorder;
    private ImageView pictureImage;
    private TextView nameText;
    private TextView statsText;
    private IconView addIcon;

    private PlayerCircleListener listener;

    public void setListener(PlayerCircleListener listener) {
        this.listener = listener;
    }

    public PlayerCard(Context context) {
        super(context);
        createView(context);
    }

    public PlayerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.container_card_player, this);
        pictureImage = findViewById(R.id.pictureImage);
        chemistryBorder = findViewById(R.id.chemistryBorder);
        nameText = findViewById(R.id.nameText);
        statsText = findViewById(R.id.statsText);
        addIcon = findViewById(R.id.addIcon);
        addIcon.setClickable(false);
        addIcon.setFocusable(false);

        RelativeLayout container = findViewById(R.id.container);
        container.setOnClickListener(v1 -> {
            if (listener != null) {
                listener.onContainerClick();
            }
        });
    }

    public void setViewStyle(ViewStyle style) {
        if(style == ViewStyle.STATS) {
            setLayoutParams(new TableLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 0.333f));
            setGravity(Gravity.CENTER);
            nameText.setTypeface(Typeface.DEFAULT_BOLD);
            nameText.setBackgroundColor(Color.TRANSPARENT);
            nameText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light));
            statsText.setVisibility(VISIBLE);
            chemistryBorder.setVisibility(GONE);
            LayoutParams params = new LayoutParams(Helper.dpToPx(60), Helper.dpToPx(60));
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            pictureImage.setLayoutParams(params);
        } else if (style == ViewStyle.LINE) {
            nameText.setTypeface(Typeface.DEFAULT);
            nameText.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_text));
            nameText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_black));
            statsText.setVisibility(GONE);
            chemistryBorder.setVisibility(VISIBLE);
            LayoutParams params = new LayoutParams(Helper.dpToPx(70), Helper.dpToPx(70));
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            pictureImage.setLayoutParams(params);

        }
    }

    public void setChemistryBorderColor(int color) {
        //NOTE: Drawable must be set as 'background' in xml to this take effect
        chemistryBorder.setImageResource(R.drawable.circle);
        chemistryBorder.setColorFilter(color);
    }

    public void setName(String name) {
        nameText.setText(name);
    }

    public void setPicture(Bitmap picture, boolean showPlus) {
        if(showPlus) {
            addIcon.setVisibility(VISIBLE);
            pictureImage.setVisibility(GONE);
            chemistryBorder.setVisibility(GONE);
        } else {
            addIcon.setVisibility(GONE);
            pictureImage.setVisibility(VISIBLE);
            chemistryBorder.setVisibility(VISIBLE);
            if(picture == null) {
                pictureImage.setImageResource(R.drawable.default_picture);
            } else {
                pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(picture));
            }
        }
    }

    public void setStats(String stats) {
        statsText.setText(stats);
    }
}
