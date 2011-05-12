package fr.systerel.smt.provers.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import br.ufrn.smt.solver.translation.Exec;
import fr.systerel.smt.provers.internal.core.SmtProverCall;

/**
 * This class contains acceptance tests of the plugin using veriT preprocessing.
 * 
 * @author Vitor Alcantara
 * 
 */
public class RunProverTestWithVeriT extends CommonSolverRunTests {
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

	@After
	public void finalizeSolverProcess() {
		if (solverProcess != null) {
			solverProcess.destroy();
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
					smtProverCall.smtTranslationThroughVeriT());
			super.solverProcess = Exec.startProcess(smtArgs);
			smtProverCall.callProver(super.solverProcess, smtArgs);
			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (final IOException ioe) {
			fail(ioe.getMessage());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
	}

	@Test
	public void testTePlusSort() {
		setPreferencesForZ3Test();
		final List<String> hyps = new ArrayList<String>();
		hyps.add("g ∈ e");

		final Set<String> expectedSorts = new HashSet<String>();
		expectedSorts.add("S");

		final Set<String> expectedFuns = new HashSet<String>();
		expectedFuns.add("(g S)");

		final Set<String> expectedPreds = new HashSet<String>();
		expectedPreds.add("(e S)");
		expectedPreds.add("(f S)");

		doTTeTest("tetestSort", hyps, "g ∈ f", pow_te, expectedFuns,
				expectedPreds, expectedSorts);
	}

	@Test
	@Ignore("ERROR: line 4 column 42: could not find sort symbol 'Pair'.")
	public void testRule20() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		// hyps.add("(λx· ∀y·y ∈ ℕ ∧ x > y ∣ x+x) = ∅");

		doTest("rule20", hyps, "(λx·x>0 ∣ x+x) ≠ ∅", te, VALID);
	}

	@Test
	@Ignore("error : Sort 't and (Pair Int Int) mismatch")
	public void testRule20ManyForalls() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();

