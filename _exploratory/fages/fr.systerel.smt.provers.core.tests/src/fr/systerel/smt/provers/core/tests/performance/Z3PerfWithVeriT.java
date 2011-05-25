package fr.systerel.smt.provers.core.tests.performance;

import static br.ufrn.smt.solver.translation.SMTSolver.Z3;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class Z3PerfWithVeriT extends SolverPerfWithVeriT {
	public Z3PerfWithVeriT() {
		super(Z3);
	}
}
