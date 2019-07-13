package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class LinesFragmentData {

    private Map<Integer, Line> lines;

    public LinesFragmentData() {
    }

    public Map<Integer, Line> getLines() {
        if(lines == null) {
            this.lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

}
