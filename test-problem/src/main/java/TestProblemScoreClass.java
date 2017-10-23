package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.HashMap;


public class TestProblemScoreClass implements EasyScoreCalculator<TestProblem> {

    public static final int costPerBusFixed = 10000;
    public static final int costPerUnitDistance = 1000;
    public static final double bellTime = 1.1; // in units of distance

    @Override
    public HardSoftScore calculateScore(TestProblem solution) {
	return calculateScore(solution, false);
    }

    public HardSoftScore calculateScore(TestProblem solution, Boolean verbose) {
	int busCount = 0;
	int strandedKids = 0;
	double totalDistance = 0.0;
	double currentDistance = 0.0;
	TestNode start, current, next;
	HashMap<Long,Integer> state;

	// Initialize
	start = current = solution.getBusList().get(0);
	next = current.getNext();
	state = new HashMap();

	// Traverse chain
	for (int i = 0; i < 2; i = i + (current == start ? 1 : 0)) {

	    // Skip over useless visits to schools (allow clone schools to be sopped up)
	    while ((next instanceof TestSchool) && !state.containsKey(next.getUUID())) {
		next = next.getNext();
	    }

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
		TestSchool school = (TestSchool)current;
		long location = new Long(school.getUUID());

		if (currentDistance < bellTime)
		    state.remove(location);
	    }

	    // Account for distance (distance also used as proxy for time)
	    if (!(next instanceof TestBus)) // XXX account for return to garage
		currentDistance += current.distanceTo(next);

	    // Print information
	    if (verbose) {
		System.out.println(current + "\t" + next + "\t" + current.distanceTo(next) + "\t" + currentDistance + "\t" + totalDistance + "\t" + busCount);
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
