package com.azavea;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.*;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.*;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.azavea.SourceOrSink;
import com.azavea.SourceOrSinkOrAnchor;
import com.azavea.Plan;


public class SoSMoveFilter implements SelectionFilter<SourceOrSink, AbstractMove> {

    public boolean accept(ScoreDirector scoreDirector, AbstractMove _move) {

        if (Plan.NO_TIERING) {

            // ChangeMove
            if (_move instanceof ChangeMove) {
                ChangeMove move = (ChangeMove)_move;
                SourceOrSink entity = (SourceOrSink)move.getEntity();
                if (move.getToPlanningValue() instanceof SourceOrSink) {
                    SourceOrSink previous = (SourceOrSink)move.getToPlanningValue();
                    if (entity.getBus().equals("dummy"))
                        return true;
                    else
                        return entity.getSchoolUuid().equals(previous.getSchoolUuid());
                }
            }

            // SwapMove
            else if (_move instanceof SwapMove) {
                SwapMove move = (SwapMove)_move;
                if ((move.getLeftEntity() instanceof SourceOrSink) &&
                    (move.getRightEntity() instanceof SourceOrSink)) {
                    SourceOrSink left = (SourceOrSink)move.getLeftEntity();
                    SourceOrSink right = (SourceOrSink)move.getRightEntity();
                    if (left.getBus().equals("dummy") && right.getBus().equals("dummy"))
                        return true;
                    else
                        return left.getSchoolUuid().equals(right.getSchoolUuid());
                }
            }

            // SubChainSwapMove
            else if (_move instanceof SubChainSwapMove) {
                SubChainSwapMove move = (SubChainSwapMove)_move;
                if ((move.getLeftSubChain().getEntityList().get(0) instanceof SourceOrSink) &&
                    (move.getRightSubChain().getEntityList().get(0) instanceof SourceOrSink)) {
                    SourceOrSink left = (SourceOrSink)move.getLeftSubChain().getEntityList().get(0);
                    SourceOrSink right = (SourceOrSink)move.getRightSubChain().getEntityList().get(0);
                    if (left.getBus().equals("dummy") && right.getBus().equals("dummy"))
                        return true;
                    else
                        return left.getSchoolUuid().equals(right.getSchoolUuid());
                }
            }

            // SubChainReversingSwapMove
            else if (_move instanceof SubChainReversingSwapMove) {
                SubChainReversingSwapMove move = (SubChainReversingSwapMove)_move;
                if ((move.getLeftSubChain().getEntityList().get(0) instanceof SourceOrSink) &&
                    (move.getRightSubChain().getEntityList().get(0) instanceof SourceOrSink)) {
                    SourceOrSink left = (SourceOrSink)move.getLeftSubChain().getEntityList().get(0);
                    SourceOrSink right = (SourceOrSink)move.getRightSubChain().getEntityList().get(0);
                    if (left.getBus().equals("dummy") && right.getBus().equals("dummy"))
                        return true;
                    else
                        return left.getSchoolUuid().equals(right.getSchoolUuid());
                }
            }

            // TailChainSwapMove
            else if (_move instanceof TailChainSwapMove) {
                TailChainSwapMove move = (TailChainSwapMove)_move;
                if ((move.getLeftEntity() instanceof SourceOrSink) &&
                    (move.getRightValue() instanceof SourceOrSink)) {
                    SourceOrSink left = (SourceOrSink)move.getLeftEntity();
                    SourceOrSink right = (SourceOrSink)move.getRightValue();
                    if (left.getBus().equals("dummy") && right.getBus().equals("dummy"))
                        return true;
                    else
                        return left.getSchoolUuid().equals(right.getSchoolUuid());
                }
            }
        }

        // Anything else
        return true;
    }

}
