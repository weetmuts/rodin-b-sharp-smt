/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.acceptance;

import static org.eventb.smt.provers.internal.core.SMTSolver.UNKNOWN;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.SMTTranslationApproach.USING_PP;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class contains acceptance tests of the plugin with pptranslation.
 * 
 * @author Yoann Guyot
 * 
 */
public class RunProverTestWithPPV2_0 extends CommonSolverRunTests {
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	public RunProverTestWithPPV2_0() {
		super(UNKNOWN, null, V2_0, !GET_UNSAT_CORE);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_PP, lemmaName, inputHyps, inputGoal, te,
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
	@Ignore("OpenSMT needs a known logic to be set")
	public void testUnsatOpenSMTCall() {
		setPreferencesForOpenSMTTest();

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
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
	public void testTRUEPredVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	public void testBOOLSetZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	public void testBOOLSetZ3Call2() {
		setPreferencesForZ3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c ∈ BOOL×BOOL", //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set2", hyps, "b = TRUE", arith_te, VALID);
	}

	@Test
	public void testBOOLSetAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	public void testBOOLSetVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	public void testBOOLSetCVC3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
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
	@Ignore("Implementation canceled")
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
	@Ignore("Implementation canceled")
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
	@Ignore("Implementation canceled")
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
	@Ignore("Implementation canceled")
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
	@Ignore("Fail (It was successfull because of a bug in the mbqi)")
	public void testIntegerSetZ3Call() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetVeriTCall() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetForCVC3Call() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testIntegerSetForAltErgoCall() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList();

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}
}