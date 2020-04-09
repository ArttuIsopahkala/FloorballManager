package com.ardeapps.floorballmanager.tacticBoard.objects;

import java.util.ArrayList;

public class ExportItem {
    public int paramSize;
    public MovableView.Type type;
    public int index;
    public ArrayList<Position> positions = new ArrayList<>();

    public ExportItem(MovableView item) {
        this.paramSize = item.paramSize;
        this.type = item.type;
        this.index = item.index;
        this.positions = item.positions;
    }

    public ExportItem() {}

}
