package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import com.example.Plan;

import java.util.HashMap;


public class PlanScore implements EasyScoreCalculator<Plan> {

    public static final int costPerBusFixed = 10000;
    public static final int costPerUnitDistance = 1000;
    public static final double bellTime = 2.0; // in units of distance

    @Override
    public HardSoftScore calculateScore(Plan solution) {
	return calculateScore(solution, false);
    }

    public HardSoftScore calculateScore(Plan solution, Boolean verbose) {
	int dollars = 0;
	int delivered = 0;
	java.util.HashSet<Bus> seen = new java.util.HashSet<Bus>();

	if (verbose) solution.display();
	for (SourceOrSink entity : solution.getEntityList()) {
	    Bus bus = entity.getBus();
	    if (bus != null && !seen.contains(bus)) {
		seen.add(bus);
	    }
	}

	for (Bus bus : solution.getBusList()) {
	    SourceOrSinkOrAnchor current;
	    HashMap<Long, Integer> kids = new HashMap();
	    
	    if (bus.getNext() != null) {
		dollars += costPerBusFixed;
		current = bus;
		while (current != null) {
		    SourceOrSink next = current.getNext();

		    // Stop
		    if (current instanceof Stop) {
			Stop stop = (Stop)current;
			long key = new Long(stop.getDestination().getNode().getUuid());
			int totalWeight = 0;

			// Total kids at this stop
			for (int w : stop.getNode().getWeights()) {
			    totalWeight += w;
			}

			// Add kids
			if (kids.containsKey(key))
			    kids.put(key, kids.get(key) + totalWeight);
			else
			    kids.put(key, totalWeight);
		    }
		    else if (current instanceof School) {
			School school = (School)current;
			long key = new Long(school.getNode().getUuid());

			// Subtract kids
			if (kids.containsKey(key))
			    delivered += kids.get(key);
			kids.remove(key);
		    }

		    if (next != null)
			dollars += (int)(costPerUnitDistance * current.getNode().surfaceDistance(next.getNode()));
		    current = next;
		}
	    }
	}

	return HardSoftScore.valueOf(delivered - solution.getWeight(), -dollars);
    }

}
