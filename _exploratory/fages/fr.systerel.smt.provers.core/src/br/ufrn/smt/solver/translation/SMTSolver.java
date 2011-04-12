package br.ufrn.smt.solver.translation;

public enum SMTSolver {
	Z3(0), CVC3(1), VERIT(2), ALT_ERGO(3);

	private int solver;

	SMTSolver(int solver) {
		this.solver = solver;
	}

}
