package fr.systerel.smt.provers.core.tests;

import br.ufrn.smt.solver.translation.SMTSolver;
import fr.systerel.smt.provers.core.tests.utils.LemmaData;

public class XMLtoSMTTestsVerit extends XMLtoSMTTests {

	public XMLtoSMTTestsVerit(final LemmaData data) {
		super(data, SMTSolver.VERIT);
	}
}
