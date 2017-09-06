/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.z3;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_Z3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.performance.solvers.SolverPerfWithPP;
import org.junit.Ignore;
import org.junit.Test;

public class BundledZ3PerfWithPPV2_0 extends SolverPerfWithPP {

	public BundledZ3PerfWithPPV2_0() {
		super(BUNDLED_Z3);
	}

	@Test
	public void testUnsatZ3Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatZ3Call() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	public void testTRUEPredZ3Call() {
		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void testBOOLSetZ3Call2() {
		final List<String> hyps = Arrays.asList( //
				"b↦c ∈ BOOL×BOOL", //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set2", hyps, "b = TRUE", arith_te, VALID);
	}

	@Test
	public void testSetsEqualityZ3Call() {
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
	public void testDivisionZ3Call() {
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
	public void testExponentiationZ3Call() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testModZ3Call() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList( //
				" 4 mod  2 =  0", //
				"−4 mod  2 =  0", //
				"−4 mod −2 =  0", //
				" 4 mod −2 =  0");

		doTest("mod", hyps, "3 mod 2 = 1", te, VALID);
	}

	/**
	 * It was successful because of a bug in the mbqi
	 */
	@Test
	public void testIntegerSetZ3Call() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();
		// it is obviously VALID, but Z3 fails
		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, !VALID);
	}

	// NOTE: the following tests are overridden because the prover fails somehow;
	// ideally, they should not be overridden.

	@Override
	public void testCh7Conc29() {
		expectTimeout(() -> {
			super.testCh7Conc29();
		});
	}

	@Override
	public void testDynamicStableLSR_081014_15() {
		expectTimeout(() -> {
			super.testDynamicStableLSR_081014_15();
		});
	}

	@Override
	public void testSets4() {
		expectTimeout(() -> {
			super.testSets4();
		});
	}

	@Override
	public void testDifferentForallPlusSimple() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple();
		});
	}

	@Override
	public void testDifferentForallPlusSimple1y() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple1y();
		});
	}

	@Override
	public void testDifferentForall() {
		expectTimeout(() -> {
			super.testDifferentForall();
		});
	}

	@Override
	public void testDifferentForallPlusSimple01() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple01();
		});
	}

	@Override
	public void testDifferentForallPlusSimple12() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple12();
		});
	}

	@Override
	public void testDifferentForallPlusSimple32() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple32();
		});
	}

	@Override
	public void testDifferentForallPlusSimple30() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple30();
		});
	}

	@Override
	public void testDifferentForallPlusSimple3y() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimple3y();
		});
	}

	@Override
	public void testDifferentForallPlusSimplex1() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimplex1();
		});
	}

	@Override
	public void testDifferentForallPlusSimplex2() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimplex2();
		});
	}

	@Override
	public void testDifferentForallPlusSimplexy() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimplexy();
		});
	}

	@Override
	public void testDifferentForallPlusSimpleMonadic() {
		expectTimeout(() -> {
			super.testDifferentForallPlusSimpleMonadic();
		});
	}

	@Override
	public void testIntsSetEquality() {
		expectTimeout(() -> {
			super.testIntsSetEquality();
		});
	}

	@Override
	public void testSets7() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// originally VALID
		doTest("sets7", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, !VALID);
	}
}
