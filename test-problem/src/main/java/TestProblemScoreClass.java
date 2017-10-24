package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.HashMap;


public class TestProblemScoreClass implements EasyScoreCalculator<TestProblem> {

    public static final int costPerBusFixed = 10000;
    public static final int costPerUnitDistance = 1000;
    public static final double bellTime = 2.0; // in units of distance

    @Override
    public HardSoftScore calculateScore(TestProblem solution) {
	return calculateScore(solution, false);
    }

    public HardSoftScore calculateScore(TestProblem solution, Boolean verbose) {
	int busCount = 0;
	int strandedKids = 0;
	int[] busRiders = new int[] {0, 0};
	int[] busCapacity = new int[] {0, 0};
	double totalDistance = 0.0;
	double currentDistance = 0.0;
	double highWaterCapacityRatio = 0.0;
	TestNode start, current, next;
	HashMap<Long,int[]> state;

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

	    // BUS: Start of a new subchain
	    if (current instanceof TestBus) {
		totalDistance += currentDistance;
		currentDistance = 0.0;

		for (int[] kids : state.values()) {
		    strandedKids += kids[0] + kids[1];
		}
		state = new HashMap();
		busCapacity = ((TestBus)current).getCapacity();
		busRiders = new int[] {0, 0};
		highWaterCapacityRatio = 0.0;
	    }

	    // STOP: Add students
	    else if (current instanceof TestStop) {
		TestStop stop = (TestStop)current;
		long destination = new Long(stop.getDestinationUUID());
		int[] kids = stop.getKids();
		double[] ratio = new double[2];

		for (int j = 0; j < 2; ++j) {
		    busRiders[j] += kids[j];
		    ratio[j] = (double)busRiders[j] / busCapacity[j];
		}
		highWaterCapacityRatio = Math.max(Math.max(highWaterCapacityRatio, ratio[0]), ratio[1]);

		if (state.containsKey(destination)) {
		    int[] kidsOnBoard = state.get(destination);
		    for (int j = 0; j < 2; ++j) kidsOnBoard[j] += kids[j];
		    state.put(destination, kidsOnBoard); // XXX
		}
		else {
		    state.put(destination, kids);
		}
	    }

	    // SCHOOL: Subtract students
	    else if (current instanceof TestSchool) {
		TestSchool school = (TestSchool)current;
		long location = new Long(school.getUUID());

		if (currentDistance < bellTime) {
		    if (state.containsKey(location)) {
			int [] kids = state.get(location);
			for (int j = 0; j < 2; ++j) busRiders[j] -= kids[j];
		    }
		    state.remove(location);
		}
	    }

	    // Account for distance (distance also used as proxy for time)
	    if (!(next instanceof TestBus)) { // XXX account for return to garage
		currentDistance += current.distanceTo(next);
	    }

	    if (next instanceof TestBus) {
		busCount += (int)Math.ceil(highWaterCapacityRatio);
	    }

	    // Print information
	    if (verbose)
		System.out.format("%5s %5s \t %1.5f \t %2.5f \t %2.5f \t %1.5f \t %d\n",
				  current, next,
				  current.distanceTo(next), currentDistance, totalDistance,
				  Math.ceil(highWaterCapacityRatio), busCount);

	    // Advance
	    current = next;
	    next = current.getNext();
	}

	// Compute and return score
	int dollars = busCount*costPerBusFixed + (int)(totalDistance*costPerUnitDistance);
	return HardSoftScore.valueOf(-strandedKids, -dollars);
    }

}
