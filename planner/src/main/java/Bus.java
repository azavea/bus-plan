package com.azavea;

import com.azavea.Node;

import java.util.Arrays;


public class Bus implements SourceOrSinkOrAnchor {

    private Node node;
    private SourceOrSink next;
    private int n;

    public Bus() { }

    public Bus(Node node, int n) {
        this.node = node;
        this.n = n;
    }

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @Override public Bus getBus() { return this; }
    @Override public void setBus(Bus bus) { /* Ø */ }

    @Override
    public int hashCode() {
        return this.getNode().toString().hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof String)
            return this.getNode().getUuid() == ((String)other);
        else if (!(other instanceof Bus))
            return false;
        else
            return this.getNode().getUuid() == ((Bus)other).getNode().getUuid();
    }

    public String toString() { return "ANCHOR[" + this.node.toString() + "(" + this.n + ")]"; }

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
