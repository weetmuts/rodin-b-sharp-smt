/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import static org.eventb.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.provers.internal.core.SMTSolver.Z3;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTSignature;
import org.eventb.smt.preferences.SMTPreferences;
import org.eventb.smt.preferences.SolverDetails;
import org.eventb.smt.provers.internal.core.SMTPPCall;
import org.eventb.smt.provers.internal.core.SMTProverCall;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.provers.internal.core.SMTVeriTCall;
import org.eventb.smt.translation.SMTLIBVersion;
import org.eventb.smt.translation.SMTThroughPP;
import org.eventb.smt.translation.SMTTranslationApproach;
import org.junit.After;

public abstract class CommonSolverRunTests extends AbstractTests {
	public static final String DEFAULT_TEST_TRANSLATION_PATH = System
			.getProperty("user.home")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";

	private static final NullProofMonitor MONITOR = new NullProofMonitor();

	private final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	private SMTPreferences preferences;

	static File smtFolder;

	/**
	 * H |- ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static boolean VALID = true;
	/**
	 * H |- ¬ G is SAT, so H |- G is NOT VALID
	 */
	protected static boolean NOT_VALID = false;

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
	private static class NullProofMonitor implements IProofMonitor {
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

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 * @param solverBinaryName
	 * @param solverArgs
	 * @param isSMTV1_2Compatible
	 * @param isSMTV2_0Compatible
	 */
	private void setSolverPreferences(final String solverBinaryName,
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

		final SolverDetails sd = new SolverDetails(solverBinaryName,
				solverPath.toString(), solverArgs, isSMTV1_2Compatible,
				isSMTV2_0Compatible);
		preferences = new SMTPreferences(DEFAULT_TEST_TRANSLATION_PATH, sd,
				veritBinPath.toString());
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
	private void doTest(final SMTTranslationApproach translationApproach,
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
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(parsedHypotheses, parsedGoal,
						MONITOR, preferences, lemmaName) {
					// nothing to do
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
						.getSolver().getId(), V1_2);

		final SMTSignature signature = benchmark.getSignature();

		AbstractTests.testTypeEnvironmentSorts(signature, expectedSorts, "");
		AbstractTests.testTypeEnvironmentFuns(signature, expectedFuns, "");
		AbstractTests.testTypeEnvironmentPreds(signature, expectedPreds, "");
	}

	protected void setPreferencesForAltErgoTest(
			final SMTLIBVersion smtlibVersion) {
		switch (smtlibVersion) {
		case V1_2:
			setSolverPreferences(ALT_ERGO.toString(), "", true, false);
			break;

		default:
			setSolverPreferences(ALT_ERGO.toString(), "", false, true);
			break;
		}
	}

	protected void setPreferencesForVeriTTest(final SMTLIBVersion smtlibVersion) {
		switch (smtlibVersion) {
		case V1_2:
			setSolverPreferences(VERIT.toString(), "", true, false);
			break;

		default:
			setSolverPreferences(VERIT.toString(), "-i smtlib2", false, true);
			break;
		}
	}

	protected void setPreferencesForCvc3Test(final SMTLIBVersion smtlibVersion) {
		switch (smtlibVersion) {
		case V1_2:
			setSolverPreferences(CVC3.toString(), "-lang smt", true, false);
			break;

		default:
			setSolverPreferences(CVC3.toString(), "-lang smt2", false, true);
			break;
		}

	}

	protected void setPreferencesForZ3Test(final SMTLIBVersion smtlibVersion) {
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

		switch (smtlibVersion) {
		case V1_2:
			setSolverPreferences(binaryName.toString(), "", true, false);
			break;

		default:
			setSolverPreferences(binaryName.toString(), "-smt2", false, true);
			break;
		}
	}

	protected void setPreferencesForSolverTest(final SMTSolver solver,
			final SMTLIBVersion smtlibVersion) {
		switch (solver) {
		case ALT_ERGO:
			setPreferencesForAltErgoTest(smtlibVersion);
			break;
		case CVC3:
			setPreferencesForCvc3Test(smtlibVersion);
			break;
		case VERIT:
			setPreferencesForVeriTTest(smtlibVersion);
			break;
		case Z3:
			setPreferencesForZ3Test(smtlibVersion);
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

	@After
	public void finalizeSolverProcess() {
		for (final SMTProverCall smtProverCall : smtProverCalls) {
			smtProverCall.cleanup();
		}
	}
}
