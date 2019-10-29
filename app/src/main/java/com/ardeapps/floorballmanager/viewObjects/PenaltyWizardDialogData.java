package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Penalty;

import java.util.HashMap;
import java.util.Map;

public class PenaltyWizardDialogData {

    private Penalty penalty;
    private Game game;
    private boolean isOpponentPenalty;
    private Map<Integer, Line> lines;

    public PenaltyWizardDialogData() {
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isOpponentPenalty() {
        return isOpponentPenalty;
    }

    public void setOpponentPenalty(boolean opponentPenalty) {
        isOpponentPenalty = opponentPenalty;
    }

    public Map<Integer, Line> getLines() {
        if(lines == null) {
            lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }
}
