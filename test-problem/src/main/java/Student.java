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
	this.node = node;
	if (rng.nextInt(50) > 0) {
	    int[] weights = {1,0};
	    this.node.setWeights(weights);
	}
	else {
	    int [] weights = {0, 1};
	    this.node.setWeights(weights);
	}
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
