package fr.systerel.smt.provers.core.tests.performance;

import static br.ufrn.smt.solver.translation.SMTSolver.ALT_ERGO;
import fr.systerel.smt.provers.core.tests.SolverPerfWithPP;

public class AltErgoPerfWithPP extends SolverPerfWithPP {
	public AltErgoPerfWithPP() {
		super(ALT_ERGO);
	}
}
