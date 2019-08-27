package com.ardeapps.floorballmanager.objects;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ChemistryConnection {
    C_LW,
    C_RW,
    C_LD,
    C_RD,
    LD_RD,
    LD_LW,
    RD_RW;

    public static Position getComparePosition(ChemistryConnection connection, Position position) {
        Map<ChemistryConnection, Pair<Player.Position, Player.Position>> positionsInChemistryConnectionsAsPairs = getPositionsInChemistryConnectionsAsPairs();
        Pair<Position, Position> positions = positionsInChemistryConnectionsAsPairs.get(connection);
        if(positions != null) {
            if(positions.first == position) {
                return positions.second;
            }
            if(positions.second == position) {
                return positions.first;
            }
        }
        return null;
    }

    public static Map<ChemistryConnection, Pair<Player.Position, Player.Position>> getPositionsInChemistryConnectionsAsPairs() {
        Map<ChemistryConnection, Pair<Player.Position, Player.Position>> positionsInChemistryConnectionsAsPairs = new HashMap<>();
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.C_LW, new Pair<>(Position.C, Position.LW));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.C_RW, new Pair<>(Position.C, Position.RW));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.C_LD, new Pair<>(Position.C, Position.LD));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.C_RD, new Pair<>(Position.C, Position.RD));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.LD_RD, new Pair<>(Position.LD, Position.RD));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.LD_LW, new Pair<>(Position.LD, Position.LW));
        positionsInChemistryConnectionsAsPairs.put(ChemistryConnection.RD_RW, new Pair<>(Position.RD, Position.RW));
        return positionsInChemistryConnectionsAsPairs;
    }

    public static Map<ChemistryConnection, ArrayList<Position>> getPositionsInChemistryConnectionsAsList() {
        Map<ChemistryConnection, ArrayList<Position>> positionsInChemistryConnectionsAsList = new HashMap<>();
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.C_LW, new ArrayList<>(Arrays.asList(Position.C, Position.LW)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.C_RW, new ArrayList<>(Arrays.asList(Position.C, Position.RW)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.C_LD, new ArrayList<>(Arrays.asList(Position.C, Position.LD)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.C_RD, new ArrayList<>(Arrays.asList(Position.C, Position.RD)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.LD_RD, new ArrayList<>(Arrays.asList(Position.LD, Position.RD)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.LD_LW, new ArrayList<>(Arrays.asList(Position.LD, Position.LW)));
        positionsInChemistryConnectionsAsList.put(ChemistryConnection.RD_RW, new ArrayList<>(Arrays.asList(Position.RD, Player.Position.RW)));
        return positionsInChemistryConnectionsAsList;
    }

    public static ChemistryConnection getChemistryConnection(Position position1, Position position2) {
        for (Map.Entry<ChemistryConnection, ArrayList<Position>> entry : ChemistryConnection.getPositionsInChemistryConnectionsAsList().entrySet()) {
            ChemistryConnection connection = entry.getKey();
            ArrayList<Position> positions = entry.getValue();
            if(positions.contains(position1) && positions.contains(position2)) {
                return connection;
            }
        }
        return null;
    }

    public static ArrayList<ChemistryConnection> getClosestChemistryConnections(Position position) {
        Map<Position, ArrayList<ChemistryConnection>> closestConnectionsInPosition = new HashMap<>();
        closestConnectionsInPosition.put(Position.LW, new ArrayList<>(Arrays.asList(ChemistryConnection.C_LW, ChemistryConnection.LD_LW)));
        closestConnectionsInPosition.put(Position.C, new ArrayList<>(Arrays.asList(ChemistryConnection.C_LW, ChemistryConnection.C_RW, ChemistryConnection.C_LD, ChemistryConnection.C_RD)));
        closestConnectionsInPosition.put(Position.RW, new ArrayList<>(Arrays.asList(ChemistryConnection.C_RW, ChemistryConnection.RD_RW)));
        closestConnectionsInPosition.put(Position.LD, new ArrayList<>(Arrays.asList(ChemistryConnection.C_LD, ChemistryConnection.LD_RD, ChemistryConnection.LD_LW)));
        closestConnectionsInPosition.put(Position.RD, new ArrayList<>(Arrays.asList(ChemistryConnection.C_RD, ChemistryConnection.LD_RD, ChemistryConnection.RD_RW)));
        return closestConnectionsInPosition.get(position);
    }
}
