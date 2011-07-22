package fr.systerel.smt.provers.core.tests.performance;

import static fr.systerel.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class AltErgoPerfWithVeriT extends SolverPerfWithVeriT {
	public AltErgoPerfWithVeriT() {
		super(ALT_ERGO);
	}
}
