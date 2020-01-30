package com.ardeapps.floorballmanager.tacticBoard;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class TacticBoardHelper {

    public static String getType(String clipData) {
        return clipData.split("#")[0];
    }

    public static String getId(String clipData) {
        return clipData.split("#")[1];
    }

    public static String createTag(MovableView.Type type, String id) {
        return type.toDatabaseName() + "#" + id;
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

}
