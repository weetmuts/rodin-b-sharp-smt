package fr.systerel.smt.provers.core.tests.acceptance;

import static br.ufrn.smt.solver.translation.SMTTranslationApproach.USING_PP;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.junit.Test;

import fr.systerel.smt.provers.core.tests.CommonSolverRunTests;

/**
 * This class contains acceptance tests of the plugin with pptranslation.
 * 
 * @author Yoann Guyot
 * 
 */
public class RunProverTestWithPP extends CommonSolverRunTests {
	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		doTest(USING_PP, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult);
	}

	@Test
	public void testUnsatAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("altergo_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatAltErgoCall() {
		setPreferencesForAltErgoTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("altergo_sat", hyps, "x > z", arith_te, NOT_VALID);
	}

	@Test
	public void testUnsatCvc3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("cvc3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatCvc3Call() {
		setPreferencesForCvc3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("cvc3_sat", hyps, "x > z", arith_te, NOT_VALID);
	}

	@Test
	public void testUnsatVeriTCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("verit_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatVeritCall() {
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("verit_sat", hyps, "x > z", arith_te, NOT_VALID);
	}

	@Test
	public void testUnsatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("z3_unsat", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSatZ3Call() {
		setPreferencesForZ3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		doTest("z3_sat", hyps, "x > z", arith_te, NOT_VALID);
	}
}