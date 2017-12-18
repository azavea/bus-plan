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
        boolean sameSchool = student.getSchoolUuid().equals(stop.getSchoolUuid());
        boolean eligible = student.eligible(stop);
        boolean notFull = (stop.getStudentList().size() < Plan.STUDENTS_PER_STOP);

        return notFull && sameSchool && eligible;
    }

}
