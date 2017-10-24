package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Arrays;


public class TestStop extends TestNode {

    private int[] kids;
    private long destination;

    public TestStop() {}

    public TestStop(long uuid, long destination, int type0Kids, int type1Kids, double x, double y) {
	this.uuid = uuid;
	this.destination = destination;
	this.kids = new int[2];
	this.kids[0] = type0Kids;
	this.kids[1] = type1Kids;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "X" + uuid;
    }

    public long getDestinationUUID() {
	return this.destination;
    }

    public int[] getKids() {
	return Arrays.copyOf(this.kids, this.kids.length);
    }
}
