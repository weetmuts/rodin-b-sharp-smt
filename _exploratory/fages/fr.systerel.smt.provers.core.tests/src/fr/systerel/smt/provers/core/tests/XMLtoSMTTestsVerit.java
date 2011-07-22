package fr.systerel.smt.provers.core.tests;

import fr.systerel.smt.provers.core.tests.utils.LemmaData;
import fr.systerel.smt.provers.internal.core.SMTSolver;

public class XMLtoSMTTestsVerit extends XMLtoSMTTests {

	public XMLtoSMTTestsVerit(final LemmaData data) {
		super(data, SMTSolver.VERIT);
	}
}
