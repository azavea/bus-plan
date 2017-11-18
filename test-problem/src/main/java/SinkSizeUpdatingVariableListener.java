package com.example;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.example.School;
import com.example.SourceOrSink;


public class SinkSizeUpdatingVariableListener implements VariableListener<SourceOrSink> {
    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, SourceOrSink sos) {}

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, SourceOrSink sos) {
        if (sos instanceof School)
            updateSinkSize(scoreDirector, sos);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, SourceOrSink sos) {}

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, SourceOrSink sos) {
        if (sos instanceof School)
            updateSinkSize(scoreDirector, sos);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, SourceOrSink sos) {}

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, SourceOrSink sos) {}

    protected void updateSinkSize(ScoreDirector scoreDirector, SourceOrSink sos) {
	School school = (School)sos;
	System.out.println("XXX");
        scoreDirector.beforeVariableChanged(school, "sinkSize");
        school.setSinkSize(school._sinkSize());
        scoreDirector.afterVariableChanged(school, "sinkSize");
        // TaskOrEmployee previous = sourceTask.getPreviousTaskOrEmployee();
        // Task shadowTask = sourceTask;
        // Integer previousEndTime = (previous == null ? null : previous.getEndTime());
        // Integer startTime = calculateStartTime(shadowTask, previousEndTime);
        // while (shadowTask != null && !Objects.equals(shadowTask.getStartTime(), startTime)) {
        //     scoreDirector.beforeVariableChanged(shadowTask, "startTime");
        //     shadowTask.setStartTime(startTime);
        //     scoreDirector.afterVariableChanged(shadowTask, "startTime");
        //     previousEndTime = shadowTask.getEndTime();
        //     shadowTask = shadowTask.getNextTask();
        //     startTime = calculateStartTime(shadowTask, previousEndTime);
        // }
    }
}
