package com.example;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.example.School;
import com.example.SourceOrSink;


public class SinkSizeUpdatingVariableListener implements VariableListener<SourceOrSinkOrAnchor> {
   @Override
    public boolean requiresUniqueEntityEvents() {
        return true;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
            updateSinkSizes(scoreDirector, sos);
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        updateSinkSizes(scoreDirector, sos);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        updateSinkSizes(scoreDirector, sos);
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        updateSinkSizes(scoreDirector, sos);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        updateSinkSizes(scoreDirector, sos);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        updateSinkSizes(scoreDirector, sos);
    }

    protected void updateSinkSizes(ScoreDirector scoreDirector, SourceOrSinkOrAnchor sos) {
        Bus anchor = sos.getBus();
        if (anchor != null) {
            SourceOrSink current = anchor.getNext();

            while (current != null) {
                if (current instanceof School) {
                    School school = (School)current;
                    scoreDirector.beforeVariableChanged(school, "sinkSize");
                    school.setSinkSize(school._sinkSize());
                    scoreDirector.afterVariableChanged(school, "sinkSize");
                }
                current = current.getNext();
            }
        }
    }
}
