package fr.systerel.smt.provers.core.tests.performance;

import static fr.systerel.smt.provers.internal.core.SMTSolver.VERIT;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class VeriTPerfWithVeriT extends SolverPerfWithVeriT {
	public VeriTPerfWithVeriT() {
		super(VERIT);
	}
}
