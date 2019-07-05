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

import com.ardeapps.floorballcoach.R;

public class GoalPositionFragment extends Fragment {

    ImageView shootmapImage;
    ImageView shootPointImage;

    private double positionPercentX;
    private double positionPercentY;
    private double imageWidth;
    private double imageHeight;
    private Double positionX;
    private Double positionY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_position, container, false);

        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootPointImage = v.findViewById(R.id.shootPointImage);

        shootPointImage.setVisibility(View.GONE);
        ViewTreeObserver vto = shootmapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootmapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootmapImage.getMeasuredHeight();
                imageWidth = shootmapImage.getMeasuredWidth();

                if(positionPercentX > 0 && positionPercentY > 0) {
                    shootPointImage.setVisibility(View.VISIBLE);
                    positionX = getPositionX(positionPercentX);
                    positionY = getPositionY(positionPercentY);
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

    public void setPositionPercents(double positionPercentX, double positionPercentY) {
        this.positionPercentX = positionPercentX;
        this.positionPercentY = positionPercentY;
    }

    public Double getPositionPercentX() {
        if(positionX != null) {
            return positionX / imageWidth;
        } else {
            return null;
        }
    }

    public Double getPositionPercentY() {
        if(positionY != null) {
            return positionY / imageHeight;
        } else {
            return null;
        }
    }
}
