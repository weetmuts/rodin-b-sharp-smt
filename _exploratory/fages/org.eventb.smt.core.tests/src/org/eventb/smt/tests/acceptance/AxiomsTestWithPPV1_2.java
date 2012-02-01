/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.translation.SMTTranslationApproach.USING_PP;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.junit.Test;

/**
 * This class contains acceptance tests of the plugin with pptranslation.
 * 
 * @author Yoann Guyot
 * 
 */
public class AxiomsTestWithPPV1_2 extends CommonSolverRunTests {
	static ITypeEnvironment simple_te = mTypeEnvironment(//
			"PS", "ℙ(ℙ(ℤ))", "S", "ℙ(ℤ)", "x", "ℤ");

	public AxiomsTestWithPPV1_2(final SMTSolver solver) {
		super(solver, null, V1_2, !GET_UNSAT_CORE);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_PP, lemmaName, inputHyps, inputGoal, te, !TRIVIAL,
				expectedSolverResult);
	}

	@Test
	public void testSingletonAxiom() {
		final List<String> hyps = Arrays.asList("S ∈ PS");

		doTest("singleton_axiom", hyps, "¬ x ∈ S", simple_te, !VALID);
	}
}