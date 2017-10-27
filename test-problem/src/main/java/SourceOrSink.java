package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import com.example.Node;
import com.example.Bus;


@PlanningEntity
public abstract class SourceOrSink {

    protected Bus bus;
    protected Node node;
    protected SourceOrSink next;

    @PlanningVariable(valueRangeProviderRefs = {"entityRange"},
		      graphType = PlanningVariableGraphType.CHAINED)
    public SourceOrSink getNext() { return this.next; }
    public void setNext(SourceOrSink next) { this.next = next; }

    public Node getNode() {return this.node; }
    public void setNode(Node node) { this.node = node; }

    @AnchorShadowVariable(sourceVariableName = "next")
    public Bus getBus() { return this.bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public double surfaceAndHighwayDistance(SourceOrSink other) {
	return this.getNode().surfaceAndHighwayDistance(other.getNode());
    }

    public double surfaceDistance(SourceOrSink other) {
	return this.getNode().surfaceDistance(other.getNode());
    }

    public String toString() { return "SOURCE.or.SINK" + node.toString(); }
}
