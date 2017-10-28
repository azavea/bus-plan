package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import com.example.Bus;
import com.example.Node;
import com.example.SourceOrSinkOrAnchor;


@PlanningEntity
public abstract class SourceOrSink implements SourceOrSinkOrAnchor {

    protected Bus bus;
    protected Node node;
    protected SourceOrSink next;
    protected SourceOrSinkOrAnchor previous;

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @PlanningVariable(valueRangeProviderRefs = {"busRange", "entityRange"},
		      graphType = PlanningVariableGraphType.CHAINED)
    public SourceOrSinkOrAnchor getPrevious() { return this.previous; }
    public void setPrevious(SourceOrSinkOrAnchor previous) { this.previous = previous; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @AnchorShadowVariable(sourceVariableName = "previous")
    @Override public Bus getBus() { return this.bus; }
    @Override public void setBus(Bus bus) { this.bus = bus; }

    public double surfaceAndHighwayDistance(SourceOrSink other) {
	return this.getNode().surfaceAndHighwayDistance(other.getNode());
    }

    public double surfaceDistance(SourceOrSink other) {
	return this.getNode().surfaceDistance(other.getNode());
    }

    public String toString() { return "SOURCE.or.SINK" + node.toString(); }
}
