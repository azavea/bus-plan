package com.example;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

import com.example.Student;
import com.example.Stop;


public class StudentStopDistanceMeter implements NearbyDistanceMeter<Student, Stop> {

    @Override
    public double getNearbyDistance(Student student, Stop stop) {
        return student.distance(stop);
    }

}
