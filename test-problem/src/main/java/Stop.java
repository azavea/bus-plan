package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;


public class Stop extends SourceOrSink {

    private School destination;

    public Stop() {}

    public Stop(Node node, School destination) {
	this.node = node;
	this.destination = destination;
    }

    public School getDestination() { return this.destination; }
    public void setDestination(School destination) { this.destination = destination; }
    
    public String toString() {
	return "SOURCE" + this.node.toString();
    }
}
