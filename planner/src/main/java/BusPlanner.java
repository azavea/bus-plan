package com.azavea;

import java.io.IOException;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.azavea.Plan;
import com.azavea.Student;


public class BusPlanner {

    public static void main(String[] args) throws IOException {
        SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan before = new Plan(args[0], args[1]);
        Plan after = solver.solve(before);

        after.display();
        System.out.println(after.getScore());
    }

}
