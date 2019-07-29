package com.ardeapps.floorballcoach.viewObjects;

import com.ardeapps.floorballcoach.objects.Line;

public class LineFragmentData {

    private Line line;
    private int lineNumber;
    private boolean showChemistry;

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

    public boolean isShowChemistry() {
        return showChemistry;
    }

    public void setShowChemistry(boolean showChemistry) {
        this.showChemistry = showChemistry;
    }
}
