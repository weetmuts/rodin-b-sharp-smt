package fr.systerel.smt.provers.core.tests;

import static br.ufrn.smt.solver.translation.SMTSolver.ALT_ERGO;
import static br.ufrn.smt.solver.translation.SMTSolver.CVC3;
import static br.ufrn.smt.solver.translation.SMTSolver.VERIT;
import static br.ufrn.smt.solver.translation.SMTSolver.Z3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.junit.After;
import org.junit.BeforeClass;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetail;
import br.ufrn.smt.solver.translation.SMTSolver;
import br.ufrn.smt.solver.translation.SMTThroughPP;
import br.ufrn.smt.solver.translation.SMTTranslationApproach;
import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.internal.core.SMTPPCall;
import fr.systerel.smt.provers.internal.core.SMTProverCall;
import fr.systerel.smt.provers.internal.core.SMTVeriTCall;

public abstract class CommonSolverRunTests extends AbstractTests {

	protected SMTPreferences preferences;

	public static String smtFolder;

	protected final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	protected static final boolean CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST = true;
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
	 * In linux: '/home/username/bin/'
	 */
	private static final void binPathToString(final StringBuilder sb) {
		final String separator = System.getProperty("file.separator");
		sb.append(System.getProperty("user.home"));
		sb.append(separator);
		sb.append("bin");
		sb.append(separator);
	}

	/**
	 * A ProofMonitor is necessary for SMTProverCall instances creation.
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
			// Nothing do to
		}

		@Override
		public void setTask(final String name) {
			// Nothing do to
		}
	}

	@BeforeClass
	public static void cleanSMTFolder() {
		if (CommonSolverRunTests.CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST) {
			CommonSolverRunTests.smtFolder = SMTProverCall
					.mkTranslationDir(CLEAN_FOLDER_FILES_BEFORE_EACH_CLASS_TEST);
		}
	}

	@After
	public void finalizeSolverProcess() {
		for (final SMTProverCall smtProverCall : smtProverCalls) {
			smtProverCall.cleanup();
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
	protected void setSolverPreferences(final String solverBinaryName,
			final String solverArgs, final boolean isSMTV1_2Compatible,
			final boolean isSMTV2_0Compatible) {
		final String OS = System.getProperty("os.name");
		final StringBuilder solverPath = new StringBuilder();
		final StringBuilder veritBinPath = new StringBuilder();

		if (OS.startsWith("Windows")) {
			binPathToString(solverPath);
			solverPath.append(solverBinaryName);
			solverPath.append(".exe");
		} else {
			binPathToString(solverPath);
			solverPath.append(solverBinaryName);
		}

		binPathToString(veritBinPath);
		VERIT.toString(veritBinPath);

		final SolverDetail sd = new SolverDetail(solverBinaryName,
				solverPath.toString(), solverArgs, isSMTV1_2Compatible,
				isSMTV2_0Compatible);
		preferences = new SMTPreferences(sd, true, veritBinPath.toString());
	}

	protected void setPreferencesForVeriTTest() {
		setSolverPreferences(VERIT.toString(), "", true, false);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences(CVC3.toString(), "-lang smt", true, false);

	}

	protected void setPreferencesForZ3Test() {
		final StringBuilder binaryName = new StringBuilder();
		final String separator = System.getProperty("file.separator");
		if (System.getProperty("os.name").startsWith("Windows")) {
			binaryName.append("bin");
			binaryName.append(separator);
			Z3.toString(binaryName);
			binaryName.append(separator);
			binaryName.append("bin");
			binaryName.append(separator);
		}
		Z3.toString(binaryName);

		setSolverPreferences(binaryName.toString(), "", true, false);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(ALT_ERGO.toString(), "", true, false);
	}

	protected void setPreferencesForSolverTest(final SMTSolver solver) {
		switch (solver) {
		case ALT_ERGO:
			setPreferencesForAltErgoTest();
			break;
		case CVC3:
			setPreferencesForCvc3Test();
			break;
		case VERIT:
			setPreferencesForVeriTTest();
			break;
		case Z3:
			setPreferencesForZ3Test();
			break;
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
	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(translationApproach, lemmaName, hypotheses, goal,
				expectedSolverResult);
	}

	/**
	 * First, calls the translation of the given sequent (hypotheses and goal
	 * 'Predicate' instances) into SMT-LIB syntax, and then calls the SMT
	 * prover. The test is successful if the solver returns the expected result.
	 * 
	 * @param parsedHypotheses
	 *            list of the sequent hypotheses (Predicate instances)
	 * @param parsedGoal
	 *            sequent goal (Predicate instance)
	 * @param expectedSolverResult
	 *            the result expected to be produced by the solver call
	 */
	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<Predicate> parsedHypotheses,
			final Predicate parsedGoal, final boolean expectedSolverResult)
			throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final SMTProverCall smtProverCall;

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(parsedHypotheses, parsedGoal,
						MONITOR, preferences, lemmaName) {
					@Override
					public String displayMessage() {
						return "SMT";
					}
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(parsedHypotheses, parsedGoal,
						MONITOR, preferences, lemmaName) {
					@Override
					public String displayMessage() {
						return "SMT";
					}
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
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
			final List<Predicate> parsedHypotheses, final Predicate parsedGoal,
			final Set<String> expectedFuns, final Set<String> expectedPreds,
			final Set<String> expectedSorts) throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final SMTBenchmark benchmark = SMTThroughPP.translateToSmtLibBenchmark(
				lemmaName, parsedHypotheses, parsedGoal, preferences
						.getSolver().getId());

		final SMTSignature signature = benchmark.getSignature();

		AbstractTests.testTypeEnvironmentSorts(signature, expectedSorts, "");
		AbstractTests.testTypeEnvironmentFuns(signature, expectedFuns, "");
		AbstractTests.testTypeEnvironmentPreds(signature, expectedPreds, "");
	}
}
