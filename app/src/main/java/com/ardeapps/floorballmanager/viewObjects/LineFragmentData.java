package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Line;

public class LineFragmentData {

    private Line line;
    private int lineNumber;
    private int fieldHeight;

    public LineFragmentData() {
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }
}
