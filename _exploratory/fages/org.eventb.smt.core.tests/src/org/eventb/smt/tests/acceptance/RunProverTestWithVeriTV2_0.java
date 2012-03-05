/*******************************************************************************
 * Copyright (c) 2011, 2012 UFRN. All rights reserved.
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
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;
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
 * @author Vitor Alcantara
 * 
 *         TODO: To check if each test is according to SMT 2.0
 * 
 */
public class RunProverTestWithVeriTV2_0 extends CommonSolverRunTests {

	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public RunProverTestWithVeriTV2_0() {
		super(UNKNOWN, null, V2_0, !GET_UNSAT_CORE);
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
	@Ignore("CVC3 fails")
	// TODO: See the problem
	public void testUnsatCvc3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore("CVC3 fails")
	// TODO: See the problem
	// FIXME: The Int sort must be declared in some theory in veriT approach.
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
	@Ignore("See the problem")
	// TODO: See the problem
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
	@Ignore()
	// TODO: See the problem
	public void testSatMathSat5Call() {
		setPreferencesForMathSat5Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
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
	@Ignore("Problem related to sort veriT_TPTP produced after pre-processing.")
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
	@Ignore()
	// TODO: See the problem
	public void testUnsatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testSatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredCVC3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore("Sent to harvey repository for tests")
	public void testTRUEPredVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat_verit", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore("Cartesian product not implemented yet")
	public void testBOOLSetZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Cartesian product not implemented yet")
	public void testBOOLSetAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testBOOLSetVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
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
	@Ignore("Implementation cancelled")
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
	@Ignore("Implementation cancelled")
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
	@Ignore("Implementation cancelled")
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
	@Ignore("Implementation cancelled")
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
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testIntegerSetVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set_verit", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
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
	@Ignore("Alt-Ergo fails")
	public void testUnionForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "Sb", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set_alt_ergo", hyps, "(X ∪ Sb) = (X ∪ Sb)", te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testUnionForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "Sb", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set_verit", hyps, "(X ∪ Sb) = (X ∪ Sb)", te, VALID);
	}

	@Test
	@Ignore("Alt-Ergo fails")
	public void testInterForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "Sb", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("intersection_altergo", hyps, "(X ∩ Sb) = (X ∩ Sb)", te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testInterForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "Sb", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("intersection_verit", hyps, "(X ∩ Sb) = (X ∩ Sb)", te, VALID);
	}

	@Test
	@Ignore("Alt-Ergo fails")
	public void testSetMembershipForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ");

		final List<String> hyps = Arrays.asList("X = {1}", "a = 1");

		doTest("setmembership_alt_ergo", hyps, "a ∈ X", te, VALID);
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
	@Ignore("Sets of pairs not implemented yet")
	public void testPairSetForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℤ↔ℤ", "a", "ℤ");

		final List<String> hyps = Arrays.asList("X = {1↦2}");

		doTest("integer_set", hyps, "∅ ≠ X", te, VALID);
	}

	@Test
	public void testEmptySetForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ");

		final List<String> hyps = Arrays.asList("X = {}", "a = 1");

		doTest("integer_set", hyps, "¬(a ∈ X)", te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testRelationAltErgocall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "a ∈ ℤ↔ℤ", te, VALID);
	}

	@Test
	@Ignore("pre-proc error: undefined sort and on line 11")
	// SMT file sent to benchmark repository.
	public void testCardinalityForAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("card_alt_ergo_2_0", hyps, "card({1}) = 1", te, VALID);
	}

	@Test
	@Ignore("pre-proc error: undefined sort and on line 11")
	// SMT file sent to benchmark repository.
	public void testCardinalityForVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"X", "ℙ(ℤ)", "a", "ℤ↔ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("card_verit_2_0", hyps, "card({1}) = 1", te, VALID);
	}

	@Test
	public void testRangeInteger() {
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

		doTest("relation", hyps, "{1} ⊆ ℕ", te, VALID);
	}

	@Test
	public void testNat1() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("relation", hyps, "{2} ⊆ ℕ1", te, VALID);
	}

	@Test
	public void testSubset() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("subsetV2_0", hyps, "{1} ⊂ {1,2}", te, VALID);
	}

	@Test
	public void testSetMinusAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("A = B");

		doTest("setminus_altergo", hyps, "A ∖ B = ∅", te, VALID);
	}

	@Test
	// @Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testSetMinusVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList("A = B");

		doTest("setminus_altergo", hyps, "A ∖ B = ∅", te, VALID);
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
	@Ignore("Alt-Ergo fails")
	public void testPairAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("pair_alt_ergo", hyps, "1↦1 ∈ {1↦1,1↦2}", te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testPairVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("pair_verit", hyps, "1↦1 ∈ {1↦1,1↦2}", te, VALID);
	}

	@Test
	@Ignore("Alt-Ergo fails")
	public void testIsMinAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismin_2_0_alt_ergo", hyps, "1 = min({1,2})", te, VALID);
	}

	@Test
	public void testIsMinVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismin_2_0_verit", hyps, "1 = min({1,2})", te, VALID);
	}

	@Test
	@Ignore("Alt-Ergo fails")
	public void testIsMaxAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismax_2_0", hyps, "2 = max({1,2})", te, VALID);
	}

	@Test
	public void testIsMaxVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
		);

		final List<String> hyps = Arrays.asList();

		doTest("ismax_2_0", hyps, "2 = max({1,2})", te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testTotalRelationForVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"A", "ℙ(ℤ)", "B", "ℙ(ℤ)");

		final List<String> hyps = Arrays.asList();

		doTest("total_relation_verit_2_0", hyps, "A \ue100 B = A \ue100 B", te,
				VALID);
	}

}