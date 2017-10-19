package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class TestProblemScoreClass implements EasyScoreCalculator<TestProblemSolution> {

    @Override
    public HardSoftScore calculateScore(TestProblemSolution solution) {
	return HardSoftScore.valueOf(13, 33);
    }

}
