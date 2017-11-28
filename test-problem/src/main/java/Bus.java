package com.azavea;

import com.azavea.Node;

import java.util.Arrays;


public class Bus implements SourceOrSinkOrAnchor {

    private int[] weights;
    private Node node;
    private SourceOrSink next;
    private int multiplicity = 0;

    public Bus() { }

    public Bus(Node node) {
        int[] weights = {50, 5};
        this.setWeights(weights);
        this.node = node;
    }

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @Override public Bus getBus() { return this; }
    @Override public void setBus(Bus bus) { /* Ø */ }

    public int[] getWeights() { return Arrays.copyOf(this.weights, this.weights.length); }
    public void setWeights(int[] weights) { this.weights = Arrays.copyOf(weights, weights.length); }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public int getMultiplicity() {
        return this.multiplicity;
    }

    public boolean equals(Object other) {
        if (other instanceof String)
            return this.getNode().getUuid() == ((String)other);
        else if (!(other instanceof Bus))
            return false;
        else
            return this.getNode().getUuid() == ((Bus)other).getNode().getUuid();
    }

    public String toString() { return "ANCHOR[" + this.node.toString() + "(" + this.getMultiplicity() + ")]"; }

    public int time(SourceOrSinkOrAnchor other) {
        return this.getNode().time(other.getNode());
    }

    public int time(Node other) {
        return this.getNode().time(other);
    }

    public double distance(SourceOrSinkOrAnchor other) {
        return this.getNode().distance(other.getNode());
    }

    public double distance(Node other) {
        return this.getNode().distance(other);
    }
}
