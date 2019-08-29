package com.ardeapps.floorballmanager.objects;

import android.util.Pair;

import com.ardeapps.floorballmanager.objects.Player.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Connection {
    C_LW,
    C_RW,
    C_LD,
    C_RD,
    LD_RD,
    LD_LW,
    RD_RW;

    public static Position getComparePosition(Position position, Connection connection) {
        Map<Connection, Pair<Player.Position, Player.Position>> positionsInChemistryConnectionsAsPairs = getPositionsInChemistryConnectionsAsPairs();
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

    public static Map<Connection, Pair<Player.Position, Player.Position>> getPositionsInChemistryConnectionsAsPairs() {
        Map<Connection, Pair<Player.Position, Player.Position>> positionsInChemistryConnectionsAsPairs = new HashMap<>();
        positionsInChemistryConnectionsAsPairs.put(Connection.C_LW, new Pair<>(Position.C, Position.LW));
        positionsInChemistryConnectionsAsPairs.put(Connection.C_RW, new Pair<>(Position.C, Position.RW));
        positionsInChemistryConnectionsAsPairs.put(Connection.C_LD, new Pair<>(Position.C, Position.LD));
        positionsInChemistryConnectionsAsPairs.put(Connection.C_RD, new Pair<>(Position.C, Position.RD));
        positionsInChemistryConnectionsAsPairs.put(Connection.LD_RD, new Pair<>(Position.LD, Position.RD));
        positionsInChemistryConnectionsAsPairs.put(Connection.LD_LW, new Pair<>(Position.LD, Position.LW));
        positionsInChemistryConnectionsAsPairs.put(Connection.RD_RW, new Pair<>(Position.RD, Position.RW));
        return positionsInChemistryConnectionsAsPairs;
    }

    public static Map<Connection, ArrayList<Position>> getPositionsInChemistryConnectionsAsList() {
        Map<Connection, ArrayList<Position>> positionsInChemistryConnectionsAsList = new HashMap<>();
        positionsInChemistryConnectionsAsList.put(Connection.C_LW, new ArrayList<>(Arrays.asList(Position.C, Position.LW)));
        positionsInChemistryConnectionsAsList.put(Connection.C_RW, new ArrayList<>(Arrays.asList(Position.C, Position.RW)));
        positionsInChemistryConnectionsAsList.put(Connection.C_LD, new ArrayList<>(Arrays.asList(Position.C, Position.LD)));
        positionsInChemistryConnectionsAsList.put(Connection.C_RD, new ArrayList<>(Arrays.asList(Position.C, Position.RD)));
        positionsInChemistryConnectionsAsList.put(Connection.LD_RD, new ArrayList<>(Arrays.asList(Position.LD, Position.RD)));
        positionsInChemistryConnectionsAsList.put(Connection.LD_LW, new ArrayList<>(Arrays.asList(Position.LD, Position.LW)));
        positionsInChemistryConnectionsAsList.put(Connection.RD_RW, new ArrayList<>(Arrays.asList(Position.RD, Player.Position.RW)));
        return positionsInChemistryConnectionsAsList;
    }

    public static Connection getChemistryConnection(Position position1, Position position2) {
        for (Map.Entry<Connection, ArrayList<Position>> entry : Connection.getPositionsInChemistryConnectionsAsList().entrySet()) {
            Connection connection = entry.getKey();
            ArrayList<Position> positions = entry.getValue();
            if(positions.contains(position1) && positions.contains(position2)) {
                return connection;
            }
        }
        return null;
    }

    public static ArrayList<Connection> getClosestChemistryConnections(Position position) {
        Map<Position, ArrayList<Connection>> closestConnectionsInPosition = new HashMap<>();
        closestConnectionsInPosition.put(Position.LW, new ArrayList<>(Arrays.asList(Connection.C_LW, Connection.LD_LW)));
        closestConnectionsInPosition.put(Position.C, new ArrayList<>(Arrays.asList(Connection.C_LW, Connection.C_RW, Connection.C_LD, Connection.C_RD)));
        closestConnectionsInPosition.put(Position.RW, new ArrayList<>(Arrays.asList(Connection.C_RW, Connection.RD_RW)));
        closestConnectionsInPosition.put(Position.LD, new ArrayList<>(Arrays.asList(Connection.C_LD, Connection.LD_RD, Connection.LD_LW)));
        closestConnectionsInPosition.put(Position.RD, new ArrayList<>(Arrays.asList(Connection.C_RD, Connection.LD_RD, Connection.RD_RW)));
        return closestConnectionsInPosition.get(position);
    }
}
