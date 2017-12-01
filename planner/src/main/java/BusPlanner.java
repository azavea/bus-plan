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
        double sigmas = 1;
        double sigmaOverMu = 1.05;
        int busesPerGarage = 1;
        int costPerBus = 0;
        int maxRideMinutes = 90;
        int secondsPerStudent = 15;
        int studentsPerBus = 62;

        Plan.BUSES_PER_GARAGE = busesPerGarage;
        Plan.COST_PER_BUS = costPerBus;
        Plan.MAX_RIDE_MINUTES = maxRideMinutes;
        Plan.SECONDS_PER_STUDENT = secondsPerStudent;
        Plan.STUDENTS_PER_BUS = studentsPerBus;
        Plan.SIGMA_OVER_MU = sigmaOverMu;
        Plan.SIGMAS = sigmas;

        SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan before = new Plan(costMatrixCsv, studentCsv);
        Plan after = solver.solve(before);

        after.display();
        System.out.println(after.getScore());
    }

}
