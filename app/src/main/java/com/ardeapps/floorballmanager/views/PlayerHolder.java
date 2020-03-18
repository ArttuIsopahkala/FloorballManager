package com.ardeapps.floorballmanager.views;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;

public class PlayerHolder {

    public RelativeLayout playerContainer;
    public ImageView pictureImage;
    public TextView nameNumberShootsText;
    public TextView positionText;
    private IconView arrowIcon;
    private RelativeLayout disableOverlay;

    private boolean isSelected;
    private boolean isDisabled;
    private int selectColor;
    private int deselectColor;

    public PlayerHolder(View v, boolean showSelectIcon, boolean showPosition) {
        playerContainer = v.findViewById(R.id.playerContainer);
        disableOverlay = v.findViewById(R.id.disableOverlay);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameNumberShootsText = v.findViewById(R.id.nameNumberShootsText);
        positionText = v.findViewById(R.id.positionText);
        arrowIcon = v.findViewById(R.id.arrowIcon);
        IconView selectIcon = v.findViewById(R.id.selectIcon);

        selectIcon.setVisibility(showSelectIcon ? View.VISIBLE : View.GONE);
        arrowIcon.setVisibility(showSelectIcon ? View.GONE : View.VISIBLE);
        positionText.setVisibility(showPosition ? View.VISIBLE : View.GONE);

        selectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_player_selected);
        deselectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_background_fourth);
        pictureImage.setBackgroundColor(deselectColor);
        arrowIcon.setTextColor(deselectColor);
        disableOverlay.setVisibility(View.GONE);
        pictureImage.setVisibility(View.VISIBLE);
        nameNumberShootsText.setVisibility(View.VISIBLE);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            pictureImage.setBackgroundColor(selectColor);
            arrowIcon.setTextColor(selectColor);
        } else {
            pictureImage.setBackgroundColor(deselectColor);
            arrowIcon.setTextColor(deselectColor);
        }
    }

    boolean isDisabled() {
        return isDisabled;
    }

    void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        if (disabled) {
            disableOverlay.setVisibility(View.VISIBLE);
        } else {
            disableOverlay.setVisibility(View.GONE);
        }
    }
}
