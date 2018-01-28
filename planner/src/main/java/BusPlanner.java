package com.azavea;

import java.io.IOException;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.azavea.Plan;
import com.azavea.Student;


/**
 * The main class.  The main fucntion takes various command-line
 * parameters and uses an OptaPlanner-based solver to optimize bus
 * routes.
 *
 * @author James McClain
 */
public class BusPlanner {

    public static void main(String[] args) throws IOException {
        // Parameters
        String costMatrixCsv = args[0];
        String studentCsv = args[1];
        String studentStopCsv = null;   // args[2]
        String garageCountCsv = null;   // args[3]
        String routeOutputCsv = null;   // args[4]
        String studentOutputCsv = null; // args[5]
        double sigmas = 1;
        double sigmaOverMu = 1.05;
        int costPerBus = 0;
        int maxRideMinutes = 175;
        int secondsPerStudentAtStop = 10; // For students over the first, per stop
        int secondsPerStudentAtSchool = 10; // Time to unload each student at their school
        int secondsPerStop = 45;
        int studentsPerBus = 62;
        int studentsPerStop = 10;

        if (args.length > 2) studentStopCsv = args[2];
        if (args.length > 3) garageCountCsv = args[3];
        if (args.length > 4) routeOutputCsv = args[4];
        if (args.length > 5) studentOutputCsv = args[5];

        Plan.COST_PER_BUS = costPerBus;
        Plan.MAX_RIDE_MINUTES = maxRideMinutes;
        Plan.NO_TIERING = true;
        Plan.SECONDS_PER_STUDENT_AT_STOP = secondsPerStudentAtStop;
        Plan.SECONDS_PER_STUDENT_AT_SCHOOL = secondsPerStudentAtSchool;
        Plan.SECONDS_PER_STOP = secondsPerStop;
        Plan.SIGMA_OVER_MU = sigmaOverMu;
        Plan.SIGMAS = sigmas;
        Plan.STUDENTS_PER_BUS = studentsPerBus;
        Plan.STUDENTS_PER_STOP = studentsPerStop;

        SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan before = new Plan(costMatrixCsv, studentCsv, studentStopCsv, garageCountCsv);
        Plan after = solver.solve(before);

        after.display();
	if (routeOutputCsv != null && studentOutputCsv != null)
	    after.render(routeOutputCsv, studentOutputCsv);
        System.out.println(after.getScore());
    }

}
