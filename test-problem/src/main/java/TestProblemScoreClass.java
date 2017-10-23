package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.HashMap;


public class TestProblemScoreClass implements EasyScoreCalculator<TestProblem> {

    public static final int costPerBusFixed = 10000;
    public static final int costPerUnitDistance = 1000;
    public static final double bellTime = 1.9; //3.0/24; // in units of distance

    @Override
    public HardSoftScore calculateScore(TestProblem solution) {
	int busCount = 0;
	int strandedKids = 0;
	double totalDistance = 0.0, currentDistance;
	TestNode start, current, next;
	HashMap<Long,Integer> state;

	// Initialize
	start = current = solution.getBusList().get(0);
	next = current.getNext();
	currentDistance = 0.0;
	state = new HashMap();

	// Traverse chain
	for (int i = 0; i < 2; i = i + (current == start ? 1 : 0)) {
	    if (current instanceof TestBus) { // Bus: Start of a new subchain
		totalDistance += currentDistance;
		currentDistance = 0.0;
		for (Integer kids : state.values()) {
		    strandedKids += kids.intValue();
		}
		state = new HashMap();
		if (!(next instanceof TestBus) && (i < 1)) busCount++; // non-trivial chains imply buses
	    }
	    else if (current instanceof TestStop) { // Stop: Add students
		TestStop stop = (TestStop)current;
		long destination = new Long(stop.getDestinationUUID());
		int kids = stop.getKids();

		if (state.containsKey(destination))
		    state.put(destination, new Integer(state.get(destination).intValue() + kids));
		else
		    state.put(destination, new Integer(kids));
	    }
	    else if (current instanceof TestSchool) { // School: Subtract students
		if (currentDistance < bellTime) {
		    TestSchool school = (TestSchool)current;
		    long location = new Long(school.getUUID());
		    state.remove(location);
		}
	    }

	    // Account for distance (time)
	    if (!(next instanceof TestBus)) {
		currentDistance += current.distanceTo(next);
	    }

	    // Advance
	    current = next;
	    next = current.getNext();
	}

	// Compute and return score
	int dollars = busCount*costPerBusFixed + (int)(totalDistance*costPerUnitDistance);
	return HardSoftScore.valueOf(-strandedKids, -dollars);
    }

}
