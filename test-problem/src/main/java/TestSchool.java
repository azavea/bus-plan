package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class TestSchool extends TestNode {

    public TestSchool() {}

    public TestSchool(long uuid, double x, double y) {
	this.uuid = uuid;
	this.x = x;
	this.y = y;
    }

}
