package com.azavea;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

import com.azavea.Student;
import com.azavea.Stop;


public class StudentStopDistanceMeter implements NearbyDistanceMeter<Student, Stop> {

    @Override
    public double getNearbyDistance(Student student, Stop stop) {
	if (student.eligible(stop))
	    return 1.0;
	else
	    return 1000000.0;
    }

}
