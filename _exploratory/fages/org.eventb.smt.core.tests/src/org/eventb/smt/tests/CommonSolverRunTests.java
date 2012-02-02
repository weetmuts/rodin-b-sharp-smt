/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import static org.eventb.smt.internal.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.CVC4;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.MATHSAT5;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.OPENSMT;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.Z3;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.internal.ast.SMTBenchmark;
import org.eventb.smt.internal.ast.SMTSignature;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.internal.core.SMTPPCall;
import org.eventb.smt.internal.provers.internal.core.SMTProverCall;
import org.eventb.smt.internal.provers.internal.core.SMTSolver;
import org.eventb.smt.internal.provers.internal.core.SMTVeriTCall;
import org.eventb.smt.internal.translation.SMTLIBVersion;
import org.eventb.smt.internal.translation.SMTThroughPP;
import org.eventb.smt.internal.translation.SMTTranslationApproach;
import org.eventb.smt.utils.Theory;
import org.junit.After;

public abstract class CommonSolverRunTests extends AbstractTests {
	public static final String LAST_ALTERGO = "alt-ergo-nightly-r217";
	public static final String LAST_CVC3 = "cvc3-2011-10-05";
	public static final String LAST_CVC4 = "cvc4-2011-12-11";
	public static final String LAST_MATHSAT5 = "mathsat5-smtcomp2011";
	public static final String LAST_OPENSMT = "opensmt-20101017";
	public static final String LAST_VERIT = "veriT-dev-r2863";
	public static final String LAST_Z3 = "z3-3.2";
	public static final boolean GET_UNSAT_CORE = true;
	public static final String DEFAULT_TEST_TRANSLATION_PATH = System
			.getProperty("user.home")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";

	private static final NullProofMonitor MONITOR = new NullProofMonitor();

	private final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	static File smtFolder;

