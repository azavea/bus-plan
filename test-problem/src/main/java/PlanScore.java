package com.example;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.Plan;
import com.example.Student;


public class PlanScore implements EasyScoreCalculator<Plan> {

    public static final int costPerBusFixed = 10000;
    public static final int costPerUnitDistance = 1000;
    public static final double bellTime = 3.14; // in units of distance

    @Override
    public HardSoftScore calculateScore(Plan solution) {
	return calculateScore(solution, false);
    }

    public HardSoftScore calculateScore(Plan solution, Boolean verbose) {
	int dollars = 0;
	int delivered = 0;

	if (verbose) solution.display();

	for (Bus bus : solution.getBusList()) {
	    SourceOrSinkOrAnchor current;
	    List<Student> kids = new ArrayList<Student>();
	    double distance = 0.0;
	    
	    if (bus.getNext() != null) {
		dollars += costPerBusFixed;
		current = bus;

		while (current != null) {
		    SourceOrSink next = current.getNext();

		    if (next != null) {
			double d = current.getNode().surfaceDistance(next.getNode());
			distance += d;
			dollars += (int)(costPerUnitDistance * d);
		    }

		    if (current instanceof Stop) { // Stop
			Stop stop = (Stop)current;
			kids.addAll(stop.getStudentList());
		    }
		    else if (current instanceof School) { // School
			School school = (School)current;

			// Tally delivered kids
			for (Student kid : kids) {
			    if (kid.getSchool().equals(school)) {
				int[] ws = kid.getNode().getWeights();
				for (int w : ws)
				    delivered += w;
			    }
			}

			// Remove delivered kids from bus
			kids = kids
			    .stream()
			    .filter(kid -> !kid.getSchool().equals(school))
			    .collect(Collectors.toList());
		    }

		    current = next;
		}
	    }
	}

	return HardSoftScore.valueOf(delivered - solution.getWeight(), -dollars);
    }

}
