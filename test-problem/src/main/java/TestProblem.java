package com.example;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class TestProblem implements Serializable {

    private List<TestNode> nodes = null;
    private List<TestBus> buslist = null;
    private HardSoftScore score = null;

    public void display() {
	for (TestNode node : nodes) {
	    TestNode next = node.getNext();
	    if (next != null) {
		System.out.println(node + " --> " + next + "\t" + node.distanceTo(next));
	    }
	    else {
		System.out.println(node);
	    }
	}
    }

    public TestProblem() {
	Random r = new Random(42);
	// long buses = 2700;
	// long schools = 750;
	// long stops = 10000;
	long buses = 7;
	long schools = 7;
	long stops = 10;
	nodes = new ArrayList<TestNode>();
	buslist = new ArrayList<TestBus>();

	for (long i = 0; i < buses; ++i) {
	    TestBus bus = new TestBus(i, 50, 0, r.nextDouble(), r.nextDouble());
	    nodes.add(bus);
	    buslist.add(bus);
	}
	for (int j = 0; j < buses; ++j) {
	    for (long i = buses; i < buses + schools; ++i) {
		nodes.add(new TestSchool(i, r.nextDouble(), r.nextDouble()));
	    }
	}
	for (long i = buses + schools; i < buses + schools + stops; ++i) {
	    nodes.add(new TestStop(i, (i % schools) + buses, 5, 0, r.nextDouble(), r.nextDouble()));
	}

	buslist.get(0).setNext(buslist.get(0)); // Trivial chain
    }

    public List<TestBus> getBusList() {
	return buslist;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "nodeRange")
    public List<TestNode> getNodeList() {
	return nodes;
    }

    @PlanningScore
    public HardSoftScore getScore() {
	return score;
    }

    public void setScore(HardSoftScore score) {
	this.score = score;
    }

}
