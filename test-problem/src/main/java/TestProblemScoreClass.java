package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class TestProblemScoreClass implements EasyScoreCalculator<TestProblem> {

    @Override
    public HardSoftScore calculateScore(TestProblem solution) {
	int major = 0, minor = 0;

	for (TestBus bus : solution.getBusList()) {
	    if ((bus.getNext() != null) && !(bus.getNext() instanceof TestBus)) {
		major++;
	    }
	}

	return HardSoftScore.valueOf(major-2, minor);
    }

}
