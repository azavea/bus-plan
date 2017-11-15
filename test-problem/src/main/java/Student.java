package com.example;

import java.util.Arrays;
import java.util.Random;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.example.Node;
import com.example.SourceOrSink;
import com.example.Stop;


@PlanningEntity
public class Student {

    private int[] weights;
    private Node node = null;
    private School school = null;
    private Stop stop = null;

    private static Random rng = new Random(33);

    public Student() {}

    public Student(Node node, School school) {
	this.node = node;
	if (rng.nextInt(50) > 0) {
	    int[] weights = {1,0};
	    this.setWeights(weights);
	}
	else {
	    int [] weights = {0, 1};
	    this.setWeights(weights);
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

    public int[] getWeights() { return Arrays.copyOf(this.weights, this.weights.length); }
    public void setWeights(int[] weights) { this.weights = Arrays.copyOf(weights, weights.length); }

    public int time(SourceOrSink other) {
	return this.getNode().time(other.getNode());
    }

    public int time(Student other) {
	return this.getNode().time(other.getNode());
    }

    public int time(Node other) {
	return this.getNode().time(other);
    }

    public double distance(SourceOrSink other) {
	return this.getNode().distance(other.getNode());
    }

    public double distance(Student other) {
	return this.getNode().distance(other.getNode());
    }

    public double distance(Node other) {
	return this.getNode().distance(other);
    }

    public String toString() {
	return "SOURCE[" + this.node.toString() + "]";
    }
}
