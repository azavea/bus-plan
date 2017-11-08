package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class School extends SourceOrSink {

    public School() {}

    public School(Node node) { this.node = node; }

    public boolean equals(Object other) {
	if (!(other instanceof School))
	    return false;
	else
	    return this.getNode().equals(((School)other).getNode());
    }

    public String toString() {
	return "SINK" + this.node.toString();
    }
}
