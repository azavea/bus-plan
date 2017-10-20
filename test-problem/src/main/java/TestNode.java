package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public abstract class TestNode {

    protected double x, y;
    protected long uuid;
    protected TestNode previous = null;

    public double getX() { return x; }
    public double getY() { return y; }
    public long uuid() { return uuid; }

    @PlanningVariable(valueRangeProviderRefs = {"nodeRange"},
    		      graphType = PlanningVariableGraphType.CHAINED)
    public TestNode getPrevious() { return previous; }

    public void setPrevious(TestNode previous) { this.previous = previous; }

}
