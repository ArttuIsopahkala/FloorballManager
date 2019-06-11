package com.ardeapps.floorballcoach.views;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;

public class PlayerHolder {

    public RelativeLayout playerContainer;
    public RelativeLayout disableOverlay;
    public ImageView pictureImage;
    public TextView nameNumberText;
    public TextView positionText;
    public TextView shootsText;
    public IconView statisticsIcon;
    public IconView editIcon;
    public IconView arrowIcon;

    public enum ViewType {
        MANAGE,
        SELECT,
    }

    private boolean isSelected;
    private boolean isDisabled;
    private int selectColor;
    private int deselectColor;

    public PlayerHolder(View v, ViewType type) {
        playerContainer = v.findViewById(R.id.playerContainer);
        disableOverlay = v.findViewById(R.id.disableOverlay);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameNumberText = v.findViewById(R.id.nameNumberText);
        positionText = v.findViewById(R.id.positionText);
        shootsText = v.findViewById(R.id.shootsText);
        statisticsIcon = v.findViewById(R.id.statisticsIcon);
        editIcon = v.findViewById(R.id.editIcon);
        arrowIcon = v.findViewById(R.id.arrowIcon);

        selectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_player_selected);
        deselectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_background_fourth);
        pictureImage.setBackgroundColor(deselectColor);
        arrowIcon.setTextColor(deselectColor);
        disableOverlay.setVisibility(View.GONE);

        switch (type) {
            case MANAGE:
                pictureImage.setVisibility(View.VISIBLE);
                nameNumberText.setVisibility(View.VISIBLE);
                positionText.setVisibility(View.VISIBLE);
                shootsText.setVisibility(View.VISIBLE);
                statisticsIcon.setVisibility(View.VISIBLE);
                editIcon.setVisibility(View.VISIBLE);
                arrowIcon.setVisibility(View.GONE);
                break;
            case SELECT:
                pictureImage.setVisibility(View.VISIBLE);
                nameNumberText.setVisibility(View.VISIBLE);
                positionText.setVisibility(View.VISIBLE);
                shootsText.setVisibility(View.GONE);
                statisticsIcon.setVisibility(View.GONE);
                editIcon.setVisibility(View.GONE);
                arrowIcon.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if(selected) {
            pictureImage.setBackgroundColor(selectColor);
            arrowIcon.setTextColor(selectColor);
        } else {
            pictureImage.setBackgroundColor(deselectColor);
            arrowIcon.setTextColor(deselectColor);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        if(disabled) {
            disableOverlay.setVisibility(View.VISIBLE);
        } else {
            disableOverlay.setVisibility(View.GONE);
        }
    }

    public boolean isDisabled() {
        return isDisabled;
    }
}
