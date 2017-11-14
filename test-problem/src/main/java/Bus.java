package com.example;

import com.example.Node;

import java.util.Arrays;


public class Bus implements SourceOrSinkOrAnchor {

    private Node node;
    private SourceOrSink next;
    private int multiplicity = 0;

    public Bus() { }
    public Bus(Node node) {
	int[] weights = {50, 5};

	this.node = node;
	this.node.setWeights(weights);
    }

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @Override public Bus getBus() { return this; }
    @Override public void setBus(Bus bus) { /* Ø */ }

    public void setMultiplicity(int multiplicity) {
	this.multiplicity = multiplicity;
    }

    public int getMultiplicity() {
	return this.multiplicity;
    }

    public String toString() { return "BUS" + this.node.toString() + "(" + this.getMultiplicity() + ")"; }
}
