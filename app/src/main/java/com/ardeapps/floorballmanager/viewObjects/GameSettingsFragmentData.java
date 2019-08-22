package com.ardeapps.floorballmanager.viewObjects;

import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Line;

import java.util.HashMap;
import java.util.Map;

public class GameSettingsFragmentData {

    private Game game;
    private Map<Integer, Line> lines;

    public GameSettingsFragmentData() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Map<Integer, Line> getLines() {
        if (lines == null) {
            this.lines = new HashMap<>();
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

}
