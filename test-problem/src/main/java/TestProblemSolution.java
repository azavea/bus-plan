package com.example;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class TestProblemSolution implements Serializable {

    private List<TestNode> nodes = null;
    private HardSoftScore score = null;

    public void display() {
	for (TestNode node : nodes) {
	    TestNode previous = node.getPrevious();
	    if (previous != null) {
		System.out.println(node + " ⇦ " + previous);
	    }
	    else {
		System.out.println(node);
	    }
	}
    }

    public TestProblemSolution() {
	// long buses = 2700;
	// long schools = 750;
	// long stops = 100000;
	long buses = 2;
	long schools = 7;
	long stops = 10;
	nodes = new ArrayList<TestNode>();

	for (long i = 0; i < buses; ++i) {
	    nodes.add(new TestBus(i, 50, 0, 0.0, 0.0));
	}
	for (long i = buses; i < buses + schools; ++i) {
	    nodes.add(new TestSchool(i, 0.0, 0.0));
	}
	for (long i = buses + schools; i < buses + schools + stops; ++i) {
	    nodes.add(new TestStop(i, (i % schools) + buses, 5, 0, 0.0, 0.0));
	}
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
