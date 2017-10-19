package com.example;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import com.example.TestProblemSolution;

public class TestProblemMain {

    public static void main(String[] args) {
	SolverFactory<TestProblemSolution> solverFactory = SolverFactory.createFromXmlResource("solver.xml");
	Solver<TestProblemSolution> solver = solverFactory.buildSolver();
	TestProblemSolution before = new TestProblemSolution();
	TestProblemSolution after = solver.solve(before);

	System.out.println(after);
    }

}
