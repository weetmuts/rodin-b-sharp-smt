package fr.systerel.smt.provers.core.tests.performance;

import static br.ufrn.smt.solver.translation.SMTSolver.ALT_ERGO;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class AltErgoPerfWithVeriT extends SolverPerfWithVeriT {
	public AltErgoPerfWithVeriT() {
		super(ALT_ERGO);
	}
}
