package com.example;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.example.Plan;
import com.example.PlanScore;


public class PlannerMain {

    public static void main(String[] args) {
	SolverFactory<Plan> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
	Solver<Plan> solver = solverFactory.buildSolver();
	Plan before = new Plan();
	Plan after = solver.solve(before);

	(new PlanScore()).calculateScore(after, true);
	System.out.println(after.getScore());
    }

}
