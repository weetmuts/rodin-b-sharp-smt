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

import static org.eventb.smt.core.provers.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.provers.SolverKind.CVC3;
import static org.eventb.smt.core.provers.SolverKind.CVC4;
import static org.eventb.smt.core.provers.SolverKind.MATHSAT5;
import static org.eventb.smt.core.provers.SolverKind.OPENSMT;
import static org.eventb.smt.core.provers.SolverKind.UNKNOWN;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.provers.SolverKind.Z3;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.smt.core.provers.SolverKind;
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
		super(UNKNOWN, null, USING_VERIT, V2_0, !GET_UNSAT_CORE);
	}

	private void setSolverPreferences(final SolverKind kind) {
		setSolverPreferences(kind, USING_VERIT, V2_0);
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(lemmaName, inputHyps, inputGoal, te, !TRIVIAL,
				expectedSolverResult);
	}

	@Test
	@Ignore("alt-ergo fails")
	// TODO: See the problem
	public void testUnsatAltErgoCall() {
		setSolverPreferences(ALT_ERGO);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatAltErgoCall() {
		setSolverPreferences(ALT_ERGO);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("altergo_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("CVC3 fails")
	// TODO: See the problem
	public void testUnsatCvc3Call() {
		setSolverPreferences(CVC3);

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
		setSolverPreferences(CVC3);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("CVC4 needs a known logic to be set")
	public void testUnsatCvc4Call() {
		setSolverPreferences(CVC4);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore("See the problem")
	// TODO: See the problem
	public void testSatCvc4Call() {
		setSolverPreferences(CVC4);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("cvc4_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("MathSat5 is not well integrated because it can read on its input only")
	public void testUnsatMathSat5Call() {
		setSolverPreferences(MATHSAT5);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testSatMathSat5Call() {
		setSolverPreferences(MATHSAT5);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("mathsat5_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("Unexpected error when executed in the test suite")
	// TODO look for the error
	public void testUnsatOpenSMTCall() {
		setSolverPreferences(OPENSMT);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_unsat", hyps, "x < z", arith_te, !VALID);
	}

	@Test
	@Ignore("OpenSMT is not compatible with SMT-LIB 1.2")
	public void testSatOpenSMTCall() {
		setSolverPreferences(OPENSMT);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore("Problem related to sort veriT_TPTP produced after pre-processing.")
	public void testUnsatVeriTCall() {
		setSolverPreferences(VERIT);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("verit_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatVeritCall() {
		setSolverPreferences(VERIT);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("verit_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testUnsatZ3Call() {
		setSolverPreferences(Z3);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testSatZ3Call() {
		setSolverPreferences(Z3);

		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("z3_sat", hyps, "x > z", arith_te, !VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredZ3Call() {
		setSolverPreferences(Z3);

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredCVC3Call() {
		setSolverPreferences(CVC3);

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore()
	// TODO: See the problem
	public void testTRUEPredAltErgoCall() {
		setSolverPreferences(ALT_ERGO);

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore("Sent to harvey repository for tests")
	public void testTRUEPredVeriTCall() {
		setSolverPreferences(VERIT);

		final List<String> hyps = Arrays.asList( //
				"b = TRUE", //
				"c ≠ FALSE");

		doTest("true_pred_unsat_verit", hyps, "b = c", arith_te, VALID);
	}

	@Test
	@Ignore("Cartesian product not implemented yet")
	public void testBOOLSetZ3Call() {
		setSolverPreferences(Z3);

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Cartesian product not implemented yet")
	public void testBOOLSetAltErgoCall() {
		setSolverPreferences(ALT_ERGO);

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testBOOLSetVeriTCall() {
		setSolverPreferences(VERIT);

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Syntax error in declaration: (declare-fun (par (s t) (pair s t (Pair s t))))")
	public void testBOOLSetCVC3Call() {
		setSolverPreferences(CVC3);

		final List<String> hyps = Arrays.asList( //
				"b↦c = TRUE↦FALSE");

		doTest("test_bool_set", hyps, "b↦c ∈ BOOL×BOOL", arith_te, VALID);
	}

	@Test
	@Ignore("Fail")
	public void testSetsEqualityZ3Call() {
		setSolverPreferences(Z3);

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
		setSolverPreferences(CVC3);

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
		setSolverPreferences(ALT_ERGO);

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
		setSolverPreferences(VERIT);

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
		setSolverPreferences(Z3);

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
		setSolverPreferences(CVC3);

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
		setSolverPreferences(ALT_ERGO);

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
		setSolverPreferences(VERIT);

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
		setSolverPreferences(Z3);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationCVC3Call() {
		setSolverPreferences(CVC3);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationAltErgoCall() {
		setSolverPreferences(ALT_ERGO);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testExponentiationVeriTCall() {
		setSolverPreferences(VERIT);

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = Arrays.asList();

		doTest("exponentiation", hyps, "2 ^ 2=4", te, VALID);
	}

	@Test
	@Ignore("Implementation canceled")
	public void testModZ3Call() {
		setSolverPreferences(Z3);

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
		setSolverPreferences(VERIT);

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
		setSolverPreferences(ALT_ERGO);

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
		setSolverPreferences(CVC3);

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
		setSolverPreferences(Z3);

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
		setSolverPreferences(VERIT);

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
		setSolverPreferences(CVC3);

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
		setSolverPreferences(ALT_ERGO);

		final ITypeEnvironment te = mTypeEnvironment(//
				"n", "ℤ", "x", "ℤ");

		final List<String> hyps = Arrays.asList( //
				"n = 2", //
				"x = −5");

		doTest("integer_set", hyps, "{n↦x} ⊂ ℤ×ℤ", te, VALID);
	}
}