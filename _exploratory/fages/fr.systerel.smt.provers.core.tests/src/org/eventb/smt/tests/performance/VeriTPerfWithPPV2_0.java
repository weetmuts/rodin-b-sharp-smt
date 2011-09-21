/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.performance;

import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.tests.SolverPerfWithPP;
import org.junit.Test;

public class VeriTPerfWithPPV2_0 extends SolverPerfWithPP {

	public VeriTPerfWithPPV2_0() {
		super(VERIT, V2_0);
	}

	@Test//(timeout = 3000)
	public void testSetsEqualityUnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)",
				"n", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("p ∈ ℙ({1})");
		hyps.add("p ≠ ∅");
		hyps.add("q ∈ ℙ({1})");
		hyps.add("q ≠ ∅");
		hyps.add("n > 1");

		final List<String> expectedUnsatCore = new ArrayList<String>();
		expectedUnsatCore.add("p ∈ ℙ({1})");
		expectedUnsatCore.add("p ≠ ∅");
		expectedUnsatCore.add("q ∈ ℙ({1})");
		expectedUnsatCore.add("q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID, expectedUnsatCore,
				true);
	}
}
