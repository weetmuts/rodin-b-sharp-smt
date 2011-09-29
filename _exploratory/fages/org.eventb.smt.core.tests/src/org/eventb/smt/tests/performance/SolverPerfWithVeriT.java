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

import static org.eventb.smt.translation.SMTTranslationApproach.USING_VERIT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.translation.SMTLIBVersion;
import org.junit.Test;

public class SolverPerfWithVeriT extends CommonSolverRunTests {
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public SolverPerfWithVeriT(final SMTSolver solver,
			final SMTLIBVersion smtlibVersion) {
		super(solver, smtlibVersion);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_VERIT, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult);
	}

	@Test
	public void testNames() {
		final ITypeEnvironment te = mTypeEnvironment("x", "IN_1");
		final List<String> hyps = Arrays.asList("IN_1 = {x}");
		doTest("namecol", hyps, "(∃ IN_0 ⦂ IN_1 · IN_0 ∈ IN_1)", te, VALID);
	}

	@Test
	public void testRule15RestrictionsAndSubstractions() {
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ");

		final List<String> hyps = Arrays.asList( //
				"(A ◁ AB) = (A ◁ AB)", //
				"(A ◁ AB) = (A ◁ AB)", //
				"(A ⩤ AB) = (A ⩤ AB)", //
				"(AB ▷ A) = (AB ▷ A)");

		doTest("rule15_res_subs", hyps, "(AB ⩥ A) = (AB ⩥ A)", te, VALID);
	}

	@Test
	public void testRule15CartesianProductAndIntegerRange() {
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ", "a", "ℤ");

		final List<String> hyps = Arrays.asList("(AB × AB) = (AB × AB)");

		doTest("rule15_cart_prod_int_range", hyps, "(a ‥ a) = (a ‥ a)", te,
				VALID);
	}

	@Test
	public void testTePlusSort() {
		final List<String> hyps = Arrays.asList("g ∈ e");

		final Set<String> expectedSorts = new HashSet<String>();
		expectedSorts.add("S");

		final Set<String> expectedFuns = new HashSet<String>();
		expectedFuns.add("(g S)");

		final Set<String> expectedPreds = new HashSet<String>(Arrays.asList(
				"(e S)", "(f S)"));

		doTTeTest("tetestSort", hyps, "g ∈ f", pow_te, expectedFuns,
				expectedPreds, expectedSorts);
	}

	@Test
	// @Ignore("Z3: Expected TRUE, but was FALSE")
	public void testRule20() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();

		doTest("rule20", hyps, "{1↦2} ⊂ (λx·x>0 ∣ x+x)", te, VALID);
	}

	@Test
	public void testRule20ManyForalls() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();

		doTest("rule20_many_foralls_verit",
				hyps,
				"((λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) = ∅) ∧ (∀t·(t≥0∨t<0))",
				te, VALID);
	}

	@Test
	public void testRule20MacroInsideMacro() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();

		doTest("rule20_macro_inside_macro", hyps,
				"(λx· (x > 0 ∧ ((λy·y > 0 ∣ y+y) = ∅)) ∣ x+x) = ∅", te, VALID);
	}

	@Test
	public void testSolverCallBelong1() {
		final List<String> hyps = Arrays.asList("g ∈ e");

		// perform test
		doTest("belong_1", hyps, "g ∈ f", pow_te, NOT_VALID);
	}

	// This test is to see if it's handling the U sort ok
	@Test
	public void testSolverCallSimpleUWithVeriT() {
		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = Arrays.asList("a ∈ A");

		// perform test
		doTest("simpleU_verit", hyps, "⊤", te, VALID);
	}

	@Test
	public void testSolverCallSimpleUWithAltErgo() {
		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = Arrays.asList("a ∈ A");

		// perform test
		doTest("simpleU_altergo", hyps, "⊤", te, VALID);
	}

	@Test
	public void testPairSymbolName() {
		final ITypeEnvironment te = mTypeEnvironment("pair", "Pair");

		final List<String> hyps = Arrays.asList();
		doTest("pairName", hyps, "pair = pair", te, VALID);
	}

	@Test
	public void testSolverCallSimpleUWithCVC3() {
		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = Arrays.asList("a ∈ A");

		// perform test
		doTest("simpleU_cvc3", hyps, "⊤", te, VALID);
	}

	@Test
	public void testSolverCallWithVeriT() {
		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < z");

		// perform test
		doTest("with_verit", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithCvc3() {
		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < z");

		// perform test
		doTest("with_cvc3", hyps, "x < z", arith_te, VALID);
	}

	@Test
	// @Ignore("Z3 Error: ERROR: Benchmark constains arithmetic, but QF_UF does not support it.")
	public void testExpn() {
		final List<String> hyps = Arrays.asList();
		doTest("expn", hyps, "x ^ y = z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithZ3() {
		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < z");

		// perform test
		doTest("with_z3", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithAltErgo() {
		final List<String> hyps = Arrays.asList(//
				"x < y", //
				"y < z");

		// perform test
		doTest("with_altergo", hyps, "x < z", arith_te, VALID);
	}

	/**
	 * ch8_circ_arbiter.1 from task 1 (Requirement Analysis) 's Rodin benchmarks
	 * on 'integer' theory
	 */
	@Test
	public void testCh8CircArbiter1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"a1", "ℤ", "r1", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"a1 ≤ r1", //
				"r1 ≤ a1 + 1", //
				"r1 ≠ a1");

		doTest("ch8_circ_arbiter1", hyps, "r1 = a1 + 1", te, VALID);
	}

	/**
	 * quick_sort.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_arith' theory
	 */
	@Test
	public void testQuickSort1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"k", "ℤ", "n", "ℤ", "x", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"(k ≥ 1) ∧ (k ≤ n)", //
				"(x ≥ 1) ∧ (x ≤ n − 1)", //
				"¬ ((x ≥ 1) ∧ (x ≤ k − 1))", //
				"¬ ((x ≥ k + 1) ∧ (x ≤ n − 1))");

		doTest("quick_sort1", hyps, "x = k", te, VALID);
	}

	/**
	 * The test below has several problems:
	 * <p>
	 * <ol>
	 * <li>On veriT's side, the parser for SMT-LIB 1.2 with extensions is not
	 * robust enough: here it should emit a more meaningful error message.</li>
	 * 
	 * <li>On the PO side, there are several problems:
	 * 
	 * <ol>
	 * <li>The definition of macro pfun uses macros rel and tfun. Again, the
	 * parser does not support macros within macro-definitions. So you have to
	 * do the expansion and reductions manually for all the macros in the paper
	 * where this happens. Here we would have: (pfun (lambda (?x13 ('s Bool))
	 * (?y0 ('t Bool)) . (lambda (?r4 ((Pair 's 't) Bool)) . (and (forall (?p12
	 * (Pair 's 't)) . (implies (?r4 ?p12) (and (?x13 (fst ?p12)) (?y0 (snd
	 * ?p12))))) (forall (?pt (Pair 's 't)) (?p0 (Pair 's 't)) . (implies (and
	 * (?r4 ?pt) (?r4 ?p0)) (implies (= (fst ?pt) (fst ?p0))(= (snd ?pt) (snd
	 * ?p0)))))))))</li>
	 * 
	 * <li>The formula that is to be verified is: (not (= (pfun Int D_0) (pfun
	 * Int D_0))) Since (pfun Int D_0) is the set of all partial functions from
	 * Int to D_0, this is not a first-order logic formula; veriT emits the
	 * following error message: error : pre_process: results is not FOL Note
	 * that, in that case, it would be possible to rewrite the formula to an
	 * equi-satisfiable first-order logic formula using skolemization of
	 * function variables. This is again currently not implemented in veriT.</li>
	 * </ol>
	 * </ol>
	 * 
	 */
	@Test
	// @Ignore("This test has many problems. See the text above")
	public void testIntInRelation() {
		final ITypeEnvironment te = mTypeEnvironment("D", "ℙ(D)");

		final List<String> hyps = Arrays.asList();

		doTest("int_in_relation", hyps, "ℤ ⇸  D = ℤ ⇸  D", te, VALID);
	}

	/**
	 * ch915_bin.10 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'nonlinear_arith' theory
	 */
	@Test
	// @Ignore("Division is uninterpreted, so the solver is returning sat")
	public void testCh915Bin10() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ");

		final List<String> hyps = Arrays.asList("n ≥ 1");
		doTest("ch915_bin10", hyps, "1 ≤ (n+1) ÷ 2", te, VALID);
	}

	/**
	 * bosch_switch.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'linear_order_int' theory
	 */
	@Test
	public void testBoschSwitch1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"i", "ℤ", "t", "ℤ", "t0", "ℤ");

		// QF_LIA

		final List<String> hyps = Arrays.asList(//
				"t ≥ 0", //
				"t0 ≥ 0", //
				"t0 < t", //
				"(i ≥ t0) ∧ (i ≤ t)");

		doTest("bosch_switch1", hyps, "i ≥ 0", te, VALID);
	}

	/**
	 * bepi_colombo.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'basic_set' theory
	 */
	@Test
	public void testBepiColombo1() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "a", "S", "b", "S", "c", "S");

		final List<String> hyps = Arrays.asList(//
				"S={a,b,c}", //
				"¬ a=b", //
				"¬ b=c", //
				"¬ c=a");

		doTest("bepi_colombo1", hyps, "{a,b,c} = {c,a,b}", te, VALID);
	}

	@Test
	// @Ignore("error : pre_process: results is not FOL")
	public void testSubSet() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "a", "S", "b", "S");

		final List<String> hyps = Arrays.asList(//
				"S={a,b}", //
				"¬ a=b");

		doTest("subset", hyps, "{a} ⊂ {a,b}", te, VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test
	// Z3: ERROR: Benchmark contains uninterpreted function symbols, but QF_LIA
	// does not support them.
	public void testCh7LikeMoreSimpleYet() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = Arrays.asList();

		doTest("ch7_likeMoreSimpleYet", hyps, "{0 ↦ d} ∈ ({0,1} →  D)", te,
				!VALID);
	}

	@Test
	// @Ignore("Z3: Expected true, but returned false")
	public void testBepiColombo3Mini() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList(//
				"TC = {3 ↦ 5}", //
				"TM = {1 ↦ 1}");

		doTest("bepi_colombo3Mini", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test(timeout = 3000)
	// @Ignore("Z3: Expected true, but it was false")
	public void testBepiColombo3Medium() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}", //
						"TM = {1 ↦ 1}");

		doTest("bepi_colombo3Medium", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	public void testBepiColombo3Medium2() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6}", //
						"TM = ∅");

		doTest("bepi_colombo3Medium2", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	// ERROR: Benchmark contains uninterpreted function symbols, but QF_LIA does
	// not support them.
	public void testRelation() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "{ 1 ↦ 2 } ∈ { 1 } ↔ { 2 }", te, VALID);
	}

	/**
	 * bepi_colombo.3 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'basic_relation' theory
	 */
	@Test(timeout = 3000)
	// @Ignore("Z3: Expected true, but it was false")
	public void testBepiColombo3() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = Arrays
				.asList("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}", //
						"TM = {1 ↦ 1,1 ↦ 2,1 ↦ 7,1 ↦ 8,3 ↦ 25,5 ↦ 1,5 ↦ 2,5 ↦ 3,5 ↦ 4,6 ↦ 6,6 ↦ 10,17 ↦ 2,21 ↦ 3}");

		doTest("bepi_colombo3", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	public void testExistsRule17() {
		final ITypeEnvironment te = mTypeEnvironment("s", "ℙ(R)");

		final List<String> hyps = Arrays.asList("∃x·x∈s");

		doTest("rule17_exists", hyps, "∃x,y·x∈s∧y∈s", te, VALID);
	}

	@Test
	public void testForallRule17() {
		final ITypeEnvironment te = mTypeEnvironment("s", "ℙ(R)");

		final List<String> hyps = Arrays.asList(//
				"∀x·x∈s", //
				"∀x,y·x∈s∧y∈s");

		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", te);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(Formula.FORALL, bids,
				base.getPredicate(), null);

		doTest("rule17_forall", hyps, p.toString(), te, VALID);

	}

	@Test
	public void testRule16NotEqual() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "b", "ℤ", "c", "ℤ", "a", "ℤ");

		final List<String> hyps = Arrays.asList("(a = 1) ∧ (b = 2)");

		doTest("rule16_not_equal", hyps, "(a ∗ b ≠ 0)", te, VALID);
	}

	@Test
	public void testRule15RelationOverridingCompANdComposition() {
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ");

		final List<String> hyps = Arrays
				.asList("(AB \ue103 AB) = (AB \ue103 AB)");

		doTest("rule15_ovr_fcomp", hyps, "(AB \u003b AB) = (AB \u003b AB)", te,
				VALID);
	}

	@Test
	public void testRule15BackwardComposition() {
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("rule15_bcomp", hyps, "(AB \u2218 AB) = (AB \u2218 AB)", te,
				VALID);
	}

	public void testRule18() {
		final ITypeEnvironment te = mTypeEnvironment("a", "ℤ", "b", "ℤ", "A",
				"ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}");

		doTest("rule18", hyps, "{a∣a ≥ 0} ⊂ A", te, VALID);
	}

	@Test
	public void testRule22and23() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList("min({2,3}) = min({2,3})");

		doTest("rule22_23", hyps, "max({2,3}) = max({2,3})", te, VALID);
	}

	@Test
	// @Ignore("Z3: Expected true, but it was false")
	public void testRule24() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("rule24", hyps, "finite({1,2,3})", te, VALID);
	}

	@Test(timeout = 3000)
	public void testCard() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("rule25", hyps, "card({1,2,3}) = card({1,2,3})", te, VALID);
	}

	@Test
	public void testPred() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		doTest("testpred", hyps, "2 ↦ 1 ∈ pred", te, VALID);
	}

	@Test
	// Expected <true> but was <false>
	public void testPredSet() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		doTest("testpredset", hyps, "{2 ↦ 1} ⊂ pred", te, VALID);
	}

	@Test
	public void testSucc() {
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = Arrays.asList();
		doTest("testsucc", hyps, "1 ↦ 2 ∈ succ", te, VALID);
	}

	@Test
	// error : pre_process: result is not FOL
	public void testDistinctForSingleton() {
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = Arrays.asList("A = {1,2,3}");
		doTest("distinct_singletons", hyps, "partition(A,{1},{2},{3})", te,
				VALID);
	}

	@Test
	public void testDistinct() {
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = Arrays.asList("A = {1,2,3,4}");
		doTest("distincttest", hyps, "partition(A,{1,2},{4},{3})", te, VALID);
	}

	@Test
	public void testSimplerDistinct() {
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = Arrays.asList("A = {1,2,3}");
		doTest("distinctsimpletest", hyps, "partition(A,{1,2},{3})", te, VALID);
	}

	@Test
	public void testIntSet() {
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = Arrays.asList();
		doTest("intsettest", hyps, "A ⊆ ℤ", te, VALID);
	}

	@Test
	// @Ignore("Lemmas cannot be SMT reserved names")
	public void testExists() {
		final ITypeEnvironment te = mTypeEnvironment("x", "T");
		final List<String> hyps = Arrays.asList("T = {x}");
		doTest("exists", hyps, "(∃ z ⦂ T · z ∈ T)", te, VALID);
	}

	@Test
	// Function application (FUNIMAGE) is not implemented yet
	public void testLinearSort29() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"f", "ℙ(ℤ × ℤ)", "r", "ℙ(ℤ × BOOL)", //
				"m", "ℤ", "x", "ℤ", "j", "ℤ");
		final List<String> hyps = Arrays.asList(//
				"r ∈ 1 ‥ m → BOOL", //
				"x ∈ 1 ‥ m", //
				"j+1 ∈ dom(f)");
		doTest("linear_sort_29", hyps, "x ∈ dom(r{f(j+1) ↦ TRUE})", te, VALID);
	}

	@Test
	public void testDynamicStableLSR_081014_20() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "P", "ℙ(S × S)", "Q", "ℙ(S × S)",//
				"k", "S × S", "m", "S", "n", "S");
		final List<String> hyps = Arrays.asList(//
				"¬ k = m ↦ n", //
				"k ∈ P ∪ {m ↦ n} ∪ (Q ∖ {m ↦ n})");
		doTest("dynamicStableLSR_081014_20", hyps, "k ∈ P ∪ Q", te, VALID);
	}

	@Test
	// Expected <True> but was <False>
	public void testBepiColombo6() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "R", "ℙ(R)",
				"a", "S", "b", "S", "c", "S", "d", "S", "e", "S", "m", "R",
				"x", "R", "y", "R", "z", "R");

		final List<String> hyps = Arrays.asList(//
				"¬ a=b", //
				"¬ a=c", //
				"¬ a=d", //
				"¬ a=e", //
				"¬ b=c", //
				"¬ b=d", //
				"¬ b=e", //
				"¬ c=d", //
				"¬ c=e", //
				"¬ d=e", //
				"¬ x=y", //
				"¬ y=z", //
				"¬ z=x", //
				"S={a,c,d,b,e}", //
				"R={x,y,z}", //
				"m ∈ {x,y,z}");

		doTest("bepicombo_6", hyps,
				"m ∈ {x} ∪ dom({y} × {a,c,e}) ∪ dom({z} × {e,c,d,b,e})", te,
				VALID);
	}

	@Test
	// @Ignore("Function ?DOM_0 is not declared")
	public void testBepiColombo6_2() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "R", "ℙ(R)",
				"a", "S", "b", "S", "c", "S", "d", "S", "e", "S", "m", "R",
				"x", "R", "y", "R", "z", "R");

		final List<String> hyps = Arrays.asList();
		doTest("bepicombo_6_2", hyps,
				"dom({y} × {a,c,e}) = dom({y} × {a,c,e})", te, VALID);
	}

	@Test
	// Expected <True> but was <False>
	public void testBepiColombo6Parte_3() {
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "R", "ℙ(R)",
				"a", "S", "b", "S", "c", "S", "d", "S", "e", "S", "m", "R",
				"x", "R", "y", "R", "z", "R");

		final List<String> hyps = Arrays.asList();
		doTest("bepicombo_6_3", hyps, "dom({y} × {a,c,e}) = {y}", te, VALID);
	}

	@Test
	// @Ignore("Z3 ERROR: Benchmark contains uninterpreted function symbols, but QF_LIA does not support them.")
	public void testSubseteqMapsto() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("subseteqMapsto", hyps, "{0↦1,1↦2} ⊆ {0↦1,1↦2,2↦3}", te, VALID);
	}

	@Test
	public void testSubseteq2() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("subseteq2", hyps, "{1↦2} ⊆ {1↦2}", te, VALID);
	}

	@Test
	// @Ignore("Z3: Expected TRUE, but was FALSE")
	public void testSubsetMapsto1() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("subsetmapsto1", hyps, "{0↦1,1↦2} ⊂ {0↦1,1↦2,2↦3}", te, VALID);
	}

	@Test
	// @Ignore("Z3: Expected TRUE, but was FALSE")
	public void testSubsetMapsto2() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("subsetmapsto2", hyps, "{0↦1} ⊂ {0↦1,1↦2,2↦3}", te, VALID);
	}

	@Test
	// @Ignore("Z3: Expected TRUE, but was FALSE")
	public void testNotEqualMapsto() {
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();
		doTest("notequalmapsto", hyps, "{0↦1} ≠ {0↦1,1↦2,2↦3}", te, VALID);
	}

	@Test
	// @Ignore("Z3: Expected TRUE, but was FALSE")
	public void testBOOLSet() {
		final List<String> hyps = Arrays.asList(//
				"b↦c ∈ BOOL×BOOL", //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set_2", hyps, "b = TRUE", arith_te, VALID);
	}
}
