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

import static org.eventb.smt.core.TranslationApproach.USING_PP;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.SolverKind;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.junit.Test;

public abstract class UnsatCoreExtractionWithPP extends CommonSolverRunTests {
	private static boolean GOAL_NEEDED = true;
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public UnsatCoreExtractionWithPP(final SolverKind solver,
			final SMTLIBVersion smtlibVersion) {
		super(solver, null, USING_PP, smtlibVersion, GET_UNSAT_CORE);
	}

	@Test(timeout = 3000)
	public void noHypothesisGoalNeeded() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("noHypothesisGoalNeeded", hyps,
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void hypothesisNotNeededGoalNeeded() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))");

		final List<String> unsat = Arrays.asList();

		doTest("hypothesisNotNeededGoalNeeded", hyps,
				"∀n· ((n ∈ {0, 2, 4, 5}) ⇒ (n ∉ {6, 8, 9}))", te, VALID, unsat,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void someHypothesesNeededGoalNeeded() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"p", "ℙ(ℤ)", "q", "ℙ(ℤ)", "n", "ℤ", "m", "ℤ");

		final List<String> hyps = Arrays.asList(//
				"n > 1", //
				"m = 1", //
				"p ∈ ℙ({1})", //
				"q ∈ ℙ({1})", //
				"p ≠ ∅", //
				"n ∉ p", //
				"q ≠ ∅", //
				"m ∈ q");

		final List<String> unsat = Arrays.asList(//
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("someHypothesesNeededGoalNeeded", hyps, "p = q", te, VALID,
				unsat, GOAL_NEEDED);
	}

	@Test
	// (timeout = 3000)
	public void hypothesesNeededGoalNotNeeded() {
		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < x");

		doTest("hypothesesNeededGoalNotNeeded", hyps, "x < z", arith_te, VALID,
				hyps, !GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void someHypothesesNeededGoalNotNeeded() {
		final List<String> hyps = Arrays.asList(//
				"z + z = z", //
				"x < y", //
				"y < x");

		final List<String> unsat = Arrays.asList(//
				"x < y", //
				"y < x");

		doTest("someHypothesesNeededGoalNotNeeded", hyps, "x < z", arith_te,
				VALID, unsat, !GOAL_NEEDED);
	}

	/**
	 * quick_sort.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_arith' theory
	 */
	@Test(timeout = 3000)
	public void testQuickSort1UnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"k", "ℤ", "n", "ℤ", "x", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"(k ≥ 1) ∧ (k ≤ n)", //
				"(x ≥ 1) ∧ (x ≤ n − 1)", //
				"¬ ((x ≥ 1) ∧ (x ≤ k − 1))", //
				"¬ ((x ≥ k + 1) ∧ (x ≤ n − 1))");

		final List<String> unsat = Arrays.asList(//
				"(x ≥ 1) ∧ (x ≤ n − 1)", //
				"¬ ((x ≥ 1) ∧ (x ≤ k − 1))", //
				"¬ ((x ≥ k + 1) ∧ (x ≤ n − 1))");

		doTest("quick_sort1UnsatCore", hyps, "x = k", te, VALID, unsat,
				GOAL_NEEDED);
	}

	/**
	 * bosch_switch.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_order_int' theory
	 */
	@Test(timeout = 3000)
	public void testBoschSwitch1UnsatCore() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"i", "ℤ", "t", "ℤ", "t0", "ℤ");

		// QF_LIA
		final List<String> hyps = Arrays.asList(//
				"t ≥ 0", //
				"t0 ≥ 0", //
				"t0 < t", //
				"(i ≥ t0) ∧ (i ≤ t)");

		final List<String> unsat = Arrays.asList(//
				"t0 ≥ 0", //
				"(i ≥ t0) ∧ (i ≤ t)");

		doTest("bosch_switch1UnsatCore", hyps, "i ≥ 0", te, VALID, unsat,
				GOAL_NEEDED);
	}
}
