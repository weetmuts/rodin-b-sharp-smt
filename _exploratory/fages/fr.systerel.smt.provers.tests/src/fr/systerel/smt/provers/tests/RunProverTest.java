package fr.systerel.smt.provers.tests;

import static br.ufrn.smt.solver.preferences.SMTPreferencesStore.CreatePreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.junit.Assert;
import org.junit.Test;

import br.ufrn.smt.solver.preferences.SolverDetail;
import br.ufrn.smt.solver.translation.TranslationException;
import fr.systerel.smt.provers.core.SmtProversCore;
import fr.systerel.smt.provers.internal.core.SmtProverCall;

/**
 * This class contains acceptance tests of the plugin.
 * 
 * @author Yoann Guyot
 * 
 */
public class RunProverTest extends AbstractTests {
	/**
	 * In linux: '/home/username/bin/'
	 */
	private static final String BIN_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "bin"
			+ System.getProperty("file.separator");

	/**
	 * Possible solver call results
	 */
	private static boolean VALID = true;
	private static boolean NOT_VALID = false;

	private static final NullProofMonitor MONITOR = new NullProofMonitor();

	static ITypeEnvironment arith_te = mTypeEnvironment(//
			"x", "ℤ", "y", "ℤ", "z", "ℤ");
	static ITypeEnvironment pow_te = mTypeEnvironment(//
			"e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

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
	private static void doTest(final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(hypotheses, goal, expectedSolverResult);
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
	private static void doTest(final List<Predicate> parsedHypothesis,
			final Predicate parsedGoal, final boolean expectedSolverResult) {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (Predicate predicate : parsedHypothesis) {
			assertTypeChecked(predicate);
		}

		// Create an instance of SmtProversCall
		final SmtProverCall smtProverCall = new SmtProverCall(parsedHypothesis,
				parsedGoal, MONITOR, "SMT") {
			@Override
			public String displayMessage() {
				return "SMT";
			}
		};

		try {
			final List<String> smtArgs = new ArrayList<String>(
					smtProverCall.smtTranslation());
			smtProverCall.callProver(smtArgs);
			Assert.assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (TranslationException t) {
			System.out.println(t.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * A ProofMonitor is necessary for SmtProverCall instances creation.
	 * Instances from this ProofMonitor do nothing.
	 */
	private static class NullProofMonitor implements IProofMonitor {
		public NullProofMonitor() {
			// Nothing do to
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void setCanceled(boolean value) {
			// nothing to do
		}

		@Override
		public void setTask(String name) {
			// nothing to do
		}
	}

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 * @param solverBinaryName
	 * @param solverArgs
	 * @param isSMTV1_2Compatible
	 * @param isSMTV2_0Compatible
	 */
	private static void setSolverPreferences(final String solverBinaryName,
			final String solverArgs, final boolean isSMTV1_2Compatible,
			final boolean isSMTV2_0Compatible) {
		final String OS = System.getProperty("os.name");
		final SmtProversCore core = SmtProversCore.getDefault();
		final IPreferenceStore store = core.getPreferenceStore();
		final String solverPath;
		
		if(OS.startsWith("Windows"))
		{
			solverPath = BIN_PATH + solverBinaryName + ".exe";
		}
		else
		{
			solverPath = BIN_PATH + solverBinaryName;
		}
		
		System.out.println(solverPath);
		
		final List<SolverDetail> solvers = new ArrayList<SolverDetail>();
		solvers.add(new SolverDetail(solverBinaryName, solverPath, solverArgs,
				isSMTV1_2Compatible, isSMTV2_0Compatible));
		final String preferences = CreatePreferences(solvers);
		store.setValue("solverpreferences", preferences);
		store.setValue("solverindex", 0);
		store.setValue("usingprepro", true);
		store.setValue("prepropath", BIN_PATH + "verit");
	}

	private static void setPreferencesForVeriTTest() {
		setSolverPreferences("verit", "", true, false);
	}

	private static void setPreferencesForCvc3Test() {
		setSolverPreferences("cvc3", "-lang smt", true, false);		
		
	}

	private static void setPreferencesForZ3Test() {
		String solver = "z3";
		if(System.getProperty("os.name").startsWith("Windows"))
		{
			solver = 	"bin" + 
						System.getProperty("file.separator") + 
						solver + 
						System.getProperty("file.separator") +
						"bin" +
						System.getProperty("file.separator") +
						"z3";					
		}		
		
		setSolverPreferences(solver, "", true, false);
	}

	private static void setPreferencesForAltErgoTest() {
		setSolverPreferences("alt-ergo", "", true, false);
	}

	@Test
	public void testSolverCallBelong() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("g ∈ e");

		// perform test
		doTest(hyps, "g ∈ f", pow_te, NOT_VALID);
	}

	@Test
	public void testSolverCallWithVeriT() {
		// Set preferences to test with VeriT
		setPreferencesForVeriTTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest(hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithCvc3() {
		// Set preferences to test with Cvc3
		setPreferencesForCvc3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest(hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithZ3() {
		// Set preferences to test with Z3
		setPreferencesForZ3Test();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest(hyps, "x < z", arith_te, VALID);
	}

	@Test
	public void testSolverCallWithAltErgo() {
		// Set preferences to test with Alt-Ergo
		setPreferencesForAltErgoTest();

		final List<String> hyps = new ArrayList<String>();
		hyps.add("x < y");
		hyps.add("y < z");

		// perform test
		doTest(hyps, "x < z", arith_te, VALID);
	}

	/**
	 * ch8_circ_arbiter.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on 'integer' theory
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

		doTest(hyps, "r1 = a1 + 1", te, VALID);
	}

	/**
	 * quick_sort.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on 'linear_arith' theory
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

		doTest(hyps, "x = k", te, VALID);
	}

	/**
	 * bosch_switch.1 from task 1 (Requirement Analysis) 's Rodin benchmarks on 'linear_order_int' theory
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

		doTest(hyps, "i ≥ 0", te, VALID);
	}
}