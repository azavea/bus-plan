package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import com.example.Plan;

import java.util.HashMap;


public class PlanScore implements EasyScoreCalculator<Plan> {

    // public static final int costPerBusFixed = 10000;
    // public static final int costPerUnitDistance = 1000;
    // public static final double bellTime = 2.0; // in units of distance

    @Override
    public HardSoftScore calculateScore(Plan solution) {
	return calculateScore(solution, false);
    }

    public HardSoftScore calculateScore(Plan solution, Boolean verbose) {
	int covered = 0;
	java.util.HashSet<Bus> seen = new java.util.HashSet<Bus>();

	if (verbose) solution.display();
	for (SourceOrSink entity : solution.getEntityList()) {
	    Bus bus = entity.getBus();
	    if (bus != null && !seen.contains(bus)) {
		seen.add(bus);
	    }
	}

	return HardSoftScore.valueOf(seen.size() - solution.getWeight(), 0);
    }

}
