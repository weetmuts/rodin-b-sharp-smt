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
import static org.junit.Assert.assertTrue;
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
import org.eventb.smt.preferences.SMTSolverConfiguration;
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

	static File smtFolder;

	/**
	 * H |- ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static boolean VALID = true;
	/**
	 * H |- ¬ G is SAT, so H |- G is NOT VALID
	 */
	protected static boolean NOT_VALID = false;

	protected SMTSolverConfiguration solverConfig;
	protected String poName;
	protected String translationPath;
	protected String veritPath;

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

	public CommonSolverRunTests(final SMTSolver solver,
			final SMTLIBVersion smtlibVersion) {
		solverConfig = new SMTSolverConfiguration("test-config", solver, "",
				"", smtlibVersion);
		if (solver != null) {
			setPreferencesForSolverTest(solver);
		}
		this.translationPath = DEFAULT_TEST_TRANSLATION_PATH;
	}

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 * @param solverBinaryName
	 * @param solverArgs
	 * @param smtlibVersion
	 */
	private void setSolverPreferences(final String solverBinaryName,
			final SMTSolver solver, final String solverArgs,
			final SMTLIBVersion smtlibVersion) {
		final String OS = System.getProperty("os.name");
		final StringBuilder solverPathBuilder = new StringBuilder();
		final StringBuilder veritBinPath = new StringBuilder();

		if (OS.startsWith("Windows")) {
			binPathToString(solverPathBuilder);
			solverPathBuilder.append(solverBinaryName);
			solverPathBuilder.append(".exe");
		} else {
			binPathToString(solverPathBuilder);
			solverPathBuilder.append(solverBinaryName);
		}

		binPathToString(veritBinPath);
		VERIT.toString(veritBinPath);

		solverConfig.setSolver(solver);
		solverConfig.setPath(solverPathBuilder.toString());
		solverConfig.setArgs(solverArgs);
		solverConfig.setSmtlibVersion(smtlibVersion);
		this.veritPath = veritBinPath.toString();
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
						MONITOR, solverConfig, lemmaName, translationPath,
						veritPath) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(parsedHypotheses, parsedGoal,
						MONITOR, solverConfig, lemmaName, translationPath) {
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

	private SMTProverCall successfulProverCall(final String callMessage,
			final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<Predicate> parsedHypotheses,
			final Predicate parsedGoal, final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed) {
		SMTProverCall smtProverCall = null;
		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(parsedHypotheses, parsedGoal,
						MONITOR, solverConfig, lemmaName, translationPath,
						veritPath) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(parsedHypotheses, parsedGoal,
						MONITOR, solverConfig, lemmaName, translationPath) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			assertEquals(callMessage
					+ " The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
			assertTrue(
					callMessage
							+ " The extracted unsat-core wasn't the expected one.",
					smtProverCall.neededHypotheses() != null
							&& smtProverCall.neededHypotheses().containsAll(
									expectedUnsatCore));
			assertTrue(callMessage
					+ " The extracted unsat-core wasn't the expected one.",
					expectedUnsatCore.containsAll(smtProverCall
							.neededHypotheses()));
			assertEquals(callMessage
					+ " The extracted goal need wasn't the expected one.",
					expectedGoalNeed, smtProverCall.isGoalNeeded());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
		return smtProverCall;
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
				lemmaName, parsedHypotheses, parsedGoal,
				solverConfig.getSmtlibVersion());

		final SMTSignature signature = benchmark.getSignature();

		AbstractTests.testTypeEnvironmentSorts(signature, expectedSorts, "");
		AbstractTests.testTypeEnvironmentFuns(signature, expectedFuns, "");
		AbstractTests.testTypeEnvironmentPreds(signature, expectedPreds, "");
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(ALT_ERGO.toString(), ALT_ERGO, "",
				solverConfig.getSmtlibVersion());
	}

	protected void setPreferencesForVeriTTest() {
		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "";
		} else {
			args = "-i smtlib2 --disable-print-success";
		}
		setSolverPreferences(VERIT.toString(), VERIT, args, smtlibVersion);
	}

	protected void setPreferencesForVeriTProofTest() {
		/**
		 * smtlibVersion.equals(V2_0)
		 */
		setSolverPreferences(
				"veriT-proof-producing",
				VERIT,
				"-i smtlib2 --disable-print-success --proof=- --proof-version=1 --proof-prune",
				solverConfig.getSmtlibVersion());
	}

	protected void setPreferencesForCvc3Test() {
		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "-lang smt";
		} else {
			args = "-lang smt2";
		}
		setSolverPreferences(CVC3.toString(), CVC3, args, smtlibVersion);
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

		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "";
		} else {
			args = "-smt2";
		}
		setSolverPreferences(binaryName.toString(), Z3, args, smtlibVersion);
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
		case UNKNOWN:
			// TODO
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

	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed) {
		final List<Predicate> parsedHypotheses = new ArrayList<Predicate>();
		for (final String hyp : inputHyps) {
			parsedHypotheses.add(parse(hyp, te));
		}
		final Predicate parsedGoal = parse(inputGoal, te);

		final List<Predicate> expectedHypotheses = new ArrayList<Predicate>();
		for (final String expectedHyp : expectedUnsatCoreStr) {
			expectedHypotheses.add(parse(expectedHyp, te));
		}

		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		/**
		 * Iter 1 : calls the prover with the expected unsat-core, to check if
		 * it is right
		 */
		final Predicate goalXML = (expectedGoalNeed ? parsedGoal : parse(
				"⊥", te));
		successfulProverCall("Iter 1", translationApproach, lemmaName,
				expectedHypotheses, goalXML, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed);

		/**
		 * Iter 2 : calls the prover and check if the unsat-core is the expected
		 * one
		 */
		final SMTProverCall smtProverCall = successfulProverCall("Iter 2",
				translationApproach, lemmaName, parsedHypotheses, parsedGoal,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed);

		/**
		 * Iter 3 : calls the prover with the returned unsat-core, to check if
		 * it is right
		 */
		final List<Predicate> neededHypotheses = new ArrayList<Predicate>(
				smtProverCall.neededHypotheses());
		final Predicate goalVeriT = (smtProverCall.isGoalNeeded() ? parsedGoal
				: parse("⊥", te));
		successfulProverCall("Iter 3", translationApproach, lemmaName,
				neededHypotheses, goalVeriT, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed);
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
