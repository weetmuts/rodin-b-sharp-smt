/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.SMTTranslationApproach.USING_PP;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.junit.Ignore;
import org.junit.Test;

public class UnsatCoreExtractionPerfWithPP extends CommonSolverRunTests {
	private static boolean GOAL_NEEDED = true;
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public UnsatCoreExtractionPerfWithPP() {
		super(VERIT, V2_0);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_PP, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCore, final boolean expectedGoalNeed)
			throws IllegalArgumentException {
		doTest(USING_PP, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult, expectedUnsatCore, expectedGoalNeed);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm1UnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("Bug2105507Thm1UnsatCore", hyps,
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm2UnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))");

		doTest("Bug2105507Thm2UnsatCore", hyps,
				"∀n· ((n ∈ {0, 2, 4, 5}) ⇒ (n ∉ {6, 8, 9}))", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm3UnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))");

		doTest("Bug2105507Thm3UnsatCore", hyps,
				"∀n· n ∉ ({0, 2, 4, 5} ∩ {6, 8, 9})", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm4UnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("Bug2105507Thm4UnsatCore", hyps,
				"{0, 2, 4, 5} ∩ {6, 8, 9} = {}", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testSetsEqualityUnsatCore() {
		setPreferencesForVeriTProofTest();

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

		doTest("SetsEqualityUnsatCore", hyps, "p = q", te, VALID, unsat,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBoolsSetEqualityUnsatCore() {
		setPreferencesForVeriTProofTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(BOOL)", "non", "BOOL ↔ BOOL");

		final List<String> hyps = Arrays.asList(//
				"S ≠ ∅", //
				"non = {TRUE ↦ FALSE, FALSE ↦ TRUE}", //
				"∀ x · x ∈ S ⇔ non(x) ∈ S");

		final List<String> unsat = Arrays.asList(//
				"S ≠ ∅", //
				"non = {TRUE ↦ FALSE, FALSE ↦ TRUE}", //
				"∀ x · x ∈ S ⇔ non(x) ∈ S");

		doTest("BoolsSetEqualityUnsatCore", hyps, "S = BOOL", te, VALID, unsat,
				GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDFPSBoolUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("dfpsBoolUnsatCore", hyps,
				"{TRUE ↦ {FALSE}} ∈ {TRUE} → {{FALSE}}", te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimpleUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimpleUnsatCore", hyps,
				"{1 ↦ {0}} ∈ {1} → {{0}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * This is an example where, I think, the monadic optimization of membership
	 * predicate is unsuccessful without its refinement (axioms to add).
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimpleMonadicUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"f ∈ ℙ({1} → {{0}})", "f ≠ ∅");

		doTest("differentForallPlusSimpleMonadicUnsatCore", hyps,
				"{1 ↦ {0}} ∈ f", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple00UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple00UnsatCore", hyps,
				"{0 ↦ {0}} ∈ {0} → {{0}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple01UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple01UnsatCore", hyps,
				"{0 ↦ {1}} ∈ {0} → {{1}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple11UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple11UnsatCore", hyps,
				"{1 ↦ {1}} ∈ {1} → {{1}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple12UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple12UnsatCore", hyps,
				"{1 ↦ {2}} ∈ {1} → {{2}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple32UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple32UnsatCore", hyps,
				"{3 ↦ {2}} ∈ {3} → {{2}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple30UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple30UnsatCore", hyps,
				"{3 ↦ {0}} ∈ {3} → {{0}}", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple1yUnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple1yUnsatCore", hyps,
				"{1 ↦ {y}} ∈ {1} → {{y}}", arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple3yUnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimple3yUnsatCore", hyps,
				"{3 ↦ {y}} ∈ {3} → {{y}}", arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplex1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimplex1UnsatCore", hyps,
				"{x ↦ {1}} ∈ {x} → {{1}}", arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplex2UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimplex2UnsatCore", hyps,
				"{x ↦ {2}} ∈ {x} → {{2}}", arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplexyUnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("differentForallPlusSimplexyUnsatCore", hyps,
				"{x ↦ {y}} ∈ {x} → {{y}}", arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("sets1UnsatCore", hyps, "{x} ∈ {{x}, {y}}", arith_te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets2UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("sets2UnsatCore", hyps, "{{x},{y}} ∈ {{{x}, {y}}, {{x}}}",
				arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets3UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("sets3UnsatCore", hyps, "{{x} ↦ y} ∈ {{x}} → {y}", arith_te,
				VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets4UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("sets4UnsatCore", hyps, "∀ x · (x ∈ ℤ → ℙ(ℤ) ⇒ (∃ y · y ≠ x))",
				arith_te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 */
	@Test(timeout = 3000)
	public void testSets5UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment("a", "ℤ ↔ ℙ(ℤ)");
		te.addAll(arith_te);

		final List<String> hyps = Arrays.asList("a = {x ↦ {y}}");

		doTest("sets5UnsatCore", hyps, "a ∈ {x} → {{y}}", arith_te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets6UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList();

		doTest("sets6UnsatCore", hyps,
				"∀ x · (x ∈ {3} → {{4}} ⇒ (∃ y · y ≠ x))", arith_te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets7UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();

		doTest("sets7UnsatCore", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te,
				VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testSolverCallBelong1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList("g ∈ e");

		doTest("belong_1UnsatCore", hyps, "g ∈ f", pow_te, NOT_VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testRule20MacroInsideMacroUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		doTest("rule20_macro_inside_macroUnsatCore", hyps,

		"(λx· (x > 0 ∧ ((λy·y > 0 ∣ y+y) = ∅)) ∣ x+x) = ∅", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testRule20ManyForallsUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();

		doTest("rule20_many_forallsUnsatCore", hyps,
				"(λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) = ∅", te,
				VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testCallBelong1XtraSortXtraFunUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"e", "ℙ(S)", "f", "ℙ(S)", "g", "S", "a", "A", "c", "BOOL");

		final List<String> hyps = Arrays.asList("g ∈ e");

		final Set<String> expectedSorts = new HashSet<String>();
		expectedSorts.add("S");

		final Set<String> expectedFuns = new HashSet<String>();
		expectedFuns.add("(g S)");

		final Set<String> expectedPreds = new HashSet<String>(Arrays.asList(
				"(e S)", "(f S)"));

		doTTeTest("belong_1_type_environmentUnsatCore", hyps, "g ∈ f", te,
				expectedFuns, expectedPreds, expectedSorts);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test(timeout = 3000)
	public void testSolverCallSimpleUUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = Arrays.asList("a ∈ A");

		doTest("simpleUUnsatCore", hyps, "⊤", te, VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test(timeout = 3000)
	public void testSolverCallBelong3UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment( //
				"a", "S", "b", "T", "d", "U", "A", "ℙ(S)", //
				"r", "S ↔ T", "s", "(S × T) ↔ U");

		final List<String> hyps = Arrays.asList(//
				"a ∈ A", //
				"a↦b ∈ r", //
				"a↦b↦d ∈ s");

		doTest("belong_3UnsatCore", hyps, "⊤", te, VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testSolverCallUnsatCore() {
		setPreferencesForSolverTest(solver);

		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < z");

		doTest("solver_callUnsatCore", hyps, "x < z", arith_te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * ch8_circ_arbiter.1 from task 1 (Requirement Analysis) 's Rodin benchmarks
	 * on 'integer' theory
	 */
	@Test(timeout = 3000)
	public void testCh8CircArbiter1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"a1", "ℤ", "r1", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"a1 ≤ r1", //
				"r1 ≤ a1 + 1", //
				"r1 ≠ a1");

		doTest("ch8_circ_arbiter1UnsatCore", hyps, "r1 = a1 + 1", te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * quick_sort.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_arith' theory
	 */
	@Test(timeout = 3000)
	public void testQuickSort1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"k", "ℤ", "n", "ℤ", "x", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"(k ≥ 1) ∧ (k ≤ n)", //
				"(x ≥ 1) ∧ (x ≤ n − 1)", //
				"¬ ((x ≥ 1) ∧ (x ≤ k − 1))", //
				"¬ ((x ≥ k + 1) ∧ (x ≤ n − 1))");

		doTest("quick_sort1UnsatCore", hyps, "x = k", te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * bosch_switch.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_order_int' theory
	 */
	@Test(timeout = 3000)
	public void testBoschSwitch1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"i", "ℤ", "t", "ℤ", "t0", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"t ≥ 0", //
				"t0 ≥ 0", //
				"t0 < t", //
				"(i ≥ t0) ∧ (i ≤ t)");

		doTest("bosch_switch1UnsatCore", hyps, "i ≥ 0", te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * bepi_colombo.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'basic_set' theory
	 */
	@Test(timeout = 3000)
	public void testBepiColombo1UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "a", "S", "b", "S", "c", "S");

		final List<String> hyps = Arrays.asList(//
				"¬ a=b", //
				"¬ b=c", //
				"¬ c=a", //
				"S={a,b,c}");

		doTest("bepi_colombo1UnsatCore", hyps, "{a,b,c} = {c,a,b}", te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * ch915_bin.10 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'nonlinear_arith' theory
	 */
	@Test(timeout = 3000)
	// @Ignore("AltErgo MESSAGE: unknown (sat)")
	@Ignore("Implementation canceled")
	public void testCh915Bin10UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList("n ≥ 1");

		doTest("ch915_bin10UnsatCore", hyps, "1 ≤ (n+1) ÷ 2", te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test(timeout = 3000)
	public void testCh7LikeEvenSimplerUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("ch7_likeEvenSimplerUnsatCore", hyps, "A×B ⊆ ℕ×ℕ", te, !VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test(timeout = 3000)
	public void testCh7LikeMoreSimpleYetUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = Arrays.asList();

		doTest("ch7_likeMoreSimpleYetUnsatCore", hyps,
				"{0 ↦ d} ∈ ({0,1} →  D)", te, !VALID, hyps, GOAL_NEEDED);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	// @Ignore("(sets equality)")
	public void testDifferentForallUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = Arrays.asList();

		doTest("differentForallUnsatCore", hyps,
				"{1 ↦ {0 ↦ d}} ∈ ({1} → ({0} →  D))", te, VALID, hyps,
				GOAL_NEEDED);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 */
	@Test(timeout = 3000)
	// @Ignore("(sets equality)")
	public void testCh7Conc29UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = Arrays.asList();

		doTest("ch7_conc29UnsatCore", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBepiColombo3MiniUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList(//
				"TC = {3 ↦ 5}", //
				"TM = {1 ↦ 1}");

		doTest("bepi_colombo3MiniUnsatCore", hyps, "TC ∩ TM = ∅", te, VALID,
				hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testBepiColombo3MediumUnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList( //
				"TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}", //
						"TM = {1 ↦ 1}");

		doTest("bepi_colombo3MediumUnsatCore", hyps, "TC ∩ TM = ∅", te, VALID,
				hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	// @Ignore("Takes more than 30 seconds to return a result")
	public void testBepiColombo3Medium2UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList(//
				"TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6}", //
						"TM = ∅");

		doTest("bepi_colombo3Medium2UnsatCore", hyps, "TC ∩ TM = ∅", te, VALID,
				hyps, GOAL_NEEDED);
	}

	/**
	 * bepi_colombo.3 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'basic_relation' theory
	 * 
	 * The testBepiColombo3 doesn't run forever. It's because the alt-ergo
	 * solver takes too much time to prove. The translation is very fast, and
	 * the other solvers prove this problem in a much shorter time.
	 * 
	 */
	@Test(timeout = 3000)
	public void testBepiColombo3UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList(//
				"TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}", //
						"TM = {1 ↦ 1,1 ↦ 2,1 ↦ 7,1 ↦ 8,3 ↦ 25,5 ↦ 1,5 ↦ 2,5 ↦ 3,5 ↦ 4,6 ↦ 6,6 ↦ 10,17 ↦ 2,21 ↦ 3}");

		doTest("bepi_colombo3UnsatCore", hyps, "TC ∩ TM = ∅", te, VALID, hyps,
				GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	// @Ignore("Re-add when ppTrans updated (sets equality)")
	public void testDynamicStableLSR_081014_15UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "h", "ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = Arrays.asList(//
				"h ∈ S →  (S × S →  ℕ)", //
				"n ∈ dom(h)", //
				"m ↦ n ∈ dom(h(n))", //
				"h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15UnsatCore", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	// @Ignore("Re-add when ppTrans updated (sets equality)")
	public void testch910_ring_6UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment(//
				"P", "ℙ(ℤ)", "itv", "ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");

		final List<String> hyps = Arrays.asList("itv ∈ P → (P → ℙ(P))");
		doTest("ch910_ring_6UnsatCore", hyps, "itv∼;({f} ◁ itv) ⊆ id", te,
				VALID, hyps, GOAL_NEEDED);
	}

	@Test(timeout = 3000)
	public void testLinearSort29UnsatCore() {
		setPreferencesForSolverTest(solver);

		final ITypeEnvironment te = mTypeEnvironment( //
				"f", "ℙ(ℤ × ℤ)", "r", "ℙ(ℤ × BOOL)", //
				"m", "ℤ", "x", "ℤ", "j", "ℤ");

		final List<String> hyps = Arrays.asList(//
				"r ∈ 1 ‥ m → BOOL", //
				"x ∈ 1 ‥ m", "j+1 ∈ dom(f)");

		doTest("linear_sort_29UnsatCore", hyps, "x ∈ dom(r{f(j+1) ↦ TRUE})",
				te, VALID, hyps, GOAL_NEEDED);
	}
}
