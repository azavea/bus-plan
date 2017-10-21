package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class TestStop extends TestNode {

    private int total, wheel;
    private long dest;

    public TestStop() {}

    public TestStop(long uuid, long dest, int total, int wheel, double x, double y) {
	this.uuid = uuid;
	this.dest = dest;
	this.total = total;
	this.wheel = wheel;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "Stop " + uuid;
    }

}
