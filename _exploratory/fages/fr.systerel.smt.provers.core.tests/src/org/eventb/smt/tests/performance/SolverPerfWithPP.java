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

import static org.eventb.smt.translation.SMTTranslationApproach.USING_PP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.translation.SMTLIBVersion;
import org.junit.Ignore;
import org.junit.Test;

public class SolverPerfWithPP extends CommonSolverRunTests {
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public SolverPerfWithPP(SMTSolver solver, SMTLIBVersion smtlibVersion) {
		super(solver, smtlibVersion);
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
	public void testBug2105507Thm1() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("Bug2105507Thm1", hyps,
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))", te, VALID);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm2() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))");

		doTest("Bug2105507Thm2", hyps,
				"∀n· ((n ∈ {0, 2, 4, 5}) ⇒ (n ∉ {6, 8, 9}))", te, VALID);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm3() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList(//
				"∀m· ((m ∈ {0, 2, 4}) ⇒ (m ∉ {5, 6, 8, 9}))");

		doTest("Bug2105507Thm3", hyps, "∀n· n ∉ ({0, 2, 4, 5} ∩ {6, 8, 9})",
				te, VALID);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm4() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("Bug2105507Thm4", hyps, "{0, 2, 4, 5} ∩ {6, 8, 9} = {}", te,
				VALID);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm5() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		final Set<Integer> usedValues = new HashSet<Integer>();
		final StringBuilder goalBuilder = new StringBuilder();
		setToString(goalBuilder, produceDisjointSet(usedValues, 10, 10000));
		goalBuilder.append(" ∩ ");
		setToString(goalBuilder, produceDisjointSet(usedValues, 10, 10000));
		goalBuilder.append(" ∩ ");
		setToString(goalBuilder, produceDisjointSet(usedValues, 10, 10000));
		goalBuilder.append(" ∩ ");
		setToString(goalBuilder, produceDisjointSet(usedValues, 10, 10000));
		goalBuilder.append(" = {}");

		doTest("Bug2105507Thm5", hyps, goalBuilder.toString(), te, VALID);
	}

	@Test(timeout = 3000)
	public void testBug2105507Thm6() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		final Set<Integer> usedValues = new HashSet<Integer>();
		final StringBuilder goalBuilder = new StringBuilder();
		setToString(goalBuilder, produceDisjointSet(usedValues, 100, 10000));
		goalBuilder.append(" ∩ ");
		setToString(goalBuilder, produceDisjointSet(usedValues, 100, 10000));
		goalBuilder.append(" = {}");

		doTest("Bug2105507Thm6", hyps, goalBuilder.toString(), te, VALID);
	}

	@Test(timeout = 3000)
	public void testSetsEquality() {
		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("p ∈ ℙ({1})");
		hyps.add("p ≠ ∅");
		hyps.add("q ∈ ℙ({1})");
		hyps.add("q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("(one-point rule needed)")
	public void testIntsSetEquality() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("∀ x · x + 1 ∈ S");

		doTest("IntsSetEquality", hyps, "S = ℤ", te, VALID);
	}

	@Test(timeout = 3000)
	public void testBoolsSetEquality() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(BOOL)", "non",
				"BOOL ↔ BOOL");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("S ≠ ∅");
		hyps.add("non = {TRUE ↦ FALSE, FALSE ↦ TRUE}");
		hyps.add("∀ x · x ∈ S ⇔ non(x) ∈ S");

		doTest("BoolsSetEquality", hyps, "S = BOOL", te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDFPSBool() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("dfpsBool", hyps, "{TRUE ↦ {FALSE}} ∈ {TRUE} → {{FALSE}}", te,
				VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple", hyps, "{1 ↦ {0}} ∈ {1} → {{0}}",
				te, VALID);
	}

	/**
	 * This is an example where, I think, the monadic optimization of membership
	 * predicate is unsuccessful without its refinement (axioms to add).
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimpleMonadic() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("f ∈ ℙ({1} → {{0}})");
		hyps.add("f ≠ ∅");

		doTest("differentForallPlusSimpleMonadic", hyps, "{1 ↦ {0}} ∈ f", te,
				VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple00() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple00", hyps, "{0 ↦ {0}} ∈ {0} → {{0}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple01() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple01", hyps, "{0 ↦ {1}} ∈ {0} → {{1}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple11() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple11", hyps, "{1 ↦ {1}} ∈ {1} → {{1}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple12() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple12", hyps, "{1 ↦ {2}} ∈ {1} → {{2}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple32() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple32", hyps, "{3 ↦ {2}} ∈ {3} → {{2}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple30() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple30", hyps, "{3 ↦ {0}} ∈ {3} → {{0}}",
				te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple1y() {
		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple1y", hyps, "{1 ↦ {y}} ∈ {1} → {{y}}",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimple3y() {
		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple3y", hyps, "{3 ↦ {y}} ∈ {3} → {{y}}",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplex1() {
		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimplex1", hyps, "{x ↦ {1}} ∈ {x} → {{1}}",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplex2() {
		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimplex2", hyps, "{x ↦ {2}} ∈ {x} → {{2}}",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testDifferentForallPlusSimplexy() {
		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimplexy", hyps, "{x ↦ {y}} ∈ {x} → {{y}}",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets1() {
		final List<String> hyps = new ArrayList<String>();

		doTest("sets1", hyps, "{x} ∈ {{x}, {y}}", arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets2() {
		final List<String> hyps = new ArrayList<String>();

		doTest("sets2", hyps, "{{x},{y}} ∈ {{{x}, {y}}, {{x}}}", arith_te,
				VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets3() {
		final List<String> hyps = new ArrayList<String>();

		doTest("sets3", hyps, "{{x} ↦ y} ∈ {{x}} → {y}", arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets4() {
		final List<String> hyps = new ArrayList<String>();

		doTest("sets4", hyps, "∀ x · (x ∈ ℤ → ℙ(ℤ) ⇒ (∃ y · y ≠ x))", arith_te,
				VALID);
	}

	/**
	 */
	@Test(timeout = 3000)
	public void testSets5() {
		final ITypeEnvironment te = mTypeEnvironment("a", "ℤ ↔ ℙ(ℤ)");
		te.addAll(arith_te);

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a = {x ↦ {y}}");

		doTest("sets5", hyps, "a ∈ {x} → {{y}}", arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets6() {
		final List<String> hyps = new ArrayList<String>();

		doTest("sets6", hyps, "∀ x · (x ∈ {3} → {{4}} ⇒ (∃ y · y ≠ x))",
				arith_te, VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	public void testSets7() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();

		doTest("sets7", hyps, "∀ x · x ∈ ℙ(ℙ(ℤ)) ⇒ (∃ y · y ≠ x)", te, VALID);
	}

	@Test(timeout = 3000)
	public void testSolverCallBelong1() {
		final List<String> hyps = new ArrayList<String>();
		hyps.add("g ∈ e");

		doTest("belong_1", hyps, "g ∈ f", pow_te, NOT_VALID);
	}

	@Test(timeout = 3000)
	public void testRule20MacroInsideMacro() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		doTest("rule20_macro_inside_macro", hyps,

		"(λx· (x > 0 ∧ ((λy·y > 0 ∣ y+y) = ∅)) ∣ x+x) = ∅", te, VALID);
	}

	@Test(timeout = 3000)
	public void testRule20ManyForalls() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();

		doTest("rule20_many_foralls", hyps,
				"(λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) = ∅", te,
				VALID);
	}

	@Test(timeout = 3000)
	public void testCallBelong1XtraSortXtraFun() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"e", "ℙ(S)", "f", "ℙ(S)", "g", "S", "a", "A", "c", "BOOL");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("g ∈ e");

		final Set<String> expectedSorts = new HashSet<String>();
		expectedSorts.add("S");

		final Set<String> expectedFuns = new HashSet<String>();
		expectedFuns.add("(g S)");

		final Set<String> expectedPreds = new HashSet<String>();
		expectedPreds.add("(e S)");
		expectedPreds.add("(f S)");

		doTTeTest("belong_1_type_environment", hyps, "g ∈ f", te, expectedFuns,
				expectedPreds, expectedSorts);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test(timeout = 3000)
	public void testSolverCallSimpleU() {
		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");

		doTest("simpleU", hyps, "⊤", te, VALID);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test(timeout = 3000)
	public void testSolverCallBelong3() {
		final ITypeEnvironment te = mTypeEnvironment(
				//
				"a", "S", "b", "T", "d", "U", "A", "ℙ(S)", "r", "S ↔ T", "s",
				"(S × T) ↔ U");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");
		hyps.add("a↦b ∈ r");
		hyps.add("a↦b↦d ∈ s");

		doTest("belong_3", hyps, "⊤", te, VALID);
	}

	@Test(timeout = 3000)
	public void testSolverCall() {
		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("solver_call", hyps, "x < z", arith_te, VALID);
	}

	/**
	 * ch8_circ_arbiter.1 from task 1 (Requirement Analysis) 's Rodin benchmarks
	 * on 'integer' theory
	 */
	@Test(timeout = 3000)
	public void testCh8CircArbiter1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"a1", "ℤ", "r1", "ℤ");

		// QF_LIA

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a1 ≤ r1");
		hyps.add("r1 ≤ a1 + 1");
		hyps.add("r1 ≠ a1");

		doTest("ch8_circ_arbiter1", hyps, "r1 = a1 + 1", te, VALID);
	}

	/**
	 * quick_sort.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_arith' theory
	 */
	@Test(timeout = 3000)
	public void testQuickSort1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"k", "ℤ", "n", "ℤ", "x", "ℤ");

		// QF_LIA

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(k ≥ 1) ∧ (k ≤ n)");
		hyps.add("(x ≥ 1) ∧ (x ≤ n − 1)");
		hyps.add("¬ ((x ≥ 1) ∧ (x ≤ k − 1))");
		hyps.add("¬ ((x ≥ k + 1) ∧ (x ≤ n − 1))");

		doTest("quick_sort1", hyps, "x = k", te, VALID);
	}

	/**
	 * bosch_switch.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_order_int' theory
	 */
	@Test(timeout = 3000)
	public void testBoschSwitch1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"i", "ℤ", "t", "ℤ", "t0", "ℤ");

		// QF_LIA

		final List<String> hyps = new ArrayList<String>();
		hyps.add("t ≥ 0");
		hyps.add("t0 ≥ 0");
		hyps.add("t0 < t");
		hyps.add("(i ≥ t0) ∧ (i ≤ t)");

		doTest("bosch_switch1", hyps, "i ≥ 0", te, VALID);
	}

	/**
	 * bepi_colombo.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'basic_set' theory
	 */
	@Test(timeout = 3000)
	public void testBepiColombo1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "a", "S", "b", "S", "c", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("¬ a=b");
		hyps.add("¬ b=c");
		hyps.add("¬ c=a");
		hyps.add("S={a,b,c}");

		doTest("bepi_colombo1", hyps, "{a,b,c} = {c,a,b}", te, VALID);
	}

	/**
	 * ch915_bin.10 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'nonlinear_arith' theory
	 */
	@Test(timeout = 3000)
	// @Ignore("AltErgo MESSAGE: unknown (sat)")
	@Ignore("Implementation canceled")
	public void testCh915Bin10() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch915_bin10", hyps, "1 ≤ (n+1) ÷ 2", te, VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test(timeout = 3000)
	public void testCh7LikeEvenSimpler() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("ch7_likeEvenSimpler", hyps, "A×B ⊆ ℕ×ℕ", te, !VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test(timeout = 3000)
	public void testCh7LikeMoreSimpleYet() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();

		doTest("ch7_likeMoreSimpleYet", hyps, "{0 ↦ d} ∈ ({0,1} →  D)", te,
				!VALID);
	}

	/**
	 * 
	 */
	@Test(timeout = 3000)
	// @Ignore("(sets equality)")
	public void testDifferentForall() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForall", hyps, "{1 ↦ {0 ↦ d}} ∈ ({1} → ({0} →  D))",
				te, VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 */
	@Test(timeout = 3000)
	// @Ignore("(sets equality)")
	public void testCh7Conc29() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();

		doTest("ch7_conc29", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test(timeout = 3000)
	public void testBepiColombo3Mini() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Mini", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test(timeout = 3000)
	public void testBepiColombo3Medium() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Medium", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("Takes more than 30 seconds to return a result")
	public void testBepiColombo3Medium2() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6}");
		hyps.add("TM = ∅");

		doTest("bepi_colombo3Medium2", hyps, "TC ∩ TM = ∅", te, VALID);
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
	public void testBepiColombo3() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1,1 ↦ 2,1 ↦ 7,1 ↦ 8,3 ↦ 25,5 ↦ 1,5 ↦ 2,5 ↦ 3,5 ↦ 4,6 ↦ 6,6 ↦ 10,17 ↦ 2,21 ↦ 3}");

		doTest("bepi_colombo3", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("Re-add when ppTrans updated (sets equality)")
	public void testDynamicStableLSR_081014_15() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "h",
				"ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("h ∈ S →  (S × S →  ℕ)");
		hyps.add("n ∈ dom(h)");
		hyps.add("m ↦ n ∈ dom(h(n))");
		hyps.add("h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("division is uninterpreted, so the verit returned sat")
	// @Ignore("z3 uses the symbol div as division. And it does not have the same properties as in Event-B")
	// @Ignore("division is uninterpreted, so the cvc3 returned sat")
	// @Ignore("division is uninterpreted, so the alt-ergo returned sat")
	@Ignore("Implementation canceled")
	public void testExactDivision() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div";
		doTest(smtFileName + "_1", hyps, "4 ÷ 2 = 2", te, VALID);
		doTest(smtFileName + "_2", hyps, "−4 ÷ 2 = −2", te, VALID);
		doTest(smtFileName + "_3", hyps, "−4 ÷ −2 = 2", te, VALID);
		doTest(smtFileName + "_4", hyps, "4 ÷ −2 = −2", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("Division in veriT does not have the same properties as in Event-B")
	// @Ignore("Division in z3 does not have the same properties as in Event-B")
	// @Ignore("Division in cvc3 does not have the same properties as in Event-B")
	// @Ignore("Division in alt-ergo does not have the same properties as in Event-B")
	@Ignore("Implementation canceled")
	public void testDivisionWithRemainder() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_rem";
		doTest(smtFileName + "_1", hyps, "3 ÷ 2 = 1", te, VALID);
		doTest(smtFileName + "_2", hyps, "−3 ÷ 2 = −1", te, VALID);
		doTest(smtFileName + "_3", hyps, "−3 ÷ −2 = 1", te, VALID);
		doTest(smtFileName + "_4", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("Re-add when ppTrans updated (sets equality)")
	public void testch910_ring_6() {
		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("itv ∈ P → (P → ℙ(P))");
		doTest("ch910_ring_6", hyps, "itv∼;({f} ◁ itv) ⊆ id", te, VALID);
	}

	@Test(timeout = 3000)
	public void testLinearSort29() {
		final ITypeEnvironment te = mTypeEnvironment("f", "ℙ(ℤ × ℤ)", "r",
				"ℙ(ℤ × BOOL)", "m", "ℤ", "x", "ℤ", "j", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("r ∈ 1 ‥ m → BOOL");
		hyps.add("x ∈ 1 ‥ m");
		hyps.add("j+1 ∈ dom(f)");
		doTest("linear_sort_29", hyps, "x ∈ dom(r{f(j+1) ↦ TRUE})", te, VALID);
	}
}
