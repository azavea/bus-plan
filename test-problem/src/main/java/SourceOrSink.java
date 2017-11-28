package com.example;

import java.util.Arrays;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import com.example.Bus;
import com.example.Node;
import com.example.SourceOrSinkOrAnchor;
import com.example.Student;


@PlanningEntity
public abstract class SourceOrSink implements SourceOrSinkOrAnchor {

    protected Bus bus;
    protected int[] weights;
    protected Node node;
    protected SourceOrSink next;
    protected SourceOrSinkOrAnchor previous;

    @Override public SourceOrSink getNext() { return this.next; }
    @Override public void setNext(SourceOrSink next) { this.next = next; }

    @PlanningVariable(valueRangeProviderRefs = {"busRange", "stopRange", "schoolRange"},
                      graphType = PlanningVariableGraphType.CHAINED)
    public SourceOrSinkOrAnchor getPrevious() { return this.previous; }
    public void setPrevious(SourceOrSinkOrAnchor previous) { this.previous = previous; }

    @Override public Node getNode() { return this.node; };
    @Override public void setNode(Node node) { this.node = node; }

    @AnchorShadowVariable(sourceVariableName = "previous")
    @Override public Bus getBus() { return this.bus; }
    @Override public void setBus(Bus bus) { this.bus = bus; }

    public int[] getWeights() { return Arrays.copyOf(this.weights, this.weights.length); }
    public void setWeights(int[] weights) { this.weights = Arrays.copyOf(weights, weights.length); }

    public String toString() { return "SOURCE.or.SINK[" + node.toString() + "]"; }

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