	/**
	 * H /\ ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static boolean VALID = true;
	protected static boolean TRIVIAL = true;

	protected Set<Theory> theories;
	protected SMTSolverConfiguration solverConfig;
	protected String poName;
	protected String translationPath;
	protected String veritPath;

	/**
	 * True if the extracted unsat-core is the same (or smaller) than the
	 * expected one, and if the solver can discharge it
	 */
	protected boolean gotUnsatCore = false;
	/**
	 * True if the three other solvers could discharge the sequent with the
	 * unsat-core
	 */
	protected boolean unsatCoreChecked = false;

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
			final Set<Theory> theories, final SMTLIBVersion smtlibVersion,
			final boolean getUnsatCore) {
		this.theories = theories;
		solverConfig = new SMTSolverConfiguration(solver.name(), solver, "",
				"", smtlibVersion);
		if (solver != null) {
			if (getUnsatCore && solver.equals(SMTSolver.VERIT)) {
				setPreferencesForVeriTProofTest();
			} else {
				setPreferencesForSolverTest(solver);
			}
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

		solverConfig.setId(solverBinaryName);
		solverConfig.setSolver(solver);
		solverConfig.setPath(solverPathBuilder.toString());
		solverConfig.setArgs(solverArgs);
		solverConfig.setSmtlibVersion(smtlibVersion);
		this.veritPath = veritBinPath.toString();
	}

	private void printPerf(final StringBuilder debugBuilder,
			final String lemmaName, final String solverConfigId,
			final SMTLIBVersion smtlibVersion,
			final SMTTranslationApproach translationApproach,
			final SMTProverCall smtProverCall) {
		debugBuilder.append("<PERF_ENTRY>\n");
		debugBuilder.append("Lemma ").append(lemmaName).append("\n");
		debugBuilder.append("Solver ").append(solverConfigId).append("\n");
		debugBuilder.append(Theory.getComboLevel(theories).getName()).append(
				"\n");
		if (theories != null) {
			debugBuilder.append("Theories ");
			String separator = "";
			for (final Theory theory : theories) {
				debugBuilder.append(separator);
				debugBuilder.append(theory.getName());
				separator = ", ";
			}
			debugBuilder.append("\n");
		}
		debugBuilder.append("SMTLIB ").append(smtlibVersion).append("\n");
		debugBuilder.append("Approach ").append(translationApproach)
				.append("\n");
		debugBuilder.append("Status ");
		if (smtProverCall.isTranslationPerformed()) {
			if (!smtProverCall.isExceptionRaised()) {
				if (smtProverCall.isValid()) {
					debugBuilder.append("success\n");
				} else {
					debugBuilder.append("fail\n");
				}
			} else {
				debugBuilder.append("error\n");
			}
		} else {
			debugBuilder.append("untranslated\n");
		}
		debugBuilder.append("Unsat-Core ");
		debugBuilder.append(gotUnsatCore).append("\n");
		debugBuilder.append("Checked ");
		debugBuilder.append(unsatCoreChecked).append("\n");
		debugBuilder.append("</PERF_ENTRY>\n");
		System.out.println(debugBuilder);
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
			final Predicate parsedGoal, final boolean expectedTrivial,
			final boolean expectedSolverResult, final StringBuilder debugBuilder)
			throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, ff);

		final SMTProverCall smtProverCall;

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName, translationPath,
						veritPath) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, translationPath) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			printPerf(debugBuilder, lemmaName, solverConfig.getId(),
					solverConfig.getSmtlibVersion(), translationApproach,
					smtProverCall);

			assertEquals(expectedTrivial, smtProverCall.benchmarkIsNull());
			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
	}

	private SMTProverCallTestResult smtProverCallTest(final String callMessage,
			final SMTTranslationApproach translationApproach,
			final String lemmaName, final ISimpleSequent sequent,
			final boolean expectedSolverResult, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName, translationPath,
						veritPath) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, translationPath) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			if (smtProverCall.isValid() != expectedSolverResult) {
				errorBuilder.append(callMessage);
				errorBuilder
						.append(" The result of the SMT prover wasn't the expected one.");
				return new SMTProverCallTestResult(smtProverCall, errorBuilder);
			}
		} catch (final IllegalArgumentException iae) {
			errorBuilder.append(iae.getMessage());
			return new SMTProverCallTestResult(smtProverCall, errorBuilder);
		}
		return new SMTProverCallTestResult(smtProverCall, errorBuilder);
	}

	private SMTProverCallTestResult smtProverCallTest(final String callMessage,
			final SMTTranslationApproach translationApproach,
			final String lemmaName, final ISimpleSequent sequent,
			final ITypeEnvironment te, final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName, translationPath,
						veritPath) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, translationPath) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			if (smtProverCall.isValid() != expectedSolverResult) {
				errorBuilder.append(callMessage).append(" (");
				errorBuilder.append(lemmaName);
				errorBuilder
						.append(") The result of the SMT prover wasn't the expected one.");
				return new SMTProverCallTestResult(smtProverCall, errorBuilder);
			}

			final Set<Predicate> neededHypotheses = smtProverCall
					.neededHypotheses();
			final boolean extractedContainsExpected = neededHypotheses == null
					|| (expectedUnsatCore != null && neededHypotheses
							.containsAll(expectedUnsatCore));
			final boolean expectedContainsExtracted = expectedUnsatCore == null
					|| (neededHypotheses != null && expectedUnsatCore
							.containsAll(neededHypotheses));
			if (extractedContainsExpected) {
				if (!expectedContainsExtracted) {
					/*
					 * errorBuilder.append(callMessage);
					 * errorBuilder.append(" (").append(lemmaName); errorBuilder
					 * .
					 * append(") The expected unsat-core is smaller than the ");
					 * errorBuilder
					 * .append(solverConfig.getId()).append(" one."); return new
					 * SMTProverCallTestResult(smtProverCall, errorBuilder);
					 */
				}
			} else if (expectedContainsExtracted) {
				errorBuilder.append(callMessage);
				errorBuilder.append(" (").append(lemmaName).append(") ");
				errorBuilder.append(solverConfig.getId());
				errorBuilder
						.append(" unsat-core is smaller than the expected one.");
				return new SMTProverCallTestResult(smtProverCall, errorBuilder);
			} else {
				/*
				 * errorBuilder.append(callMessage);
				 * errorBuilder.append(" (").append(lemmaName).append(") ");
				 * errorBuilder.append(solverConfig.getId());
				 * errorBuilder.append(
				 * " unsat-core and the expected one are different and mutualy not included."
				 * ); return new SMTProverCallTestResult(smtProverCall,
				 * errorBuilder);
				 */
			}
			if (smtProverCall.isGoalNeeded() != expectedGoalNeed) {
				errorBuilder.append(callMessage);
				errorBuilder.append(" (").append(lemmaName);
				errorBuilder
						.append(") The extracted goal need wasn't the expected one.");
				return new SMTProverCallTestResult(smtProverCall, errorBuilder);
			}
		} catch (final IllegalArgumentException iae) {
			errorBuilder.append(iae.getMessage());
			return new SMTProverCallTestResult(smtProverCall, errorBuilder);
		}
		return new SMTProverCallTestResult(smtProverCall, errorBuilder);
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

		final ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, ff);

		// FIXME should not be PP because could this is used by veriT tests
		final SMTThroughPP translator = new SMTThroughPP(
				solverConfig.getSmtlibVersion());
		final SMTBenchmark benchmark = translate(translator, lemmaName, sequent);

		final SMTSignature signature = benchmark.getSignature();

		AbstractTests.testTypeEnvironmentSorts(signature, expectedSorts, "");
		AbstractTests.testTypeEnvironmentFuns(signature, expectedFuns, "");
		AbstractTests.testTypeEnvironmentPreds(signature, expectedPreds, "");
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(LAST_ALTERGO, ALT_ERGO, "",
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
		setSolverPreferences(LAST_CVC3, CVC3, args, smtlibVersion);
	}

	protected void setPreferencesForCvc4Test() {
		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "--lang smt";
		} else {
			args = "--lang smt2";
		}
		setSolverPreferences(LAST_CVC4, CVC4, args, smtlibVersion);
	}

	protected void setPreferencesForMathSat5Test() {
		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "-input=smt";
		} else {
			/**
			 * default is smt2
			 */
			args = "";
		}
		setSolverPreferences(LAST_MATHSAT5, MATHSAT5, args, smtlibVersion);
	}

	protected void setPreferencesForOpenSMTTest() {
		setSolverPreferences(LAST_OPENSMT, OPENSMT, "",
				solverConfig.getSmtlibVersion());
	}

	protected void setPreferencesForVeriTTest() {
		final SMTLIBVersion smtlibVersion = solverConfig.getSmtlibVersion();
		final String args;
		if (smtlibVersion.equals(V1_2)) {
			args = "--enable-e --max-time=2.9";
		} else {
			args = "-i smtlib2 --disable-print-success --enable-e --max-time=2.9";
		}
		setSolverPreferences(LAST_VERIT, VERIT, args, smtlibVersion);
	}

	protected void setPreferencesForVeriTProofTest() {
		/**
		 * smtlibVersion.equals(V2_0)
		 */
		setSolverPreferences(
				LAST_VERIT,
				VERIT,
				"-i smtlib2 --disable-print-success --proof=- --proof-version=1 --proof-prune --enable-e --max-time=2.9",
				solverConfig.getSmtlibVersion());
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
			Z3.toString(binaryName);
		} else {
			binaryName.append(LAST_Z3);
		}

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
		case CVC4:
			setPreferencesForCvc4Test();
			break;
		case MATHSAT5:
			setPreferencesForMathSat5Test();
			break;
		case OPENSMT:
			setPreferencesForOpenSMTTest();
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

	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedTrivial, final boolean expectedSolverResult) {
		doTest(translationApproach, lemmaName, inputHyps, inputGoal, te,
				expectedTrivial, expectedSolverResult, new StringBuilder());
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
			final boolean expectedTrivial, final boolean expectedSolverResult,
			final StringBuilder debugBuilder) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(translationApproach, lemmaName, hypotheses, goal,
				expectedTrivial, expectedSolverResult, debugBuilder);
	}

	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed) {
		doTest(translationApproach, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult, expectedUnsatCoreStr, expectedGoalNeed,
				new StringBuilder());
	}

	class SMTProverCallTestResult {
		private SMTProverCall smtProverCall;
		private StringBuilder errorBuffer = new StringBuilder("");

		public SMTProverCallTestResult(final SMTProverCall smtProverCall,
				final StringBuilder errorBuffer) {
			this.smtProverCall = smtProverCall;
			this.errorBuffer = errorBuffer;
		}

		public SMTProverCall getSmtProverCall() {
			return smtProverCall;
		}

		public StringBuilder getErrorBuffer() {
			return errorBuffer;
		}
	}

	protected void doTest(final SMTTranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
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

		final String testedSolverConfigId = solverConfig.getId();
		final SMTLIBVersion testedSmtlibVersion = solverConfig
				.getSmtlibVersion();

		/**
		 * Iter 1 : calls the prover with the expected unsat-core, to check if
		 * it is right
		 */
		debugBuilder.append("Iter 1\n");
		final Predicate goalXML = (expectedGoalNeed ? parsedGoal : parse("⊥",
				te));

		ISimpleSequent sequent = SimpleSequents.make(expectedHypotheses,
				goalXML, ff);
		final SMTProverCallTestResult iter1Result = smtProverCallTest("Iter 1",
				translationApproach, lemmaName, sequent, te,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed,
				debugBuilder);
		final String iter1ErrorBuffer = iter1Result.getErrorBuffer().toString();
		if (!iter1ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter1ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverConfigId,
					testedSmtlibVersion, translationApproach,
					iter1Result.getSmtProverCall());
			fail(iter1ErrorBuffer);
		}
		debugBuilder.append("\n");

		/**
		 * Iter 2 : calls the prover and check if the unsat-core is the expected
		 * one
		 */
		debugBuilder.append("Iter 2\n");
		sequent = SimpleSequents.make(parsedHypotheses, parsedGoal, ff);
		final SMTProverCallTestResult iter2Result = smtProverCallTest("Iter 2",
				translationApproach, lemmaName, sequent, te,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed,
				debugBuilder);
		final String iter2ErrorBuffer = iter2Result.getErrorBuffer().toString();
		if (!iter2ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter2ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverConfigId,
					testedSmtlibVersion, translationApproach,
					iter2Result.getSmtProverCall());
			fail(iter2ErrorBuffer);
		}
		debugBuilder.append("\n");

		/**
		 * Iter 3 : calls the prover with the returned unsat-core, to check if
		 * it is right
		 */
		debugBuilder.append("Iter 3\n");
		final Set<Predicate> neededHypothesesSet = iter2Result
				.getSmtProverCall().neededHypotheses();
		final List<Predicate> neededHypotheses;
		if (neededHypothesesSet != null) {
			neededHypotheses = new ArrayList<Predicate>(neededHypothesesSet);
		} else {
			neededHypotheses = new ArrayList<Predicate>();
		}
		final Predicate goalSolver = (iter2Result.getSmtProverCall()
				.isGoalNeeded() ? parsedGoal : parse("⊥", te));
		sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
		final SMTProverCallTestResult iter3Result = smtProverCallTest("Iter 3",
				translationApproach, lemmaName, sequent, te,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed,
				debugBuilder);
		final String iter3ErrorBuffer = iter3Result.getErrorBuffer().toString();
		if (!iter3ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter3ErrorBuffer).append("\n");
			/**
			 * Here we print performances of the iter 2 smt prover call because
			 * we just want the unsat core to be refused
			 */
			printPerf(debugBuilder, lemmaName, testedSolverConfigId,
					testedSmtlibVersion, translationApproach,
					iter2Result.getSmtProverCall());
			fail(iter3ErrorBuffer);
		}
		debugBuilder.append("\n");

		gotUnsatCore = true;

		/**
		 * Unsat-core checking : calls the other provers on the unsat-core, to
		 * check if it is right
		 */
		debugBuilder.append("unsat-core checking\n");
		final SMTSolver solver = solverConfig.getSolver();
		if (!solver.equals(Z3)) {
			setPreferencesForZ3Test();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult z3UCCheckResult = smtProverCallTest(
					"z3 unsat-core checking", translationApproach, lemmaName,
					sequent, expectedSolverResult, debugBuilder);
			final String z3UCCheckErrorBuffer = z3UCCheckResult
					.getErrorBuffer().toString();
			if (!z3UCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(z3UCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverConfigId,
						testedSmtlibVersion, translationApproach,
						iter3Result.getSmtProverCall());
				fail(z3UCCheckErrorBuffer);
			}
		}
		if (!solver.equals(CVC3)) {
			setPreferencesForCvc3Test();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult cvc3UCCheckResult = smtProverCallTest(
					"cvc3 unsat-core checking", translationApproach, lemmaName,
					sequent, expectedSolverResult, debugBuilder);
			final String cvc3UCCheckErrorBuffer = cvc3UCCheckResult
					.getErrorBuffer().toString();
			if (!cvc3UCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(cvc3UCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverConfigId,
						testedSmtlibVersion, translationApproach,
						iter3Result.getSmtProverCall());
				fail(cvc3UCCheckErrorBuffer);
			}
		}
		if (!solver.equals(ALT_ERGO)) {
			setPreferencesForAltErgoTest();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult altergoUCCheckResult = smtProverCallTest(
					"alt-ergo unsat-core checking", translationApproach,
					lemmaName, sequent, expectedSolverResult, debugBuilder);
			final String altergoUCCheckErrorBuffer = altergoUCCheckResult
					.getErrorBuffer().toString();
			if (!altergoUCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(altergoUCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverConfigId,
						testedSmtlibVersion, translationApproach,
						iter3Result.getSmtProverCall());
				fail(altergoUCCheckErrorBuffer);
			}
		}
		if (!solver.equals(VERIT)) {
			setPreferencesForVeriTTest();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult veritUCCheckResult = smtProverCallTest(
					"veriT unsat-core checking", translationApproach,
					lemmaName, sequent, expectedSolverResult, debugBuilder);
			final String veritUCCheckErrorBuffer = veritUCCheckResult
					.getErrorBuffer().toString();
			if (!veritUCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(veritUCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverConfigId,
						testedSmtlibVersion, translationApproach,
						iter3Result.getSmtProverCall());
				fail(veritUCCheckErrorBuffer);
			}
		}
		debugBuilder.append("\n");
		unsatCoreChecked = true;

		printPerf(debugBuilder, lemmaName, testedSolverConfigId,
				testedSmtlibVersion, translationApproach,
				iter3Result.getSmtProverCall());
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
