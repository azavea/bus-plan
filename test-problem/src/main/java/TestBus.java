package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class TestBus extends TestNode {

    private int total, wheel;

    public TestBus() {}

    public TestBus(long uuid, int total, int wheel, double x, double y) {
	this.uuid = uuid;
	this.total = total;
	this.wheel = wheel;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "B" + uuid;
    }
}
