package com.ardeapps.floorballmanager.tacticBoard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class MovableView extends View {
    public View view;
    public int paramSize;
    public Type type;
    public String id;
    public ArrayList<Position> positions = new ArrayList<>();

    public MovableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovableView(Context context) {
        super(context);
    }

    public enum Type {
        BALL,
        PLAYER;

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }
}
