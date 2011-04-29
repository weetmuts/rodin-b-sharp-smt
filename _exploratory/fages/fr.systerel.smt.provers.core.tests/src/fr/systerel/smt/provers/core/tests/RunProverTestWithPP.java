package fr.systerel.smt.provers.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.internal.core.SmtProverCall;

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

	@BeforeClass
	public static void cleanSMTFolder() {
		if (CommonSolverRunTests.CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST) {
			CommonSolverRunTests.smtFolder = SmtProverCall
					.mkTranslationDir(CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST);
		}
	}

	/**
	 * Parses the given sequent in the given type environment and launch the
	 * test with the such produced 'Predicate' instances
	 * 
	 * @param inputHyps
	 *            list of the sequent hypothesis written in Event-B syntax
	 * @param inputGoal
	 *            the sequent goal written in Event-B syntax
	 * @param te
	 *            the given type environment
	 * @param expectedSolverResult
	 *            the result expected to be produced by the solver call
	 */
	private void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(lemmaName, hypotheses, goal, expectedSolverResult);
	}

	/**
	 * First, calls the translation of the given sequent (hypothesis and goal
	 * 'Predicate' instances) into SMT-LIB syntax, and then calls the SMT
	 * prover. The test is successful if the solver returns the expected result.
	 * 
	 * @param parsedHypothesis
	 *            list of the sequent hypothesis (Predicate instances)
	 * @param parsedGoal
	 *            sequent goal (Predicate instance)
	 * @param expectedSolverResult
	 *            the result expected to be produced by the solver call
	 */
	private void doTest(final String lemmaName,
			final List<Predicate> parsedHypothesis, final Predicate parsedGoal,
			final boolean expectedSolverResult) throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate predicate : parsedHypothesis) {
			assertTypeChecked(predicate);
		}

		// Create an instance of SmtProversCall
		final SmtProverCall smtProverCall = new SmtProverCall(parsedHypothesis,
				parsedGoal, MONITOR, preferences, lemmaName) {
			@Override
			public String displayMessage() {
				return "SMT";
			}
		};

		try {
			final List<String> smtArgs = new ArrayList<String>(
					smtProverCall.smtTranslationThroughPP());
			smtProverCall.callProver(smtArgs);
			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (final TranslationException t) {
			fail(t.getMessage());
		} catch (final IOException ioe) {
			fail(ioe.getMessage());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
	}

	/**
	 * 
	 */
	@Test
	@Ignore("Expected true, but it was false")
	public void testDifferentForallPlusSimple() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForallPlusSimple", hyps, "{1 ↦ {0}} ∈ {1} → {{0}}",
				te, VALID);
	}

	@Test
	public void testSolverCallBelong1() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("g ∈ e");

		// perform test
		doTest("belong_1", hyps, "g ∈ f", pow_te, NOT_VALID);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test
	public void testSolverCallSimpleU() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");

		// perform test
		doTest("simpleU", hyps, "⊤", te, VALID);
	}

	/**
	 * This test is related to the 'Empty' problem, which declares the sort U.
	 * This problem belongs to SMT-Solvers.
	 */
	@Test
	public void testSolverCallBelong3() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(
				//
				"a", "S", "b", "T", "d", "U", "A", "ℙ(S)", "r", "S ↔ T", "s",
				"(S × T) ↔ U");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");
		hyps.add("a↦b ∈ r");
		hyps.add("a↦b↦d ∈ s");

		// perform test
		doTest("belong_3", hyps, "⊤", te, VALID);
	}

	@Test
	public void testSolverCallWithVeriT() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest("with_verit", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithCvc3() {
		// Set preferences to test with Cvc3
		setPreferencesForCvc3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest("with_cvc3", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithZ3() {
		// Set preferences to test with Z3
		setPreferencesForZ3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest("with_z3", hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithAltErgo() {
		// Set preferences to test with Alt-Ergo
		setPreferencesForAltErgoTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest("with_altergo", hyps, "x < z", arith_te, VALID);
	}

	/**
	 * ch8_circ_arbiter.1 from task 1 (Requirement Analysis) 's Rodin benchmarks
	 * on 'integer' theory
	 */
	@Test
	public void testCh8CircArbiter1() {
		setPreferencesForAltErgoTest();

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
	@Test
	public void testQuickSort1() {
		setPreferencesForAltErgoTest();

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
	@Test
	public void testBoschSwitch1() {
		setPreferencesForAltErgoTest();

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
	@Test
	public void testBepiColombo1() {
		setPreferencesForAltErgoTest();

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
	@Test
	@Ignore("AltErgo MESSAGE: unknown (sat)")
	public void testCh915Bin10() {
		setPreferencesForAltErgoTest();

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
	@Test
	public void testCh7LikeEvenSimpler() {
		setPreferencesForZ3Test();
		// setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		// hyps.add("n ≥ 1");

		doTest("ch7_likeEvenSimpler", hyps, "A×B ⊆ ℕ×ℕ", te, !VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test
	public void testCh7LikeMoreSimpleYet() {
		setPreferencesForZ3Test();
		// setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		// hyps.add("n ≥ 1");

		doTest("ch7_likeMoreSimpleYet", hyps, "{0 ↦ d} ∈ ({0,1} →  D)", te,
				!VALID);
	}

	/**
	 * 
	 */
	@Test
	@Ignore("Expected true, but it was false")
	public void testDifferentForall() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();

		doTest("differentForall", hyps, "{1 ↦ {0 ↦ d}} ∈ {1} → ({0} →  D)", te,
				VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 */
	@Test
	@Ignore("Expected true, but it was false")
	public void testCh7LikeSimple() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();

		doTest("ch7_likeSimple", hyps, "{1 ↦ {0 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 */
	@Test
	@Ignore("Expected true, but it was false")
	public void testCh7LikeConc() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		// hyps.add("n ≥ 1");

		doTest("ch7_likeconc", hyps,
				"{1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)", te, VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 */
	@Test
	@Ignore("Expected true, but it was false")
	public void testCh7Conc29_AltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch7_conc29_altErgo", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test
	@Ignore("Expected true, but it was false")
	public void testCh7Conc29_Z3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch7_conc29_z3", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test
	@Ignore("The solver returns unknown")
	public void testCh7Conc29_cvc3() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch7_conc29_cvc3", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test
	@Ignore("Segmentation Fault of the Solver")
	public void testCh7Conc29_verit() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch7_conc29_verit", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test
	public void testBepiColombo3Mini() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Mini", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	@Ignore("Segmentation Fault with VeriT")
	public void testBepiColombo3MediumWithVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Medium", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	public void testBepiColombo3MediumWithZ3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3MediumZ3", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	@Ignore("Takes more than 30 seconds to return a result")
	public void testBepiColombo3Medium2() {
		setPreferencesForAltErgoTest();

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
	@Test
	@Ignore("Segmentation Fault with VeriT")
	public void testBepiColombo3() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1,1 ↦ 2,1 ↦ 7,1 ↦ 8,3 ↦ 25,5 ↦ 1,5 ↦ 2,5 ↦ 3,5 ↦ 4,6 ↦ 6,6 ↦ 10,17 ↦ 2,21 ↦ 3}");

		doTest("bepi_colombo3", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	public void testBepiColombo3WithZ3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1,1 ↦ 2,1 ↦ 7,1 ↦ 8,3 ↦ 25,5 ↦ 1,5 ↦ 2,5 ↦ 3,5 ↦ 4,6 ↦ 6,6 ↦ 10,17 ↦ 2,21 ↦ 3}");

		doTest("bepi_colombo3z3", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	@Ignore("veriT returns unknown.")
	public void testDynamicStableLSR_081014_15WithVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "h",
				"ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("h ∈ S →  (S × S →  ℕ)");
		hyps.add("n ∈ dom(h)");
		hyps.add("m ↦ n ∈ dom(h(n))");
		hyps.add("h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15_verit", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID);
	}

	@Test
	@Ignore("cvc3 returns unknown")
	public void testDynamicStableLSR_081014_15WithCVC3() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "h",
				"ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("h ∈ S →  (S × S →  ℕ)");
		hyps.add("n ∈ dom(h)");
		hyps.add("m ↦ n ∈ dom(h(n))");
		hyps.add("h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15_cvc3", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID);
	}

	@Test
	@Ignore("Runs forever")
	public void testDynamicStableLSR_081014_15WithZ3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "h",
				"ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("h ∈ S →  (S × S →  ℕ)");
		hyps.add("n ∈ dom(h)");
		hyps.add("m ↦ n ∈ dom(h(n))");
		hyps.add("h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15_z3", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID);
	}

	@Test
	@Ignore("Takes more than 10 seconds to return a result")
	public void testDynamicStableLSR_081014_15WithAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "h",
				"ℙ(S × ℙ(S × S × ℤ))", "m", "S", "n", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("h ∈ S →  (S × S →  ℕ)");
		hyps.add("n ∈ dom(h)");
		hyps.add("m ↦ n ∈ dom(h(n))");
		hyps.add("h(n){m ↦ n ↦ (h(n))(m ↦ n)+1} ∈ S × S →  ℕ");

		doTest("DynamicStableLSR_081014_15_altergo", hyps,
				"h {n ↦ h(n){m ↦ n ↦ (h(n))(m ↦ n)+1}} ∈ S ⇸ (S × S →  ℕ)",
				te, VALID);
	}

	@Test
	@Ignore("division is uninterpreted, so the solver returned sat")
	public void testExactDivisionWithVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_verit";
		doTest(smtFileName + "_1", hyps, "4 ÷ 2 = 2", te, VALID);
		doTest(smtFileName + "_2", hyps, "−4 ÷ 2 = −2", te, VALID);
		doTest(smtFileName + "_3", hyps, "−4 ÷ −2 = 2", te, VALID);
		doTest(smtFileName + "_4", hyps, "4 ÷ −2 = −2", te, VALID);
	}

	@Test
	@Ignore("z3 uses the symbol div as division. And it does not have the same properties as in Event-B")
	public void testExactDivisionWithZ3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_Z3";
		doTest(smtFileName + "_1", hyps, "4 ÷ 2 = 2", te, VALID);
		doTest(smtFileName + "_2", hyps, "−4 ÷ 2 = −2", te, VALID);
		doTest(smtFileName + "_3", hyps, "−4 ÷ −2 = 2", te, VALID);
		doTest(smtFileName + "_4", hyps, "4 ÷ −2 = −2", te, VALID);
	}

	@Test
	@Ignore("division is uninterpreted, so the solver returned sat")
	public void testExactDivisionWithCVC3() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_cvc3";
		doTest(smtFileName + "_1", hyps, "4 ÷ 2 = 2", te, VALID);
		doTest(smtFileName + "_2", hyps, "−4 ÷ 2 = −2", te, VALID);
		doTest(smtFileName + "_3", hyps, "−4 ÷ −2 = 2", te, VALID);
		doTest(smtFileName + "_4", hyps, "4 ÷ −2 = −2", te, VALID);
	}

	@Test
	@Ignore("division is uninterpreted, so the solver returned sat")
	public void testExactDivisionWithAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_AltErgo";
		doTest(smtFileName + "_1", hyps, "4 ÷ 2 = 2", te, VALID);
		doTest(smtFileName + "_2", hyps, "−4 ÷ 2 = −2", te, VALID);
		doTest(smtFileName + "_3", hyps, "−4 ÷ −2 = 2", te, VALID);
		doTest(smtFileName + "_4", hyps, "4 ÷ −2 = −2", te, VALID);
	}

	// --------------------------------------------
	// --------------------------------------------

	@Test
	@Ignore("Division in veriT does not have the same properties as in Event-B")
	public void testDivisionWithRemainderWithVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_rem_verit";
		doTest(smtFileName + "_1", hyps, "3 ÷ 2 = 1", te, VALID);
		doTest(smtFileName + "_2", hyps, "−3 ÷ 2 = −1", te, VALID);
		doTest(smtFileName + "_3", hyps, "−3 ÷ −2 = 1", te, VALID);
		doTest(smtFileName + "_4", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	@Ignore("Division in z3 does not have the same properties as in Event-B")
	public void testDivisionWithRemainderWithZ3() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_rem_Z3";
		doTest(smtFileName + "_1", hyps, "3 ÷ 2 = 1", te, VALID);
		doTest(smtFileName + "_2", hyps, "−3 ÷ 2 = −1", te, VALID);
		doTest(smtFileName + "_3", hyps, "−3 ÷ −2 = 1", te, VALID);
		doTest(smtFileName + "_4", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	@Ignore("Division in cvc3 does not have the same properties as in Event-B")
	public void testDivisionWithRemainderWithCVC3() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div_rem_cvc3";
		doTest(smtFileName + "_1", hyps, "3 ÷ 2 = 1", te, VALID);
		doTest(smtFileName + "_2", hyps, "−3 ÷ 2 = −1", te, VALID);
		doTest(smtFileName + "_3", hyps, "−3 ÷ −2 = 1", te, VALID);
		doTest(smtFileName + "_4", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	@Ignore("Division in alt-ergo does not have the same properties as in Event-B")
	public void testDivisionWithRemainderWithAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		final String smtFileName = "div__remAltErgo";
		doTest(smtFileName + "_1", hyps, "3 ÷ 2 = 1", te, VALID);
		doTest(smtFileName + "_2", hyps, "−3 ÷ 2 = −1", te, VALID);
		doTest(smtFileName + "_3", hyps, "−3 ÷ −2 = 1", te, VALID);
		doTest(smtFileName + "_4", hyps, "3 ÷ −2 = −1", te, VALID);
	}

	@Test
	public void testch910_ring_6() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("itv ∈ P → (P → ℙ(P))");
		doTest("ch910_ring_6", hyps, "itv∼;({f} ◁ itv) ⊆ id", te, VALID);
	}

	@Test
	public void testch910_ring_6_simple() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		doTest("ch910_ring_6_simple", hyps, "itv ∈ P → (P → ℙ(P))", te, VALID);
	}

	@Test
	public void testch910_ring_6_pp() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		doTest("ch910_ring_6_pp",
				hyps,
				"(∀x,x0·x ↦ x0∈itv⇒x∈P∧(∀x,x1·x ↦ x1∈x0⇒x∈P∧(∀x·x∈x1⇒x∈P))∧(∀x,x1,x2·x ↦ x1∈x0∧x ↦ x2∈x0⇒x1=x2)∧(∀x·x∈P⇒(∃x1·x ↦ x1∈x0)))∧(∀x,x0,x1·x ↦ x0∈itv∧x ↦ x1∈itv⇒x0=x1)∧(∀x·x∈P⇒(∃x0·x ↦ x0∈itv))",
				te, VALID);
	}

	@Test
	public void testch910_ring_6_pp_simple() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		doTest("ch910_ring_6_pp_simple",
				hyps,
				"(∀x,x0·x ↦ x0∈itv⇒x∈P∧(∀x,x1·x ↦ x1∈x0⇒x∈P∧(∀x·x∈x1⇒x∈P))∧(∀x,x1,x2·x ↦ x1∈x0∧x ↦ x2∈x0⇒x1=x2)∧(∀x·x∈P⇒(∃x1·x ↦ x1∈x0)))",
				te, VALID);
	}

	@Test
	public void testch910_ring_6_pp_smaller() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		doTest("ch910_ring_6_pp_smaller", hyps,
				"(∀x,x1·x ↦ x1∈x0⇒x∈P∧(∀x·x∈x1⇒x∈P))", te, VALID);
	}

	@Test
	public void testch910_ring_6_pp_mini() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		doTest("ch910_ring_6_pp_mini", hyps,
				"(∀x,x1·x ↦ x1∈x0⇒x∈P∧(∀x·x∈x1⇒x∈P))", te, VALID);
	}
}