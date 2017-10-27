package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.example.Node;

import java.util.Arrays;


public class Bus implements SourceOrSinkOrAnchor {

    private Node node;
    private SourceOrSink next;

    public Bus() { }
    public Bus(Node node) { this.node = node; }

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @Override public Bus getBus() { return this; }
    @Override public void setBus(Bus bus) { /* Ø */ }

    public String toString() { return "BUS" + this.node.toString(); }
}
