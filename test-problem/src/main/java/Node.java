package com.example;

import java.util.Arrays;
import java.util.Random;


public class Node {

    protected double x, y;
    protected int[] weights;
    protected long uuid;
    private static Random rng = new Random(42);
    private static long serial = 0;

    public Node(int orderOfMagnitude1, int orderOfMagnitude2) {
	this.x = rng.nextDouble();
	this.y = rng.nextDouble();
	this.weights = new int[2];
	this.weights[0] = rng.nextInt(orderOfMagnitude1) + orderOfMagnitude1;
	this.weights[1] = rng.nextInt(orderOfMagnitude2) + orderOfMagnitude2;
	this.uuid = serial++;
    }

    public Node() {
	this.x = rng.nextDouble();
	this.y = rng.nextDouble();
	this.weights = new int[2];
	this.weights[0] = rng.nextInt(5);
	this.weights[1] = rng.nextInt(1);
	this.uuid = serial++;
    }

    public double getX() { return this.x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return this.y; }
    public void setY(double y) { this.y = y; }

    public int[] getWeights() { return Arrays.copyOf(this.weights, this.weights.length); }
    public void setWeights(int[] weights) { this.weights = Arrays.copyOf(weights, weights.length); }

    public long getUuid() { return uuid; }
    void setUuid(long uuid) { this.uuid = uuid; }

    public double surfaceAndHighwayDistance(Node other) { // XXX
	return surfaceDistance(other);
    }

    public double surfaceDistance(Node other) { // XXX
	if (other == null)
	    return Double.POSITIVE_INFINITY;
	else {
	    double dx = this.x - other.x;
	    double dy = this.y - other.y;
	    return Math.sqrt(dx*dx + dy*dy);
	}
    }

    public String toString() { return "" + uuid; }
}
