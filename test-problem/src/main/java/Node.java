package com.example;

import java.util.HashMap;


public class Node {

    protected String uuid;
    private static HashMap<String, Integer> timeMatrix;
    private static HashMap<String, Double> distanceMatrix;

    public static void setTimeMatrix(HashMap<String, Integer> timeMatrix) {
        Node.timeMatrix = timeMatrix;
    }

    public static void setDistanceMatrix(HashMap<String, Double> distanceMatrix) {
        Node.distanceMatrix = distanceMatrix;
    }

    public Node(String uuid) { this.uuid = uuid; }

    public String getUuid() { return uuid; }

    public int time(Node other) {
        String key = getUuid() + other.getUuid();
        if (timeMatrix.containsKey(key))
            return timeMatrix.get(key);
        else if (getUuid() == other.getUuid())
            return 0;
        else
            return Integer.MAX_VALUE;
    }

    public double distance(Node other) {
        String key = getUuid() + other.getUuid();
        if (distanceMatrix.containsKey(key))
            return distanceMatrix.get(key);
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
