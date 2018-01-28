package com.azavea;

import java.util.HashMap;
import java.util.HashSet;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.azavea.Node;
import com.azavea.SourceOrSink;
import com.azavea.Stop;


/**
 * The student class.  These planning entites are associated with
 * stops by OptaPlanner.
 *
 * @author James McClain
 */
@PlanningEntity
public class Student {

    private Node node = null;
    private String schoolUuid = null;
    private Stop stop = null;
    private String firstName = null;
    private String lastName = null;
    private static HashMap<String, HashSet<String>> ELIGIBILITY_MATRIX = null;

    public static void setEligibilityMatrix(HashMap<String, HashSet<String>> eligibilityMatrix) {
	Student.ELIGIBILITY_MATRIX = eligibilityMatrix;
    }

    public Student() {}

    public Student(Node node, String firstName, String lastName, String schoolUuid) {
        this.node = node;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolUuid = schoolUuid;
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

    public boolean eligible(Stop other) {
	HashSet<String> set = Student.ELIGIBILITY_MATRIX.get(this.getNode().getUuid());
	assert (set != null);
	return set.contains(other.getNode().getUuid());
    }

    public String toString() {
        return "SOURCE[" +
	    this.lastName + "," +
	    this.firstName + ":" +
	    this.node.toString() + "]";
    }
}
