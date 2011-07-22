package fr.systerel.smt.provers.core.tests;

import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.internal.core.SMTSolver;

public class XMLtoSMTTestsAltErgo extends XMLtoSMTTests {

	public XMLtoSMTTestsAltErgo(final LemmaData data) {
		super(data, SMTSolver.ALT_ERGO);
	}

}
