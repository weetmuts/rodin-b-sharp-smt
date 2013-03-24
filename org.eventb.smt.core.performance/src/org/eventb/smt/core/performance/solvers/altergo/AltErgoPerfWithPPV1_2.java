/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.altergo;

import static java.util.Collections.emptyList;
import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.tests.ConfigProvider.LAST_ALTERGO;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.junit.Ignore;
import org.junit.Test;

public class AltErgoPerfWithPPV1_2 extends SolverPerfWithPP {
	public AltErgoPerfWithPPV1_2() {
		super(LAST_ALTERGO, V1_2);
	}

	@Test
	public void testTrivialUnsat() {
		List<String> noHyp = emptyList();

		doTest("trivial_unsat", noHyp, "1 = 1", mTypeEnvironment(), TRIVIAL,
				VALID);
	}

	@Test
	public void testUnsatAltErgoCall() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatAltErgoCall() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	public void testTRUEPredAltErgoCall() {
		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void testBOOLSetAltErgoCall2() {
		final List<String> hyps = Arrays.asList( //
				"b↦c ∈ BOOL×BOOL", //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set2", hyps, "b = TRUE", arith_te, VALID);
	}

	@Test
	public void testSetsEqualityAltErgoCall() {
		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testDivisionAltErgoCall() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList( //
				" 4 ÷  2 =  2", //
				"−4 ÷  2 = −2", //
				"−4 ÷ −2 =  2", //
				" 4 ÷ −2 = −2", //
				" 3 ÷  2 =  1", //
				"−3 ÷  2 = −1", //
				"−3 ÷ −2 =  1");

		doTest("division", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationAltErgoCall() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testModAltErgoCall() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList( //
				" 4 mod  2 =  0", //
				"−4 mod  2 =  0", //
				"−4 mod −2 =  0", //
				" 4 mod −2 =  0");

		doTest("mod", hyps, "3 mod 2 = 1", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetForAltErgoCall() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}
}
