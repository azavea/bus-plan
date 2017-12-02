package com.azavea;

import java.io.IOException;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.azavea.Plan;
import com.azavea.Student;


public class BusPlanner {

    public static void main(String[] args) throws IOException {
        // Parameters
        String costMatrixCsv = args[0];
        String studentCsv = args[1];
        String studentStopsCsv = null; // args[2]
        String routeCsv = null; // args[3]
        String assignmentCsv = null; // args[4]
        double sigmas = 1;
        double sigmaOverMu = 1.05;
        int busesPerGarage = 1;
        int costPerBus = 0;
        int maxRideMinutes = 90;
        int secondsPerStudent = 15;
        int studentsPerBus = 62;

        if (args.length > 2) studentStopsCsv = args[2];
        if (args.length > 3) routeCsv = args[3];
        if (args.length > 4) assignmentCsv = args[4];

        Plan.BUSES_PER_GARAGE = busesPerGarage;
        Plan.COST_PER_BUS = costPerBus;
        Plan.MAX_RIDE_MINUTES = maxRideMinutes;
        Plan.SECONDS_PER_STUDENT = secondsPerStudent;
        Plan.STUDENTS_PER_BUS = studentsPerBus;
        Plan.SIGMA_OVER_MU = sigmaOverMu;
        Plan.SIGMAS = sigmas;

        SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan before = new Plan(costMatrixCsv, studentCsv, studentStopsCsv);
        Plan after = solver.solve(before);

        after.display();
	if (routeCsv != null && assignmentCsv != null) after.render(routeCsv, assignmentCsv);
        System.out.println(after.getScore());
    }

}
