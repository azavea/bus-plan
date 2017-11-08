package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Random;

import com.example.Stop;


@PlanningEntity
public class Student {

    private Node node = null;
    private School school = null;
    private Stop stop = null;

    private static Random rng = new Random(33);

    public Student() {}

    public Student(Node node, School school) {
	int[] weights = new int[2];

	this.node = node;
	if (rng.nextInt(10) > 0) {
	    weights[0] = 1;
	    weights[1] = 0;
	}
	else {
	    weights[0] = 0;
	    weights[1] = 1;
	}
	this.node.setWeights(weights);
	this.school = school;
    }

    public Node getNode() { return this.node; }
    public void setNode(Node node) { this.node = node; }

    public School getSchool() { return this.school; }
    public void setSchool(School school) { this.school = school; }

    @PlanningVariable(valueRangeProviderRefs = {"stopRange"})
    public Stop getStop() { return this.stop; }
    public void setStop(Stop stop) { this.stop = stop; }

    public String toString() {
	return "SOURCE" + this.node.toString();
    }
}
