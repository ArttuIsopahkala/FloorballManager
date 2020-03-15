package com.ardeapps.floorballmanager.tacticBoard.objects;

import java.util.ArrayList;

public class ExportItem {
    public int paramSize;
    public MovableView.Type type;
    public String id;
    public ArrayList<Position> positions = new ArrayList<>();

    public ExportItem(MovableView item) {
        this.paramSize = item.paramSize;
        this.type = item.type;
        this.id = item.id;
        this.positions = item.positions;
    }

    public ExportItem() {}

}
