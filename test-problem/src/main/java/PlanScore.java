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
    public static final double bellTime = Math.PI / 2; // in units of distance

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
	    int[] inFlow = {0, 0};
	    int[] outFlow = {0, 0};
	    int multiplicity = 1;

	    if (bus.getNext() != null) {
		int[] capacity = bus.getNode().getWeights();
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
			for (Student kid : stop.getStudentList()) {
			    int[] weights = kid.getNode().getWeights();
			    kids.add(kid);
			    for (int i = 0; i < 2; ++i)
				inFlow[i] += weights[i];
			}
		    }
		    else if (current instanceof School) { // School
			School school = (School)current;

			// Tally delivered kids
			if (distance < bellTime) {
			    for (Student kid : kids) {
				if (kid.getSchool().equals(school)) {
				    int[] weights = kid.getNode().getWeights();
				    for (int i = 0; i < 2; ++i) {
					outFlow[i] -= weights[i];
					delivered += weights[i];
				    }
				}
			    }
			}

			// Remove delivered kids from bus
			kids = kids
			    .stream()
			    .filter(kid -> !kid.getSchool().equals(school))
			    .collect(Collectors.toList());
		    }

		    for (int i = 0; i < 2; ++i) {
		    	int temp = (int)Math.ceil((double)(inFlow[i] - outFlow[i])/capacity[i]);
		    	multiplicity = Math.max(temp, multiplicity);
		    }

		    current = next;
		}
	    }
	    dollars += multiplicity * costPerBusFixed;
	}

	return HardSoftScore.valueOf(delivered - solution.getWeight(), -dollars);
    }

}
