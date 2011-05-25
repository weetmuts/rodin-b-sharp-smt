package fr.systerel.smt.provers.core.tests.performance;

import static br.ufrn.smt.solver.translation.SMTSolver.CVC3;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class Cvc3PerfWithVeriT extends SolverPerfWithVeriT {
	public Cvc3PerfWithVeriT() {
		super(CVC3);
	}
}
