package fr.systerel.smt.provers.core.tests;

import br.ufrn.smt.solver.translation.SMTSolver;
import fr.systerel.smt.provers.core.tests.utils.LemmaData;

public class XMLtoSMTTestsCVC3 extends XMLtoSMTTests {

	public XMLtoSMTTestsCVC3(final LemmaData data) {
		super(data, SMTSolver.CVC3);
	}

}
