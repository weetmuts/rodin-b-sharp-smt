package fr.systerel.smt.provers.core.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetail;
import br.ufrn.smt.solver.translation.PreProcessingException;
import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.internal.core.SmtProverCall;

public class CommonSolverRunTests extends AbstractTests {

	private static final String VERIT = "verit";
	protected SMTPreferences preferences;

	public static String smtFolder;

	protected Process solverProcess;

	protected static final boolean CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST = true;

	/**
	 * A ProofMonitor is necessary for SmtProverCall instances creation.
	 * Instances from this ProofMonitor do nothing.
	 */
	protected static class NullProofMonitor implements IProofMonitor {
		public NullProofMonitor() {
			// Nothing do to
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void setCanceled(final boolean value) {
			// nothing to do
		}

		@Override
		public void setTask(final String name) {
			// nothing to do
		}
	}

	/**
	 * In linux: '/home/username/bin/'
	 */
	protected static final String BIN_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "bin"
			+ System.getProperty("file.separator");
	/**
	 * H |- ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static boolean VALID = true;
	/**
	 * H |- ¬ G is SAT, so H |- G is NOT VALID
	 */
	protected static boolean NOT_VALID = false;
	protected static final NullProofMonitor MONITOR = new NullProofMonitor();

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 * @param solverBinaryName
	 * @param solverArgs
	 * @param isSMTV1_2Compatible
	 * @param isSMTV2_0Compatible
	 */
	protected void setSolverPreferences(final String solverBinaryName,
			final String solverArgs, final boolean isSMTV1_2Compatible,
			final boolean isSMTV2_0Compatible) {
		final String OS = System.getProperty("os.name");
		final String solverPath;

		if (OS.startsWith("Windows")) {
			solverPath = BIN_PATH + solverBinaryName + ".exe";
		} else {
			solverPath = BIN_PATH + solverBinaryName;
		}

		final SolverDetail sd = new SolverDetail(solverBinaryName, solverPath,
				solverArgs, isSMTV1_2Compatible, isSMTV2_0Compatible);
		preferences = new SMTPreferences(sd, true, BIN_PATH + VERIT);
	}

	protected void setPreferencesForVeriTTest() {
		setSolverPreferences(VERIT, "", true, false);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences("cvc3", "-lang smt", true, false);

	}

	protected void setPreferencesForZ3Test() {
		String solver = "z3";
		if (System.getProperty("os.name").startsWith("Windows")) {
			solver = "bin" + System.getProperty("file.separator") + solver
					+ System.getProperty("file.separator") + "bin"
					+ System.getProperty("file.separator") + "z3";
		}

		setSolverPreferences(solver, "", true, false);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences("alt-ergo", "", true, false);
	}

	protected void doTTeTest(final String lemmaName,
			final List<String> inputHyps, final String inputGoal,
			final ITypeEnvironment te, final Set<String> expectedFuns,
			final Set<String> expectedPreds, final Set<String> expectedSorts) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTeTest(lemmaName, hypotheses, goal, expectedFuns, expectedPreds,
				expectedSorts);
	}

	private void doTeTest(final String lemmaName,
			final List<Predicate> parsedHypothesis, final Predicate parsedGoal,
			final Set<String> expectedFuns, final Set<String> expectedPreds,
			final Set<String> expectedSorts) throws IllegalArgumentException {
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
			final SMTBenchmark benchmark = smtProverCall
					.translateToBenchmarkThroughPP();

			final SMTSignature signature = benchmark.getSignature();

			AbstractTests.testTypeEnvironmentSorts(expectedSorts, signature);
			AbstractTests.testTypeEnvironmentFuns(expectedFuns, signature);
			AbstractTests.testTypeEnvironmentPreds(expectedPreds, signature);

		} catch (final PreProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
