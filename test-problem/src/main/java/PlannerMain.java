package com.example;

import java.io.IOException;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.example.Plan;
import com.example.Student;


public class PlannerMain {

    public static void main(String[] args) throws IOException {
        SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan before = new Plan(args[0], args[1]);
        Plan after = solver.solve(before);

        after.display();
        System.out.println(after.getScore());
    }

}
