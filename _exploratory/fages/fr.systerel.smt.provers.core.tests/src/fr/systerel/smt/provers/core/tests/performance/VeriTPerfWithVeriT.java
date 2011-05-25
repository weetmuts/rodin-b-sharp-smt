package fr.systerel.smt.provers.core.tests.performance;

import static br.ufrn.smt.solver.translation.SMTSolver.VERIT;
import fr.systerel.smt.provers.core.tests.SolverPerfWithVeriT;

public class VeriTPerfWithVeriT extends SolverPerfWithVeriT {
	public VeriTPerfWithVeriT() {
		super(VERIT);
	}
}
