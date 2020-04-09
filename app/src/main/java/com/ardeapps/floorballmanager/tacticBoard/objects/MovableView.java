package com.ardeapps.floorballmanager.tacticBoard.objects;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;

public class MovableView extends View {
    public View view;
    public int paramSize;
    public Type type;
    public int index;
    public ArrayList<Position> positions = new ArrayList<>();

    public MovableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovableView(Context context) {
        super(context);
    }

    public enum Type {
        BALL,
        HOME_PLAYER,
        AWAY_PLAYER;

        public static Type fromDatabaseName(String value) {
            return Enum.valueOf(Type.class, value);
        }

        public String toDatabaseName() {
            return this.name();
        }
    }

    public Pair<Type, Integer> getPairId() {
        return new Pair<>(type, index);
    }

    public static Pair<Type, Integer> getPairId(Type type, int index) {
        return new Pair<>(type, index);
    }
}
