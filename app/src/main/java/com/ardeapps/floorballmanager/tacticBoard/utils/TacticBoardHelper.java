package com.ardeapps.floorballmanager.tacticBoard.utils;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballmanager.tacticBoard.objects.MovableView;

public class TacticBoardHelper {

    public static String getType(String clipData) {
        return clipData.split("#")[0];
    }

    public static int getIndex(String clipData) {
        return Integer.valueOf(clipData.split("#")[1]);
    }

    public static String createTag(MovableView.Type type, int index) {
        return type.toDatabaseName() + "#" + index;
    }

    public static View findViewAtPosition(View parent, int x, int y) {
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View viewAtPosition = findViewAtPosition(child, x, y);
                if (viewAtPosition != null) {
                    return viewAtPosition;
                }
            }
            return null;
        } else {
            Rect rect = new Rect();
            parent.getGlobalVisibleRect(rect);
            if (rect.contains(x, y)) {
                return parent;
            } else {
                return null;
            }
        }
    }

    public static int getColorFromProgress(int progress) {
        int r = 0;
        int g = 0;
        int b = 0;

        if (progress < 256) {
            b = progress;
        } else if (progress < 256 * 2) {
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 3) {
            g = 255;
            b = progress % 256;
        } else if (progress < 256 * 4) {
            r = progress % 256;
            g = 256 - progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 5) {
            r = 255;
            g = 0;
            b = progress % 256;
        } else if (progress < 256 * 6) {
            r = 255;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 7) {
            r = 255;
            g = 255;
            b = progress % 256;
        }
        return Color.argb(255, r, g, b);
    }

}
