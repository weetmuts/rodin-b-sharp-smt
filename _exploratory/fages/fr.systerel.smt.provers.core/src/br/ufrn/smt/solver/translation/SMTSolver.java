package br.ufrn.smt.solver.translation;

/**
 * This enum enumerates the solvers.
 * 
 * @author vitor
 * 
 */
public enum SMTSolver {
	ALT_ERGO("alt-ergo"), CVC3("cvc3"), VERIT("verit"), Z3("z3");

	private final String solver_name;

	private SMTSolver(final String name) {
		solver_name = name;
	}

	@Override
	public String toString() {
		return solver_name;
	}

	public void toString(final StringBuilder sb) {
		sb.append(solver_name);
	}
}
