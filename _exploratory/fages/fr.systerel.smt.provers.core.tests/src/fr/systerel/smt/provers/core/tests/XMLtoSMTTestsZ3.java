package fr.systerel.smt.provers.core.tests;

import br.ufrn.smt.solver.translation.SMTSolver;
import fr.systerel.smt.provers.core.tests.utils.LemmaData;

public class XMLtoSMTTestsZ3 extends XMLtoSMTTests {

	public XMLtoSMTTestsZ3(final LemmaData data) {
		super(data, SMTSolver.Z3);
	}

}
