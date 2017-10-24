package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public abstract class TestNode {

    protected double x, y;
    protected long uuid;
    protected TestNode next = null;

    public long getUUID() { return uuid; }

    @PlanningVariable(valueRangeProviderRefs = {"nodeRange"},
		      graphType = PlanningVariableGraphType.CHAINED)
    public TestNode getNext() { return next; }

    public void setNext(TestNode next) { this.next = next; }

    public double distanceTo(TestNode other) {
	if (other == null)
	    return Double.POSITIVE_INFINITY;
	else {
	    double dx = this.x - other.x;
	    double dy = this.y - other.y;
	    return Math.sqrt(dx*dx + dy*dy);
	}
    }
}
