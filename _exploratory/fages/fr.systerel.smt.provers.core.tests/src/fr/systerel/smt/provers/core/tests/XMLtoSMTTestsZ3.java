package fr.systerel.smt.provers.core.tests;

import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.internal.core.SMTSolver;

public class XMLtoSMTTestsZ3 extends XMLtoSMTTests {

	public XMLtoSMTTestsZ3(final LemmaData data) {
		super(data, SMTSolver.Z3);
	}

}
