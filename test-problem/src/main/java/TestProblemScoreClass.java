package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class TestProblemScoreClass implements EasyScoreCalculator<TestProblem> {

    public static final int costPerBus = 70000;
    public static final int costPerUnitDistance = 1000;

    @Override
    public HardSoftScore calculateScore(TestProblem solution) {
	int busCount = 0;
	double distance = 0;
	TestNode start, current, next;

	start = current = solution.getBusList().get(0);
	next = current.getNext();

	while (next != start) {
	    if (!(next instanceof TestBus))
		distance += current.distanceTo(next);
	    if ((current instanceof TestBus) && (!(next instanceof TestBus)))
		busCount++;
	    current = next;
	    next = current.getNext();
	}

	int dollars = busCount*costPerBus + (int)(distance * costPerUnitDistance);
	return HardSoftScore.valueOf(0, -dollars);
    }

}
