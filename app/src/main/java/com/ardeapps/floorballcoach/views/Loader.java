package com.ardeapps.floorballcoach.views;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class Loader {

    private static RelativeLayout loaderContainer;
    private static ImageView loader_spinner;
    private static boolean loaderVisible = false;
    private static boolean showPermanent = false;

    public static void create(RelativeLayout loaderView, ImageView loader_spinnerView) {
        loaderContainer = loaderView;
        loader_spinner = loader_spinnerView;
    }

    public static void showPermanent() {
        showPermanent = true;
        showAnimation();
    }

    public static void hidePermanent() {
        showPermanent = false;
        hideAnimation();
    }

    private static void showAnimation() {
        loaderVisible = true;
        Animation rotation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotation.setDuration(5000);
        rotation.setRepeatCount(Animation.INFINITE);

        loader_spinner.animate()
                .alpha(1)
                .setDuration(100);
        loaderContainer.setVisibility(View.VISIBLE);
        loader_spinner.setVisibility(View.VISIBLE);
        loader_spinner.startAnimation(rotation);
        loaderContainer.setOnClickListener(v -> {
            // Do nothing
        });
    }

    private static void hideAnimation() {
        loaderVisible = false;
        loaderContainer.setVisibility(View.INVISIBLE);
        loader_spinner.setVisibility(View.INVISIBLE);
        loader_spinner.clearAnimation();
    }

    public static void show() {
        if(!showPermanent) {
            showAnimation();
        }
    }

    public static void hide() {
        if(!showPermanent) {
            hideAnimation();
        }
    }

    public static boolean isVisible() {
        return loaderVisible;
    }
}
