package com.azavea;

import java.util.HashMap;


public class Node {

    protected String uuid;
    private static HashMap<String, Integer> TIME_MATRIX;
    private static HashMap<String, Double> DISTANCE_MATRIX;

    public static void setTimeMatrix(HashMap<String, Integer> timeMatrix) {
        Node.TIME_MATRIX = timeMatrix;
    }

    public static void setDistanceMatrix(HashMap<String, Double> distanceMatrix) {
        Node.DISTANCE_MATRIX = distanceMatrix;
    }

    public Node(String uuid) { this.uuid = uuid; }

    public String getUuid() { return uuid; }

    public int time(Node other) {
        String key = getUuid() + other.getUuid();
        if (Node.TIME_MATRIX.containsKey(key))
            return Node.TIME_MATRIX.get(key);
        else if (getUuid() == other.getUuid())
            return 0;
        else
            return Integer.MAX_VALUE;
    }

    public double distance(Node other) {
        String key = getUuid() + other.getUuid();
        if (Node.DISTANCE_MATRIX.containsKey(key))
            return Node.DISTANCE_MATRIX.get(key);
        else if (getUuid() == other.getUuid())
            return 0;
        else
            return Double.MAX_VALUE;
    }

    public boolean equals(Object other ) {
        if (!(other instanceof Node))
            return false;
        else {
            String uuid1 = this.getUuid();
            String uuid2 = ((Node)other).getUuid();
            return uuid1.equals(uuid2);
        }
    }

    public String toString() { return uuid; }
}
