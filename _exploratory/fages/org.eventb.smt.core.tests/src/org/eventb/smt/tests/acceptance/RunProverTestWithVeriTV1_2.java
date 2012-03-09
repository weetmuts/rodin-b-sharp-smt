/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *  UFRN - additional tests
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.internal.provers.core.SMTSolver.UNKNOWN;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.internal.translation.SMTTranslationApproach.USING_VERIT;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class contains acceptance tests of the plugin using veriT preprocessing.
 * 
 * 
 * 
 */
public class RunProverTestWithVeriTV1_2 extends CommonSolverRunTests {
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public RunProverTestWithVeriTV1_2() {
		super(UNKNOWN, null, V1_2, !GET_UNSAT_CORE);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_VERIT, lemmaName, inputHyps, inputGoal, te, !TRIVIAL,
				expectedSolverResult);
	}

	@Test
	public void testUnsatAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	public void testUnsatCvc3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatCvc3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("CVC4 needs a known logic to be set")
	public void testUnsatCvc4Call() {
		setPreferencesForCvc4Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatCvc4Call() {
		setPreferencesForCvc4Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("MathSat5 is not well integrated because it can read on its input only")
	public void testUnsatMathSat5Call() {
		setPreferencesForMathSat5Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatMathSat5Call() {
		setPreferencesForMathSat5Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("OpenSMT is not compatible with SMT-LIB 1.2")
	public void testUnsatOpenSMTCall() {
		setPreferencesForOpenSMTTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_unsat", hyps, "x < z", arith_te, !VALID);
	}

	@Test
	@Ignore("OpenSMT is not compatible with SMT-LIB 1.2")
	public void testSatOpenSMTCall() {
		setPreferencesForOpenSMTTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	public void testUnsatVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("verit_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatVeritCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("verit_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	public void testUnsatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("Fail")
	public void testTRUEPredZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void testTRUEPredCVC3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void testTRUEPredAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore("wrong result")
	// FIXME
	public void testTRUEPredVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	// FIXME
	@Ignore()
	public void testBOOLSetZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore()
	// FIXME
	public void testBOOLSetAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore()
	// FIXME
	public void testBOOLSetVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore()
	// FIXME
	public void testBOOLSetCVC3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testSetsEqualityZ3Call() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testSetsEqualityCVC3Call() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testSetsEqualityAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testSetsEqualityVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment("p", "ℙ(ℤ)", "q", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList( //
				"p ∈ ℙ({1})", //
				"p ≠ ∅", //
				"q ∈ ℙ({1})", //
				"q ≠ ∅");

		doTest("SetsEquality", hyps, "p = q", te, VALID);
	}

	@Test
	public void testDivisionZ3Call() {
		setPreferencesForZ3Test();

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
	public void testDivisionCVC3Call() {
		setPreferencesForCvc3Test();

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
	public void testDivisionAltErgoCall() {
		setPreferencesForAltErgoTest();

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
	public void testDivisionVeriT() {
		setPreferencesForVeriTTest();

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
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationCVC3Call() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testModZ3Call() {
		setPreferencesForZ3Test();

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
	@Ignore("Implementation canceled")
	public void testModVeriTCall() {
		setPreferencesForVeriTTest();

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
	@Ignore("Implementation canceled")
	public void testModAltErgoCall() {
		setPreferencesForAltErgoTest();

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
	@Ignore("Implementation canceled")
	public void testModForCVC3Call() {
		setPreferencesForCvc3Test();

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
	public void testIntegerSetZ3Call() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetForCVC3Call() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	public void testUnionForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "Sb", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "(X ∪ Sb) = (X ∪ Sb)", te, VALID);
	}

	@Test
	public void testSetMembershipForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ");

		final List<String> hyps = Arrays.asList("X = {1}", "a = 1");

		doTest("membership", hyps, "a ∈ X", te, VALID);
	}

	@Test
	public void testRelationAltErgocall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "a ∈ ℤ↔ℤ", te, VALID);
	}

	@Test
	public void testCardinality() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "card({1}) = 1", te, VALID);
	}

	@Test
	public void testImplies() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "0 = 0 ⇒ 0 = 0", te, VALID);
	}

	@Test
	public void testRange() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "1 ∈ 1‥2 ", te, VALID);
	}

	@Test
	public void testSubseteq() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("subseteq", hyps, "{1} ⊆ ℕ", te, VALID);
	}

	@Test
	public void testSubset() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("subsetV1_2", hyps, "{1} ⊂ {1,2}", te, VALID);
	}

	@Test
	public void testSetMinus() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("A = B");

		doTest("relation", hyps, "A ∖ B = ∅", te, VALID);
	}

	@Test
	public void testBools() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "TRUE ∈ BOOL", te, VALID);
	}

	@Test
	public void testPair() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("pair", hyps, "1↦1 ∈ {1↦1,1↦2}", te, VALID);
	}

	@Test
	public void testIsMin() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismin_1_2", hyps, "1 = min({1,2})", te, VALID);
	}

	@Test
	public void testIsMax() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismax_1_2", hyps, "2 = max({1,2})", te, VALID);
	}

	@Test
	public void testSetMembershipForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ");

		final List<String> hyps = Arrays.asList("X = {1}", "a = 1");

		doTest("setmembership_verit", hyps, "a ∈ X", te, VALID);
	}

	@Test
	@Ignore("error : Sort PairInt'_19 and Int mismatch")
	public void testTotalRelationForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_relation_verit_1_2", hyps, "A \ue100 B = A \ue100 B", te,
				VALID);
	}

	@Test
	@Ignore("VeriT: Time exceeded.")
	public void testTotalFunctionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("A={1}");

		doTest("total_function_verit_1_2", hyps, "¬({2 ↦ 2} ∈ A \u2192 A)", te,
				VALID);
	}

	@Test
	@Ignore("Result: unknown")
	public void testInverseForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℤ ↔ ℤ");

		final List<String> hyps = Arrays.asList("A={1↦1}");

		doTest("inverse_verit_1_2", hyps, "A = (A)∼", te, VALID);
	}

	@Test
	@Ignore("Pre-processing: Segmentation fault")
	public void testPartialInjectionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("partial_injection_verit_1_2", hyps, "A \u2914 B = A \u2914 B",
				te, VALID);
	}

	@Test
	@Ignore("Pre-processing: Segmentation fault")
	public void testTotalInjectionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_injection_verit_1_2", hyps, "A \u21a3 B = A \u21a3 B",
				te, VALID);
	}

	@Test
	@Ignore("error : Sort Pair'_19Int and Int mismatch.")
	public void testSurjectiveRelationForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("surjective_relation_verit_1_2", hyps,
				"A \ue101 B = A \ue101 B", te, VALID);
	}

	@Test
	@Ignore("Pre-processing: Segmentation fault")
	public void testPartialSurjectionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("partial_surjection_verit_1_2", hyps, "A \u2900 B = A \u2900 B",
				te, VALID);
	}

	@Test
	@Ignore("Pre-processing: Segmentation fault")
	public void testTotalSurjectionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_surjection_verit_1_2", hyps, "A \u21a0 B = A \u21a0 B",
				te, VALID);
	}

	@Test
	@Ignore("Pre-processing: Segmentation fault")
	public void testTotalBijectionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_bijection_verit_1_2", hyps, "A \u2916 B = A \u2916 B",
				te, VALID);
	}

	@Test
	@Ignore("error : Sort Pair'_19Int and Int mismatch.")
	public void testTotalSurjectiveRelationForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_surjective_relation_verit_1_2", hyps,
				"A \ue102 B = A \ue102 B", te, VALID);
	}

	@Test
	@Ignore("error : Time exceeded")
	public void testPredecessorForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("predecessor_verit_1_2", hyps, "{2 ↦ 1} ⊂ pred", te, VALID);
	}

	@Test
	@Ignore("error : Time exceeded")
	public void testSucessorForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("sucessor_verit_1_2", hyps, "{1 ↦ 2} ⊂ succ", te, VALID);
	}

	@Test
	// TODO Check translation of finite
	@Ignore("Expected true, but it was false.")
	public void testFiniteForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("finite_verit_1_2", hyps, "finite({1})", te, VALID);
	}

	@Test
	public void testRangeForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("range_verit_1_2", hyps, "2 ∈ ran({1↦2})", te, VALID);
	}

	@Test
	public void testDomForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("dom_verit_1_2", hyps, "2 ∈ dom({2↦1})", te, VALID);
	}

	@Test
	@Ignore("error : Time exceeded")
	public void testPartialFunctionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("A={1}");

		doTest("partial_function_verit_1_2", hyps, "¬({2 ↦ 2} ∈ A \u2192 A)",
				te, VALID);
	}

	@Test
	public void testRelationOverrideForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℤ↔ℤ", "B", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList("A={1↦1}", "B={1↦1}");

		doTest("relation_override_verit_1_2", hyps, "A \ue103 A = B", te, VALID);
	}

	@Test
	@Ignore("error : Assert DAG_sort_binding(DAG_sort(DAG)) failed (tstp-print.c:54)")
	public void testIDForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℤ↔ℤ", "B", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList("A={1↦1}");

		doTest("id_verit_1_2", hyps, "A ⊂ id", te, VALID);
	}
}