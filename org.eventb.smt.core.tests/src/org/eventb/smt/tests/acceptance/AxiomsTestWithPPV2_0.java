/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.TranslationApproach.USING_PP;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.tests.ConfigProvider;
import org.junit.Test;

/**
 * This class contains acceptance tests of the plugin with pptranslation.
 * 
 * @author Yoann Guyot
 * 
 */
public abstract class AxiomsTestWithPPV2_0 extends CommonSolverRunTests {
	static ITypeEnvironment simple_te = mTypeEnvironment(//
			"PS", "ℙ(ℙ(ℤ))", "S", "ℙ(ℤ)", "x", "ℤ");

	public AxiomsTestWithPPV2_0(ConfigProvider provider) {
		super(provider, null, USING_PP, V2_0, !GET_UNSAT_CORE);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(lemmaName, inputHyps, inputGoal, te, !TRIVIAL,
				expectedSolverResult);
	}

	@Test
	public void testSingletonAxiom() {
		final List<String> hyps = Arrays.asList("S ∈ PS");

		doTest("singleton_axiom", hyps, "¬ x ∈ S", simple_te, !VALID);
	}
}