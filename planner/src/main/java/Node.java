package com.azavea;

import java.util.HashMap;


/**
 * A class to represent a node (a location) in the road network
 * graph.
 *
 * @author James McClain
 */
public class Node {

    protected String uuid;
    private static HashMap<String, Integer> TIME_MATRIX;
    private static HashMap<String, Double> DISTANCE_MATRIX;

    /**
     * Set the time matrix used to calculate the time required to
     * travel between nodes.
     *
     * @author James McClain
     * @param  timeMatrix The time matrix given as a map between concatendated node names (strings) and times (integers)
     */
    public static void setTimeMatrix(HashMap<String, Integer> timeMatrix) {
        Node.TIME_MATRIX = timeMatrix;
    }

    /**
     * Set the distance matrix used to calculate the distance between
     * nodes.
     *
     * @author James McClain
     * @param  distanceMatrix  The distance matrix given as a map between concatendated node names (strings) and distances (integers)
     */
    public static void setDistanceMatrix(HashMap<String, Double> distanceMatrix) {
        Node.DISTANCE_MATRIX = distanceMatrix;
    }

    public Node(String uuid) { this.uuid = uuid; }

    public String getUuid() { return uuid; }

    /**
     * Return the amount of time required to travel between this node
     * and the other one.
     *
     * @author James McClain
     * @param  other  The other node
     */
    public int time(Node other) {
        String key = getUuid() + other.getUuid();
        if (Node.TIME_MATRIX.containsKey(key))
            return Node.TIME_MATRIX.get(key);
        else if (getUuid() == other.getUuid())
            return 0;
        else
            return Integer.MAX_VALUE;
    }

    /**
     * Return the distance between this node and the other one.
     *
     * @author James McClain
     * @param  other  The other node
     */
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
