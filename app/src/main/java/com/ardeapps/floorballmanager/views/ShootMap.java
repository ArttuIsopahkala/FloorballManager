package com.ardeapps.floorballmanager.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.utils.Helper;

import java.util.ArrayList;

/**
 * Created by Arttu on 5.5.2019.
 */

public class ShootMap extends RelativeLayout {

    ImageView shootMapImage;
    RelativeLayout shootMapPointsContainer;

    private double imageWidth;
    private double imageHeight;
    Double positionX;
    Double positionY;

    public ShootMap(Context context) {
        super(context);
        createView(context);
    }

    public ShootMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.shoot_map, this);
        shootMapImage = findViewById(R.id.shootMapImage);
        shootMapPointsContainer = findViewById(R.id.shootMapPointsContainer);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initialize(boolean drawMode, CreateViewListener listener) {
        ViewTreeObserver vto = shootMapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootMapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootMapImage.getMeasuredHeight();
                imageWidth = shootMapImage.getMeasuredWidth();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)imageHeight);
                shootMapPointsContainer.setLayoutParams(params);

                if(drawMode) {
                    shootMapImage.setOnTouchListener((v1, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            shootMapPointsContainer.removeAllViewsInLayout();
                            positionX = (double) event.getX();
                            positionY = (double) event.getY();
                            drawPoint(positionX, positionY);
                        }
                        return true;
                    });
                }

                if(listener != null) {
                    listener.onViewCreated();
                }
                return true;
            }
        });
    }

    public void drawShootPoint(Double positionPercentX, Double positionPercentY) {
        shootMapPointsContainer.removeAllViewsInLayout();
        if (positionPercentX != null && positionPercentY != null) {
            positionX = getPositionX(positionPercentX);
            positionY = getPositionY(positionPercentY);
            if (positionY > imageHeight) {
                positionY = imageHeight;
            }
            drawPoint(positionX, positionY);
        }
    }

    public void drawShootPoints(ArrayList<Goal> goals) {
        shootMapPointsContainer.removeAllViewsInLayout();
        for (Goal goal : goals) {
            if (goal.getPositionPercentX() != null && goal.getPositionPercentY() != null) {
                double x = getPositionX(goal.getPositionPercentX());
                double y = getPositionY(goal.getPositionPercentY());
                if (y > imageHeight) {
                    y = imageHeight;
                }
                drawPoint(x, y);
            }
        }
    }

    private void drawPoint(double positionX, double positionY) {
        ImageView shootPoint = new ImageView(AppRes.getActivity());
        shootPoint.setScaleType(ImageView.ScaleType.FIT_XY);
        shootPoint.setAdjustViewBounds(true);

        int strokeWidth = Helper.dpToPx(3);
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(Color.WHITE);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, Color.BLACK);
        shootPoint.setBackground(gD);

        int shootPointWidth = Helper.dpToPx(20);
        int shootPointHeight = Helper.dpToPx(20);
        double pictureX = positionX - (shootPointWidth / 2.0);
        double pictureY = positionY - (shootPointHeight / 2.0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(shootPointWidth, shootPointHeight);
        params.leftMargin = (int) pictureX;
        params.topMargin = (int) pictureY;
        shootPoint.setLayoutParams(params);

        shootMapPointsContainer.addView(shootPoint);
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * positionPercentY;
    }

    public Double getPositionPercentX() {
        if (positionX == null) {
            return null;
        }
        return positionX / imageWidth;
    }

    public Double getPositionPercentY() {
        if (positionY == null) {
            return null;
        }
        return positionY / imageHeight;
    }
}
