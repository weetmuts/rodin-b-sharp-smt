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

import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;
import static org.eventb.smt.utils.Theory.getComboLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.provers.SMTPPCall;
import org.eventb.smt.core.internal.provers.SMTProverCall;
import org.eventb.smt.core.internal.provers.SMTVeriTCall;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;
import org.eventb.smt.core.preferences.ITranslationPreferences;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.eventb.smt.utils.Theory;
import org.junit.After;

public abstract class CommonSolverRunTests extends AbstractTests {
	/**
	 * bundled solvers ids
	 */
	public static final String BUNDLED_VERIT_ID = "org.eventb.smt.verit.bundled";
	public static final String BUNDLED_CVC3_ID = "org.eventb.smt.cvc3.bundled";
	/**
	 * bundled configs ids
	 */
	public static final String BUNDLED_VERIT_ID_PREF = "bundled_veriT";
	public static final String BUNDLED_CVC3_ID_PREF = "bundled_CVC3";
	public static final String BUNDLED_VERIT_PP_SMT2_ID = "org.eventb.smt.verit.verit_pp_smt2";
	public static final String BUNDLED_CVC3_PP_SMT2_ID = "org.eventb.smt.cvc3.cvc3_pp_smt2";

	public static final IPath DEFAULT_TEST_TRANSLATION_PATH = new Path(
			System.getProperty("user.home") + File.separatorChar
					+ "rodin_smtlib_temp_files");

	/**
	 * bundled solvers (used to make new configurations)
	 */
	public static final ISMTSolver BUNDLED_VERIT;
	public static final ISMTSolver BUNDLED_CVC3;
	static {
		getPreferenceManager().getSMTSolversPrefs().loadDefault();
		getPreferenceManager().getSMTSolversPrefs().save();
		getPreferenceManager().getSolverConfigsPrefs().loadDefault();
		getPreferenceManager().getSolverConfigsPrefs().save();
		BUNDLED_VERIT = getPreferenceManager().getSMTSolversPrefs().get(
				BUNDLED_VERIT_ID);
		BUNDLED_CVC3 = getPreferenceManager().getSMTSolversPrefs().get(
				BUNDLED_CVC3_ID);
	}

	/**
	 * Bundled veriT configurations
	 */
	public static final ISolverConfig BUNDLED_VERIT_PP_SMT1 = makeConfig(
			BUNDLED_VERIT_ID_PREF, BUNDLED_VERIT, "--enable-e --max-time=2.9",
			USING_PP, V1_2);
	public static final ISolverConfig BUNDLED_VERIT_VT_SMT1 = makeConfig(
			BUNDLED_VERIT_ID_PREF, BUNDLED_VERIT, "--enable-e --max-time=2.9",
			USING_VERIT, V1_2);
	public static final ISolverConfig BUNDLED_VERIT_VT_SMT2 = makeConfig(
			BUNDLED_VERIT_ID_PREF, BUNDLED_VERIT,
			"-i smtlib2 --disable-print-success --enable-e --max-time=2.9",
			USING_VERIT, V2_0);

	/**
	 * Bundled CVC3 configurations
	 */
	public static final ISolverConfig BUNDLED_CVC3_PP_SMT1 = makeConfig(
			BUNDLED_CVC3_ID_PREF, BUNDLED_CVC3, "-lang smt", USING_PP, V1_2);
	public static final ISolverConfig BUNDLED_CVC3_VT_SMT1 = makeConfig(
			BUNDLED_CVC3_ID_PREF, BUNDLED_CVC3, "-lang smt", USING_VERIT, V1_2);
	public static final ISolverConfig BUNDLED_CVC3_VT_SMT2 = makeConfig(
			BUNDLED_CVC3_ID_PREF, BUNDLED_CVC3, "-lang smt2", USING_VERIT, V2_0);

	protected static final NullProofMonitor MONITOR = new NullProofMonitor();

	protected final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	static File smtFolder;

	protected static final boolean GET_UNSAT_CORE = true;
	protected static final boolean PERFORMANCE = true;
	protected static final boolean BUNDLED = true;

