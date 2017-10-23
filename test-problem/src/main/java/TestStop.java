package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class TestStop extends TestNode {

    private int totalKids, differentlyAbledKids;
    private long destination;

    public TestStop() {}

    public TestStop(long uuid, long destination, int totalKids, int differentlyAbledKids, double x, double y) {
	this.uuid = uuid;
	this.destination = destination;
	this.totalKids = totalKids;
	this.differentlyAbledKids = differentlyAbledKids;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "X" + uuid;
    }

    public long getDestinationUUID() {
	return this.destination;
    }

    public int getKids() {
	return this.totalKids;
    }
}
