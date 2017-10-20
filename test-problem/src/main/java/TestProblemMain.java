package com.example;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import com.example.TestProblem;

public class TestProblemMain {

    public static void main(String[] args) {
	SolverFactory<TestProblem> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
	Solver<TestProblem> solver = solverFactory.buildSolver();
	TestProblem before = new TestProblem();
	TestProblem after = solver.solve(before);

	before.display();
	System.out.println("---------------------------------");
	after.display();
    }

}