	/**
	 * H /\ ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static final boolean VALID = true;
	protected static final boolean TRIVIAL = true;

	protected Set<org.eventb.smt.utils.Theory> theories;
	protected String poName;
	protected ISolverConfig solverConfig;
	protected ISMTSolver solver;

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

	public CommonSolverRunTests() {
		setTranslationPreferences();
	}

	public CommonSolverRunTests(final SolverKind solverKind,
			final Set<Theory> theories,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion, final boolean getUnsatCore) {
		this();
		this.theories = theories;
		setSolverPreferences(solverKind, translationApproach, smtlibVersion);
	}

	protected static ISolverConfig makeConfig(final String id,
			final ISMTSolver solver, final String args,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion) {
		final String newID = id + "_" + translationApproach.toString()
				+ (smtlibVersion.equals(V1_2) ? "_SMT1" : "_SMT2");
		return newConfig(newID, newID, solver.getID(), args,
				translationApproach, smtlibVersion);
	}

	protected static void setTranslationPreferences() {
		final ITranslationPreferences translationPrefs = getPreferenceManager()
				.getTranslationPrefs();
		translationPrefs.setTranslationPath(DEFAULT_TEST_TRANSLATION_PATH
				.toOSString());
		translationPrefs.save();
	}

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 */
	protected void setSolverPreferences(final SolverKind kind,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion) {
		if (translationApproach.equals(TranslationApproach.USING_VERIT)) {
			if (smtlibVersion.equals(V1_2)) {
				switch (kind) {

				case CVC3:
					solverConfig = BUNDLED_CVC3_VT_SMT1;
					solver = BUNDLED_CVC3;
					break;

				case VERIT:
					solverConfig = BUNDLED_VERIT_VT_SMT1;
					solver = BUNDLED_VERIT;
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			} else {
				/**
				 * smtlibVersion.equals(V2_0)
				 */
				switch (kind) {

				case CVC3:
					solverConfig = BUNDLED_CVC3_VT_SMT2;
					solver = BUNDLED_CVC3;
					break;

				case VERIT:
					solverConfig = BUNDLED_VERIT_VT_SMT2;
					solver = BUNDLED_VERIT;
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			}
		} else {
			if (smtlibVersion.equals(V1_2)) {
				switch (kind) {

				case CVC3:
					solverConfig = BUNDLED_CVC3_PP_SMT1;
					solver = BUNDLED_CVC3;
					break;

				case VERIT:
					solverConfig = BUNDLED_VERIT_PP_SMT1;
					solver = BUNDLED_VERIT;
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			} else {
				/**
				 * smtlibVersion.equals(V2_0)
				 */
				switch (kind) {

				case CVC3:
					setPreferencesForBundledCvc3();
					break;

				case VERIT:
					setPreferencesForBundledVeriT();
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			}
		}
	}

	protected void setPreferencesForBundledVeriT() {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		configsPrefs.loadDefault();
		solverConfig = configsPrefs.getSolverConfig(BUNDLED_VERIT_PP_SMT2_ID);
		final ISMTSolversPreferences solversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		solver = solversPrefs.get(solverConfig.getSolverId());
	}

	protected void setPreferencesForBundledCvc3() {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		solverConfig = configsPrefs.getSolverConfig(BUNDLED_CVC3_PP_SMT2_ID);
		final ISMTSolversPreferences solversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		solver = solversPrefs.get(solverConfig.getSolverId());
	}

	protected void printPerf(final StringBuilder debugBuilder,
			final String lemmaName, final String solverId,
			final SMTLIBVersion smtlibVersion,
			final TranslationApproach translationApproach,
			final SMTProverCall smtProverCall) {
		debugBuilder.append("<PERF_ENTRY>\n");
		debugBuilder.append("Lemma ").append(lemmaName).append("\n");
		debugBuilder.append("Solver ").append(solverId).append("\n");
		debugBuilder.append(getComboLevel(theories).getName()).append("\n");
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
	private void doTest(final String lemmaName,
			final List<Predicate> parsedHypotheses, final Predicate parsedGoal,
			final boolean expectedTrivial, final boolean expectedSolverResult,
			final StringBuilder debugBuilder) throws IllegalArgumentException {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, ff);

		final SMTProverCall smtProverCall;

		try {
			switch (solverConfig.getTranslationApproach()) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, solver, lemmaName) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, solver, lemmaName) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			printPerf(debugBuilder, lemmaName, solver.getName(),
					solverConfig.getSmtlibVersion(),
					solverConfig.getTranslationApproach(), smtProverCall);

			assertEquals(expectedTrivial, smtProverCall.benchmarkIsNull());
			assertEquals(
					"The result of the SMT prover wasn't the expected one.",
					expectedSolverResult, smtProverCall.isValid());
		} catch (final IllegalArgumentException iae) {
			fail(iae.getMessage());
		}
	}

	protected SMTProverCallTestResult smtProverCallTest(
			final String callMessage, final String lemmaName,
			final ISimpleSequent sequent, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		try {
			switch (solverConfig.getTranslationApproach()) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, solver, lemmaName) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, solver, lemmaName) {
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
				errorBuilder.append(solverConfig.getName());
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

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedTrivial, final boolean expectedSolverResult) {
		doTest(lemmaName, inputHyps, inputGoal, te, expectedTrivial,
				expectedSolverResult, new StringBuilder());
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
	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedTrivial, final boolean expectedSolverResult,
			final StringBuilder debugBuilder) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();

		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, te));
		}

		final Predicate goal = parse(inputGoal, te);

		doTest(lemmaName, hypotheses, goal, expectedTrivial,
				expectedSolverResult, debugBuilder);
	}

	public class SMTProverCallTestResult {
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

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed) {
		final StringBuilder debugBuilder = new StringBuilder();

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

		final String testedSolverName = solver.getName();
		final TranslationApproach testedTranslationApproach = solverConfig
				.getTranslationApproach();
		final SMTLIBVersion testedSmtlibVersion = solverConfig
				.getSmtlibVersion();

		ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, ff);
		final SMTProverCallTestResult iter2Result = smtProverCallTest(
				"Unsat core extraction", lemmaName, sequent, te,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed,
				debugBuilder);
		final String iter2ErrorBuffer = iter2Result.getErrorBuffer().toString();
		if (!iter2ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter2ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
					testedSmtlibVersion, testedTranslationApproach,
					iter2Result.getSmtProverCall());
			fail(iter2ErrorBuffer);
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

	@After
	public void finalizeSolverProcess() {
		for (final SMTProverCall smtProverCall : smtProverCalls) {
			smtProverCall.cleanup();
		}
	}
}
