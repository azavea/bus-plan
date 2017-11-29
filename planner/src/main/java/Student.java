package com.azavea;

import java.util.Arrays;
import java.util.Random;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.azavea.Node;
import com.azavea.SourceOrSink;
import com.azavea.Stop;


@PlanningEntity
public class Student {

    private int[] weights;
    private Node node = null;
    private String schoolUuid = null;
    private Stop stop = null;
    private String firstName = null;
    private String lastName = null;
    private String studentUuid = null;

    private static Random rng = new Random(33);

    public Student() {}

    public Student(Node node, String studentUuid, String firstName, String lastName, String schoolUuid) {
        this.node = node;
	this.studentUuid = studentUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolUuid = schoolUuid;
        int[] weights = {1,0};
        this.setWeights(weights);
    }

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String name) { this.firstName = name; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String name) { this.lastName = name; }

    public Node getNode() { return this.node; }
    public void setNode(Node node) { this.node = node; }

    public String getSchoolUuid() { return this.schoolUuid; }
    public void setSchoolUuid(String schoolUuid) { this.schoolUuid = schoolUuid; }

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
        return "SOURCE[" +
	    this.studentUuid + ":" +
	    this.lastName + "," +
	    this.firstName + ":" +
	    this.node.toString() + "]";
    }
}
