package com.azavea;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.azavea.Student;
import com.azavea.Stop;
import com.azavea.Plan;


public class StudentChangeMoveFilter implements SelectionFilter<Student, ChangeMove> {

    public boolean accept(ScoreDirector scoreDirector, ChangeMove move) {
        Student student = (Student)move.getEntity();
        Stop stop = (Stop)move.getToPlanningValue();
        return student.getSchoolUuid().equals(stop.getSchoolUuid()) && student.eligible(stop);
    }

}
