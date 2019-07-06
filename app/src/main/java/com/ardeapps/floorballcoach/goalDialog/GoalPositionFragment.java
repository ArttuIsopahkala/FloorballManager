package com.ardeapps.floorballcoach.goalDialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalPositionFragmentData;

public class GoalPositionFragment extends Fragment implements DataView {

    ImageView shootmapImage;
    ImageView shootPointImage;
    TextView awayNameText;
    TextView homeNameText;

    private double positionPercentX;
    private double positionPercentY;

    private double imageWidth;
    private double imageHeight;
    private Double positionX;
    private Double positionY;
    private String opponentName;

    GoalPositionFragmentData data;

    @Override
    public void setData(Object viewData) {
        data = (GoalPositionFragmentData) viewData;
    }

    @Override
    public GoalPositionFragmentData getData() {
        return data;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_position, container, false);

        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootPointImage = v.findViewById(R.id.shootPointImage);
        awayNameText = v.findViewById(R.id.awayNameText);
        homeNameText = v.findViewById(R.id.homeNameText);

        shootPointImage.setVisibility(View.GONE);
        homeNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
        awayNameText.setText(data.getOpponentName());

        ViewTreeObserver vto = shootmapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootmapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootmapImage.getMeasuredHeight();
                imageWidth = shootmapImage.getMeasuredWidth();

                if(data.getPositionPercentX() != null && data.getPositionPercentY() != null) {
                    shootPointImage.setVisibility(View.VISIBLE);
                    positionX = getPositionX(data.getPositionPercentX());
                    positionY = getPositionY(data.getPositionPercentY());

                    drawShootPoint(positionX, positionY);
                }

                return true;
            }
        });

        shootmapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    positionX = (double) event.getX();
                    positionY = (double) event.getY();
                    data.setPositionPercentX(getPositionPercentX());
                    data.setPositionPercentY(getPositionPercentY());

                    drawShootPoint(positionX, positionY);
                }
                return true;
            }
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

        int shootPointWidth = 40;
        int shootPointHeight = 40;
        double pictureX = positionX - (shootPointWidth / 2.0);
        double pictureY = positionY - (shootPointHeight / 2.0);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) shootPointImage.getLayoutParams();
        params.width = shootPointWidth;
        params.height = shootPointHeight;
        params.leftMargin = (int)pictureX;
        params.topMargin = (int)pictureY;
        shootPointImage.setLayoutParams(params);
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * positionPercentY;
    }


    private double getPositionPercentX() {
        return positionX / imageWidth;
    }

    private double getPositionPercentY() {
        return positionY / imageHeight;
    }

}
