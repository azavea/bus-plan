package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Arrays;


public class TestBus extends TestNode {

    private int[] capacity;

    public TestBus() {}

    public TestBus(long uuid, int type0Kids, int type1Kids, double x, double y) {
	this.uuid = uuid;
	this.capacity = new int[2];
	this.capacity[0] = type0Kids;
	this.capacity[1] = type1Kids;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "B" + uuid;
    }

    public int[] getCapacity() {
	return Arrays.copyOf(this.capacity, this.capacity.length);
    }
}
