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
    public TextView nameNumberShootsText;
    public TextView positionText;
    public IconView arrowIcon;
    public IconView selectIcon;

    private boolean isSelected;
    private boolean isDisabled;
    private int selectColor;
    private int deselectColor;

    public PlayerHolder(View v, boolean showSelectIcon) {
        playerContainer = v.findViewById(R.id.playerContainer);
        disableOverlay = v.findViewById(R.id.disableOverlay);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameNumberShootsText = v.findViewById(R.id.nameNumberShootsText);
        positionText = v.findViewById(R.id.positionText);
        arrowIcon = v.findViewById(R.id.arrowIcon);
        selectIcon = v.findViewById(R.id.selectIcon);

        selectIcon.setVisibility(showSelectIcon ? View.VISIBLE : View.GONE);
        arrowIcon.setVisibility(showSelectIcon ? View.GONE : View.VISIBLE);

        selectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_player_selected);
        deselectColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_background_fourth);
        pictureImage.setBackgroundColor(deselectColor);
        arrowIcon.setTextColor(deselectColor);
        disableOverlay.setVisibility(View.GONE);
        pictureImage.setVisibility(View.VISIBLE);
        nameNumberShootsText.setVisibility(View.VISIBLE);
        positionText.setVisibility(View.VISIBLE);
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