		doTest("rule20_many_foralls", hyps,
				"(λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x))) ∣ x+x) ≠ ∅", te,
				VALID);
	}

	@Test
	public void testRule20MacroInsideMacro() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();

		doTest("rule20_macro_inside_macro", hyps,
				"(λx· (x > 0 ∧ ((λy·y > 0 ∣ y+y) = ∅)) ∣ x+x) = ∅", te, VALID);
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

	@Test
	@Ignore("Verit is producing sort (Pair (Pair 2)), which is not being recognized by other solvers")
	public void testSolverCallSimpleUWithVeriT() {
		setPreferencesForVeriTTest();

		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");

		// perform test
		doTest("simpleU_verit", hyps, "⊤", te, VALID);
	}

	@Test
	@Ignore("Verit is producing sort (Pair (Pair 2)), which is not being recognized by other solvers")
	public void testSolverCallSimpleUWithAltErgo() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");

		// perform test
		doTest("simpleU_altergo", hyps, "⊤", te, VALID);
	}

	@Test
	@Ignore("Verit is producing sort (Pair (Pair 2)), which is not being recognized by other solvers")
	public void testSolverCallSimpleUWithCVC3() {
		setPreferencesForCvc3Test();

		final ITypeEnvironment te = mTypeEnvironment("a", "U", "A", "ℙ(U)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("a ∈ A");

		// perform test
		doTest("simpleU_cvc3", hyps, "⊤", te, VALID);
	}

	@Test
	@Ignore("Type S×T×U: Sets of sets are not supported yet")
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

	@Test(timeout = 5000)
	@Ignore("Expn is not working")
	public void testExpn() {
		setPreferencesForZ3Test();

		final List<String> hyps = new ArrayList<String>();
		doTest("expn", hyps, "x ^ y = z", arith_te, VALID);
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

	@Test
	@Ignore("Error: error : DAG_new: unable to determine sort")
	public void testIntInRelation() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("D", "ℙ(D)", "f",
				"ℙ(ℤ × D)", "n", "ℤ", "r", "ℤ");

		final List<String> hyps = new ArrayList<String>();

		doTest("int_in_relation", hyps, "f ∈ ℤ ⇸  D", te, VALID);
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
	@Ignore("Verit is producing the sort (Pair (Pair 2)) in the post-processed file, which is not ")
	public void testBepiColombo1() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"S", "ℙ(S)", "a", "S", "b", "S", "c", "S");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("S={a,b,c}");
		hyps.add("¬ a=b");
		hyps.add("¬ b=c");
		hyps.add("¬ c=a");

		doTest("bepi_colombo1", hyps, "{a,b,c} = {c,a,b}", te, VALID);
	}

	/**
	 * ch915_bin.10 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'nonlinear_arith' theory
	 */
	@Test
	@Ignore("Division is uninterpreted, so the solver is returning sat")
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
	@Ignore("error : DAG_new: unable to determine sort")
	public void testCh7LikeEvenSimpler() {
		setPreferencesForZ3Test();
		// setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		doTest("ch7_likeEvenSimpler", hyps, "A×B ⊆ ℕ×ℕ", te, !VALID);
	}

	/**
	 * ch7_conc.29 from task 1 (Requirement Analysis) 's Rodin benchmarks on
	 * 'full_set_theory' theory
	 * 
	 */
	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testCh7LikeMoreSimpleYet() {
		setPreferencesForZ3Test();

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
	@Ignore("Type ℙ(ℙ(ℤ×ℙ(ℤ×D))): Sets of sets are not supported yet")
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
	@Ignore("Type ℙ(ℙ(ℤ×ℙ(ℤ×D))): Sets of sets are not supported yet")
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
	@Ignore("Type ℙ(ℙ(ℤ×ℙ(ℤ×D))): Sets of sets are not supported yet")
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
	@Ignore("Type ℙ(ℙ(ℤ×ℙ(ℤ×D))): Sets of sets are not supported yet")
	public void testCh7Conc29() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"D", "ℙ(D)", "d", "D");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("n ≥ 1");

		doTest("ch7_conc29", hyps,
				"{0 ↦ {0 ↦ d,1 ↦ d},1 ↦ {0 ↦ d,1 ↦ d}} ∈ {0,1} → ({0,1} →  D)",
				te, VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testBepiColombo3Mini() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Mini", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Ignore("Takes too much time with AltErgo")
	@Test
	public void testBepiColombo3Medium() {
		setPreferencesForAltErgoTest();

		final ITypeEnvironment te = mTypeEnvironment(//
				"TC", "ℤ↔ℤ", "TM", "ℤ↔ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("TC = {3 ↦ 5,3 ↦ 6,3 ↦ 129,6 ↦ 2,6 ↦ 5,6 ↦ 9,9 ↦ 129,17 ↦ 1,17 ↦ 128,21 ↦ 1,21 ↦ 2,21 ↦ 128,21 ↦ 129,200 ↦ 1,200 ↦ 2,200 ↦ 3,200 ↦ 4,200 ↦ 5,200 ↦ 6,200 ↦ 7,201 ↦ 1,201 ↦ 2,201 ↦ 3,201 ↦ 4,201 ↦ 5,201 ↦ 6,201 ↦ 7,201 ↦ 8,201 ↦ 9,201 ↦ 10,202 ↦ 1,202 ↦ 2,202 ↦ 3,202 ↦ 4,203 ↦ 1,203 ↦ 2,203 ↦ 3,203 ↦ 4,203 ↦ 5,203 ↦ 6,203 ↦ 7,203 ↦ 8,203 ↦ 9}");
		hyps.add("TM = {1 ↦ 1}");

		doTest("bepi_colombo3Medium", hyps, "TC ∩ TM = ∅", te, VALID);
	}

	@Test
	@Ignore("error : Sort 't and (Pair Int Int) mismatch")
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
	@Ignore("error : Sort 't and (Pair Int Int) mismatch")
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
	@Ignore("error : DAG_new: unable to determine sort")
	public void testRule14() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ", "p", "S",
				"q", "S", "a", "ℤ", "b", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("AB = (AB)∼");
		hyps.add("\u00ac(p = q)");
		hyps.add("a = (−b)");
		hyps.add("AB = id");
		hyps.add("a ∈ dom(AB)");

		doTest("rule14", hyps, "b ∈ ran(AB)", te, VALID);
	}

	@Test
	public void testExistsRule17() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("s", "ℙ(R)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("∃x·x∈s");

		doTest("rule17_exists", hyps, "∃x,y·x∈s∧y∈s", te, VALID);
	}

	@Test
	public void testForallRule17() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("s", "ℙ(R)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("∀x·x∈s");
		hyps.add("∀x,y·x∈s∧y∈s");

		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", te);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(Formula.FORALL, bids,
				base.getPredicate(), null);
		System.out.println("Predicate " + p);

		doTest("rule17_forall", hyps, p.toString(), te, VALID);

	}

	@Test
	@Ignore("error : pre_process: results is not FOL")
	public void testRule16() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)", "b", "ℤ",
				"c", "ℤ", "a", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("((A ∩ A) ⊂ (A ∪ A)) ∧ (a + b + c = b) ∧  (a ∗ b ∗ c = 0)");

		doTest("rule16", hyps,
				"((A ∩ A) ⊂ (A ∪ A)) ∨ (a + b + c = b) ∨  (a ∗ b ∗ c ≠ 0)", te,
				VALID);
	}

	@Test
	public void testRule16NotEqual() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)", "b", "ℤ",
				"c", "ℤ", "a", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(a = 1) ∧ (b = 2)");

		doTest("rule16_not_equal", hyps, "(a ∗ b ≠ 0)", te, VALID);
	}

	@Test
	@Ignore("Z3 Message: ERROR: Benchmark constains arithmetic, but QF_UF does not support it.")
	public void testRule15SetMinusUnionInter() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(A ∖ A) ⊂ (A ∪ A)");

		doTest("rule15_setminus_union_inter", hyps, "(A ∩ A) ⊂ (A ∪ A)", te,
				VALID);
	}

	@Test
	@Ignore("Z3 MESSAGE: ERROR: Benchmark contains uninterpreted function symbols, but QF_LIA does not support them.")
	public void testRule15() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("a", "ℤ", "A", "ℙ(ℤ)",
				"b", "ℤ", "c", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(a ∈ A) ∧ (A ⊆ A)");
		hyps.add("(a < b ∧ b > c) ⇒ a = c");
		doTest("rule15", hyps, "(a ≤ b ∧ b ≥ c) ⇔ (a ÷ b) < (c mod b)", te,
				VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testRule15Functions() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ", "A", "ℙ(ℤ)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("AB ∈ (A↔A)");
		hyps.add("AB ∈ (A→A)");
		hyps.add("AB ∈ (A⇸A)");
		hyps.add("AB ∈ (A↣A)");
		hyps.add("AB ∈ (A⤔A)");
		hyps.add("AB ∈ (A↠A)");
		hyps.add("AB ∈ (A⤀A)");

		doTest("rule15_functions", hyps, "AB ∈ (A⤖A)", te, VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testRule15RelationOverridingCompANdComposition() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(AB \ue103 AB) = (AB \ue103 AB)");

		doTest("rule15_ovr_fcomp", hyps, "(AB \u003b AB) = (AB \u003b AB)", te,
				VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testRule15BackwardComposition() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ");

		final List<String> hyps = new ArrayList<String>();

		doTest("rule15_bcomp", hyps, "(AB \u2218 AB) = (AB \u2218 AB)", te,
				VALID);
	}

	@Test
	@Ignore("error : Sort 't and (Pair Int Int) mismatch")
	public void testRule15CartesianProductAndIntegerRange() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("AB", "ℤ ↔ ℤ", "a", "ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(AB × AB) = (AB × AB)");

		doTest("rule15_cart_prod_int_range", hyps, "(a ‥ a) = (a ‥ a)", te,
				VALID);
	}

	@Test
	@Ignore("error : Sort 's and (Pair Int Int) mismatch")
	public void testRule15RestrictionsAndSubstractions() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("(A ◁ AB) = (A ◁ AB)");
		hyps.add("(A ⩤ AB) = (A ⩤ AB)");
		hyps.add("(AB ▷ A) = (AB ▷ A)");

		doTest("rule15_res_subs", hyps, "(AB ⩥ A) = (AB ⩥ A)", te, VALID);
	}

	@Test
	public void testRule18() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("a", "ℤ", "b", "ℤ", "A",
				"ℙ(ℤ)");

		final List<String> hyps = new ArrayList<String>();
		hyps.add("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}");

		doTest("rule18", hyps, "{a∣a ≥ 0} = A", te, VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testSet() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("{0 ↦ 1,1 ↦ 2} = {0 ↦ 1,1 ↦ 2}");

		doTest("testSet", hyps, "{0,1,2,3,4} = A", te, VALID);
	}

	@Test
	public void testRule22and23() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();
		final List<String> hyps = new ArrayList<String>();
		hyps.add("min({2,3}) = min({2,3})");

		doTest("rule22_23", hyps, "max({2,3}) = max({2,3})", te, VALID);
	}

	@Test
	@Ignore("Expected true, but it was false")
	public void testRule24() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();

		doTest("rule24", hyps, "finite({1,2,3})", te, VALID);
	}

	@Test
	@Ignore("Expected true, but it was false")
	public void testRule25() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment();

		final List<String> hyps = new ArrayList<String>();
		doTest("rule25", hyps, "card({1,2,3}) = card({1,2,3})", te, VALID);
	}

	/**
	 * Check if this is a right way to translate
	 */
	@Test
	@Ignore("Verit is producing the sort (Pair (Pair 2)) in the post-processed file, which is not recognized by the solvers")
	public void testKSucc() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("x", "ℙ(ℤ×ℤ)");
		final List<String> hyps = new ArrayList<String>();
		doTest("test_succ", hyps, "x = succ", te, VALID);

	}

	@Test
	@Ignore("Verit is producing the sort (Pair (Pair 2)) in the post-processed file, which is not recognized by the solvers")
	public void testPredSuccMacro() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("x", "ℙ(ℤ×ℤ)");
		final List<String> hyps = new ArrayList<String>();
		doTest("testpred_succ_macro", hyps, "x =(λx·x∈ℤ ∣x+1)", te, VALID);
	}

	/**
	 * Check if this is a right way to translate
	 */
	@Test
	@Ignore("error : line 10, constant - is not declared")
	public void testPred() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("x", "ℙ(ℤ×ℤ)");
		final List<String> hyps = new ArrayList<String>();
		doTest("testpred_pred", hyps, "x = pred", te, VALID);
	}

	@Test
	public void testDistinctForSingleton() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("A = {1,2,3}");
		doTest("distinct_singletons", hyps, "partition(A,{1},{2},{3})", te,
				VALID);
	}

	@Test
	public void testDistinct() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("A = {1,2,3,4}");
		doTest("distincttest", hyps, "partition(A,{1,2},{4},{3})", te, VALID);
	}

	@Test
	public void testSimplerDistinct() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("A = {1,2,3}");
		doTest("distinctsimpletest", hyps, "partition(A,{1,2},{3})", te, VALID);
	}

	@Test
	public void testIntSet() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)");
		final List<String> hyps = new ArrayList<String>();
		doTest("intsettest", hyps, "A ⊆ ℤ", te, VALID);

	}

	@Test
	@Ignore("Type ℤ×ℙ(ℤ×ℙ(ℤ)): Sets of sets are not supported yet")
	public void testch910_ring_6() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("P", "ℙ(ℤ)", "itv",
				"ℙ(ℤ × ℙ(ℤ × ℙ(ℤ)))", "f", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("itv ∈ P → (P → ℙ(P))");
		doTest("ch910_ring_6", hyps, "itv∼;({f} ◁ itv) ⊆ id", te, VALID);
	}

	@Test
	@Ignore("Sort BOOL is not implemented yet")
	public void testLinearSort29() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("f", "ℙ(ℤ × ℤ)", "r",
				"ℙ(ℤ × BOOL)", "m", "ℤ", "x", "ℤ", "j", "ℤ");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("r ∈ 1 ‥ m → BOOL");
		hyps.add("x ∈ 1 ‥ m");
		hyps.add("j+1 ∈ dom(f)");
		doTest("linear_sort_29", hyps, "x ∈ dom(r{f(j+1) ↦ TRUE})", te, VALID);
	}

	@Test
	@Ignore("error : Sort 't and (Pair S S) mismatch.")
	public void testDynamicStableLSR_081014_20() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "P",
				"ℙ(S × S)", "Q", "ℙ(S × S)", "k", "S × S", "m", "S", "n", "S");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("¬ k = m ↦ n");
		hyps.add("k ∈ P ∪ {m ↦ n} ∪ (Q ∖ {m ↦ n})");
		doTest("dynamicStableLSR_081014_20", hyps, "k ∈ P ∪ Q", te, VALID);
	}

	@Test
	@Ignore("Type ℙ(ℙ(S×S×ℤ)): Sets of sets are not supported yet")
	public void testDynamicStableLSR_081014_50() {
		setPreferencesForZ3Test();

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "l", "S × S");
		final List<String> hyps = new ArrayList<String>();
		doTest("dynamicStableLSR_081014_50", hyps,
				"dom({l} × {S × S × {0}}) = {l}", te, VALID);
	}

	@Test
	@Ignore("error : DAG_new: unable to determine sort")
	public void testAllBehaviorOfAllMacrosTogether() {
		setPreferencesForZ3Test();
		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "p", "S",
				"q", "S", "r", "ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)",
				"AB", "ℤ ↔ ℤ", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		final List<String> hyps = new ArrayList<String>();
		hyps.add("card({1,2,3}) = card({1,2,3})");
		hyps.add("finite({1,2,3})");
		hyps.add("max({2,3}) = max({2,3})");
		hyps.add("{0,1,2,3,4} = A");
		hyps.add("{a∣a ≥ 0} = A");
		hyps.add("{0 ↦ 1,1 ↦ 2} = {0 ↦ 1,1 ↦ 2}");
		hyps.add("min({2,3}) = min({2,3})");
		hyps.add("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}");
		hyps.add("(A ◁ AB) = (A ◁ AB)");
		hyps.add("(A ⩤ AB) = (A ⩤ AB)");
		hyps.add("(AB ▷ A) = (AB ▷ A)");
		hyps.add("(AB ⩥ A) = (AB ⩥ A)");
		hyps.add("(a ‥ a) = (a ‥ a)");
		hyps.add("(AB × AB) = (AB × AB)");
		hyps.add("(AB \ue103 AB) = (AB \ue103 AB)");
		hyps.add("(AB \u003b AB) = (AB \u003b AB)");
		hyps.add("AB ∈ (A⤖A)");
		hyps.add("AB ∈ (A↔A)");
		hyps.add("AB ∈ (A→A)");
		hyps.add("AB ∈ (A⇸A)");
		hyps.add("AB ∈ (A↣A)");
		hyps.add("AB ∈ (A⤔A)");
		hyps.add("AB ∈ (A↠A)");
		hyps.add("AB ∈ (A⤀A)");
		hyps.add("(a ≤ b ∧ b ≥ c) ⇔ (a ÷ b) < (c mod b)");
		hyps.add("(a ∈ A) ∧ (A ⊆ A)");
		hyps.add("(a < b ∧ b > c) ⇒ a = c");
		hyps.add("(A ∖ A) ⊂ (A ∪ A)");
		hyps.add("(A ∩ A) ⊂ (A ∪ A)");
		hyps.add("((A ∩ A) ⊂ (A ∪ A)) ∨ (a + b + c = b) ∨  (a ∗ b ∗ c = 0)");
		hyps.add("((A ∩ A) ⊂ (A ∪ A)) ∧ (a + b + c = b) ∧  (a ∗ b ∗ c = 0)");
		hyps.add("∀x·x∈s");
		hyps.add("∀x,y·x∈s∧y∈s");
		hyps.add("∃x·x∈s");
		hyps.add("∃x,y·x∈s∧y∈s");
		hyps.add("b ∈ ran(AB)");
		hyps.add("AB = (AB)∼");
		hyps.add("\u00ac(p = q)");
		hyps.add("a = (−b)");
		hyps.add("AB = id");
		hyps.add("a ∈ dom(AB)");

		doTest("all_macros_together", hyps, "⊤", te, VALID);
	}
}