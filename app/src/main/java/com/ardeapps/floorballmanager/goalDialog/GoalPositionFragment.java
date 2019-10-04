package com.ardeapps.floorballmanager.goalDialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalPositionFragmentData;

public class GoalPositionFragment extends Fragment implements DataView {

    ImageView shootmapImage;
    ImageView shootPointImage;
    TextView awayNameText;
    TextView homeNameText;
    GoalPositionFragmentData data;
    private double imageWidth;
    private double imageHeight;
    private Double positionX;
    private Double positionY;

    @Override
    public GoalPositionFragmentData getData() {
        data.setPositionPercentX(getPositionPercentX());
        data.setPositionPercentY(getPositionPercentY());
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalPositionFragmentData) viewData;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_position, container, false);

        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootPointImage = v.findViewById(R.id.shootPointImage);
        awayNameText = v.findViewById(R.id.awayNameText);
        homeNameText = v.findViewById(R.id.homeNameText);

        shootPointImage.setVisibility(View.GONE);
        if (data.isOpponentGoal()) {
            homeNameText.setText(data.getOpponentName());
            awayNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        } else {
            homeNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
            awayNameText.setText(data.getOpponentName());
        }

        ViewTreeObserver vto = shootmapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootmapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootmapImage.getMeasuredHeight();
                imageWidth = shootmapImage.getMeasuredWidth();

                if (data.getPositionPercentX() != null && data.getPositionPercentY() != null) {
                    shootPointImage.setVisibility(View.VISIBLE);
                    positionX = getPositionX(data.getPositionPercentX());
                    positionY = getPositionY(data.getPositionPercentY());
                    drawShootPoint(positionX, positionY);
                }

                return true;
            }
        });

        shootmapImage.setOnTouchListener((v1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                positionX = (double) event.getX();
                positionY = (double) event.getY();
                drawShootPoint(positionX, positionY);
            }
            return true;
        });

        return v;
    }

    public void drawShootPoint(double positionX, double positionY) {
        shootPointImage.setVisibility(View.VISIBLE);

        int strokeWidth = 5;
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(Color.WHITE);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, Color.BLACK);
        shootPointImage.setBackground(gD);

        int shootPointWidth = Helper.dpToPx(30);
        int shootPointHeight = Helper.dpToPx(30);
        double pictureX = positionX - (shootPointWidth / 2.0);
        double pictureY = positionY - (shootPointHeight / 2.0);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) shootPointImage.getLayoutParams();
        params.width = shootPointWidth;
        params.height = shootPointHeight;
        params.leftMargin = (int) pictureX;
        params.topMargin = (int) pictureY;
        shootPointImage.setLayoutParams(params);
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * positionPercentY;
    }


    private Double getPositionPercentX() {
        if (positionX == null) {
            return null;
        }
        return positionX / imageWidth;
    }

    private Double getPositionPercentY() {
        if (positionY == null) {
            return null;
        }
        return positionY / imageHeight;
    }

}
