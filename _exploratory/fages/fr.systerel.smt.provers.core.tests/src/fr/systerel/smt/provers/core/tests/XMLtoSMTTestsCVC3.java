package fr.systerel.smt.provers.core.tests;

import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.internal.core.SMTSolver;

public class XMLtoSMTTestsCVC3 extends XMLtoSMTTests {

	public XMLtoSMTTestsCVC3(final LemmaData data) {
		super(data, SMTSolver.CVC3);
	}

}
