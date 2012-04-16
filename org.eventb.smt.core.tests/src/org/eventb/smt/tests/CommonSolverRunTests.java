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
import static org.eventb.smt.core.preferences.SMTSolverFactory.newSolver;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ENABLED;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;
import static org.eventb.smt.core.provers.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.provers.SolverKind.CVC3;
import static org.eventb.smt.core.provers.SolverKind.CVC4;
import static org.eventb.smt.core.provers.SolverKind.MATHSAT5;
import static org.eventb.smt.core.provers.SolverKind.OPENSMT;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.provers.SolverKind.Z3;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
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
	public static final String LAST_ALTERGO_BIN = "alt-ergo-nightly-r217";
	public static final String LAST_CVC3_BIN = "cvc3-2011-10-05";
	public static final String LAST_CVC4_BIN = "cvc4-2011-12-11";
	public static final String LAST_MATHSAT5_BIN = "mathsat5-smtcomp2011";
	public static final String LAST_OPENSMT_BIN = "opensmt-20101017";
	public static final String LAST_VERIT_BIN = "veriT-dev-r2863";
	public static final String LAST_Z3_BIN = "z3-3.2";
	public static final String BUNDLED_VERIT = "org.eventb.smt.verit.verit_smt2";
	public static final String BUNDLED_CVC3 = "org.eventb.smt.cvc3.cvc3_smt2";
	public static final boolean GET_UNSAT_CORE = true;
	public static final IPath DEFAULT_TEST_TRANSLATION_PATH = new Path(
			System.getProperty("user.home") + File.separatorChar
					+ "rodin_smtlib_temp_files");

	public static final ISMTSolver LAST_ALTERGO = newSolver(LAST_ALTERGO_BIN,
			LAST_ALTERGO_BIN, ALT_ERGO, makeSolverPath(LAST_ALTERGO_BIN));
	public static final ISMTSolver LAST_CVC3 = newSolver(LAST_CVC3_BIN,
			LAST_CVC3_BIN, CVC3, makeSolverPath(LAST_CVC3_BIN));
	public static final ISMTSolver LAST_CVC4 = newSolver(LAST_CVC4_BIN,
			LAST_CVC4_BIN, CVC4, makeSolverPath(LAST_CVC4_BIN));
	public static final ISMTSolver LAST_MATHSAT5 = newSolver(LAST_MATHSAT5_BIN,
			LAST_MATHSAT5_BIN, MATHSAT5, makeSolverPath(LAST_MATHSAT5_BIN));
	public static final ISMTSolver LAST_OPENSMT = newSolver(LAST_OPENSMT_BIN,
			LAST_OPENSMT_BIN, OPENSMT, makeSolverPath(LAST_OPENSMT_BIN));
	public static final ISMTSolver LAST_VERIT = newSolver(LAST_VERIT_BIN,
			LAST_VERIT_BIN, VERIT, makeSolverPath(LAST_VERIT_BIN));
	public static final ISMTSolver LAST_Z3 = newSolver(LAST_Z3_BIN,
			LAST_Z3_BIN, Z3, makeSolverPath(LAST_Z3_BIN));

	public static final ISolverConfig ALTERGO_SMT1 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", V1_2);
	public static final ISolverConfig ALTERGO_SMT2 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", V2_0);
	public static final ISolverConfig CVC3_SMT1 = makeConfig(LAST_CVC3_BIN,
			LAST_CVC3, "-lang smt", V1_2);
	public static final ISolverConfig CVC3_SMT2 = makeConfig(LAST_CVC3_BIN,
			LAST_CVC3, "-lang smt2", V2_0);
	public static final ISolverConfig CVC4_SMT1 = makeConfig(LAST_CVC4_BIN,
			LAST_CVC4, "--lang smt", V1_2);
	public static final ISolverConfig CVC4_SMT2 = makeConfig(LAST_CVC4_BIN,
			LAST_CVC4, "--lang smt2", V2_0);
	public static final ISolverConfig MATHSAT5_SMT1 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "-input=smt", V1_2);
	public static final ISolverConfig MATHSAT5_SMT2 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "", V2_0);
	public static final ISolverConfig OPENSMT_SMT1 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", V1_2);
	public static final ISolverConfig OPENSMT_SMT2 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", V2_0);
	public static final ISolverConfig VERIT_SMT1 = makeConfig(LAST_VERIT_BIN,
			LAST_VERIT, "--enable-e --max-time=2.9", V1_2);
	public static final ISolverConfig VERIT_SMT2 = makeConfig(LAST_VERIT_BIN,
			LAST_VERIT,
			"-i smtlib2 --disable-print-success --enable-e --max-time=2.9",
			V2_0);
	public static final ISolverConfig Z3_SMT1 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "", V1_2);
	public static final ISolverConfig Z3_SMT2 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "-smt2", V2_0);

	private static final NullProofMonitor MONITOR = new NullProofMonitor();

	private final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	static File smtFolder;
	protected static boolean PERFORMANCE = true;

	/**
	 * H /\ ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static boolean VALID = true;
	protected static boolean TRIVIAL = true;

	protected Set<org.eventb.smt.utils.Theory> theories;
	protected String poName;

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

	public CommonSolverRunTests(final SolverKind solverKind,
			final Set<Theory> theories, final SMTLIBVersion smtlibVersion,
			final boolean getUnsatCore) {
		setTranslationPreferences();
		setSolvers();
		this.theories = theories;
		if (getUnsatCore && solverKind.equals(SolverKind.VERIT)) {
			setPreferencesForBundledVeriT();
		} else {
			setSolverPreferences(solverKind, smtlibVersion);
		}
	}

	private static ISolverConfig makeConfig(final String id,
			final ISMTSolver solver, final String args,
			final SMTLIBVersion smtlibVersion) {
		final String newID = id
				+ (smtlibVersion.equals(V1_2) ? "-SMT1" : "-SMT2");
		return newConfig(newID, newID, solver.getID(), args, smtlibVersion);
	}

	private static void setSolvers() {
		final ISMTSolversPreferences solversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		solversPrefs.add(LAST_ALTERGO);
		solversPrefs.add(LAST_CVC3);
		solversPrefs.add(LAST_CVC4);
		solversPrefs.add(LAST_MATHSAT5);
		solversPrefs.add(LAST_OPENSMT);
		solversPrefs.add(LAST_VERIT);
		solversPrefs.add(LAST_Z3);
		solversPrefs.save();
	}

	private static IPath makeSolverPath(final String binaryName) {
		final StringBuilder solverPathBuilder = new StringBuilder();
		solverPathBuilder.append(System.getProperty("user.home"));
		solverPathBuilder.append(System.getProperty("file.separator"));
		solverPathBuilder.append("bin");
		solverPathBuilder.append(System.getProperty("file.separator"));
		solverPathBuilder.append(binaryName);

		if (System.getProperty("os.name").startsWith("Windows")) {
			solverPathBuilder.append(".exe");
		}

		return new Path(solverPathBuilder.toString());
	}

	private static void setTranslationPreferences() {
		final ITranslationPreferences translationPrefs = getPreferenceManager()
				.getTranslationPrefs();
		translationPrefs.setTranslationPath(DEFAULT_TEST_TRANSLATION_PATH
				.toOSString());
		translationPrefs.setVeriTPath(LAST_VERIT);
		translationPrefs.save();
	}

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 */
	protected static void setSolverPreferences(final SolverKind kind,
			final SMTLIBVersion smtlibVersion) {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		configsPrefs.loadDefault();
		switch (smtlibVersion) {
		case V1_2:
			switch (kind) {
			case ALT_ERGO:
				configsPrefs.add(ALTERGO_SMT1);
				break;

			case CVC3:
				configsPrefs.add(CVC3_SMT1);
				break;

			case CVC4:
				configsPrefs.add(CVC4_SMT1);
				break;

			case MATHSAT5:
				configsPrefs.add(MATHSAT5_SMT1);
				break;

			case OPENSMT:
				configsPrefs.add(OPENSMT_SMT1);
				break;

			case VERIT:
				configsPrefs.add(VERIT_SMT1);
				break;

			case Z3:
				configsPrefs.add(Z3_SMT1);
				break;

			default:
				// TODO
				break;
			}
			break;

		default:
			switch (kind) {
			case ALT_ERGO:
				configsPrefs.add(ALTERGO_SMT2);
				break;

			case CVC3:
				configsPrefs.add(CVC3_SMT2);
				break;

			case CVC4:
				configsPrefs.add(CVC4_SMT2);
				break;

			case MATHSAT5:
				configsPrefs.add(MATHSAT5_SMT2);
				break;

			case OPENSMT:
				configsPrefs.add(OPENSMT_SMT2);
				break;

			case VERIT:
				configsPrefs.add(VERIT_SMT2);
				break;

			case Z3:
				configsPrefs.add(Z3_SMT2);
				break;

			default:
				// TODO
				break;
			}
			break;
		}
		configsPrefs.setConfigEnabled(BUNDLED_VERIT, !ENABLED);
		configsPrefs.setConfigEnabled(BUNDLED_CVC3, !ENABLED);
		configsPrefs.save();
	}

	protected static void setSolverV1_2Preferences(final SolverKind kind) {
		setSolverPreferences(kind, V1_2);
	}

	protected static void setSolverV2_0Preferences(final SolverKind kind) {
		setSolverPreferences(kind, V2_0);
	}

	protected static void setPreferencesForBundledVeriT() {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		configsPrefs.setConfigEnabled(BUNDLED_VERIT, ENABLED);
		configsPrefs.save();
	}

	protected static void setPreferencesForBundledCvc3() {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		configsPrefs.setConfigEnabled(BUNDLED_VERIT, !ENABLED);
		configsPrefs.setConfigEnabled(BUNDLED_CVC3, ENABLED);
		configsPrefs.save();
	}

	private void printPerf(final StringBuilder debugBuilder,
			final String lemmaName, final String solverConfigId,
			final SMTLIBVersion smtlibVersion,
			final TranslationApproach translationApproach,
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
	private void doTest(final TranslationApproach translationApproach,
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

		final ISolverConfig solverConfig = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next();

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName,
						getPreferenceManager().getTranslationPrefs()
								.getTranslationPath(), getPreferenceManager()
								.getTranslationPrefs().getVeriTPath()) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, getPreferenceManager()
								.getTranslationPrefs().getTranslationPath()) {
					// nothing to do
				};
				break;
			}

			smtProverCalls.add(smtProverCall);
			smtProverCall.run();

			printPerf(debugBuilder, lemmaName, solverConfig.getID(),
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
			final TranslationApproach translationApproach,
			final String lemmaName, final ISimpleSequent sequent,
			final boolean expectedSolverResult, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		final ISolverConfig solverConfig = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next();

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName,
						getPreferenceManager().getTranslationPrefs()
								.getTranslationPath(), getPreferenceManager()
								.getTranslationPrefs().getVeriTPath()) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, getPreferenceManager()
								.getTranslationPrefs().getTranslationPath()) {
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
			final TranslationApproach translationApproach,
			final String lemmaName, final ISimpleSequent sequent,
			final ITypeEnvironment te, final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		final ISolverConfig solverConfig = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next();

		try {
			switch (translationApproach) {
			case USING_VERIT:
				// Create an instance of SmtVeriTCall
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, solverConfig, lemmaName,
						getPreferenceManager().getTranslationPrefs()
								.getTranslationPath(), getPreferenceManager()
								.getTranslationPrefs().getVeriTPath()) {
					// nothing to do
				};
				break;

			default: // USING_PP
				// Create an instance of SmtPPCall
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						solverConfig, lemmaName, getPreferenceManager()
								.getTranslationPrefs().getTranslationPath()) {
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
		final SMTThroughPP translator = new SMTThroughPP(getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next()
				.getSmtlibVersion());
		final SMTBenchmark benchmark = translate(translator, lemmaName, sequent);

		final SMTSignature signature = benchmark.getSignature();

		AbstractTests.testTypeEnvironmentSorts(signature, expectedSorts, "");
		AbstractTests.testTypeEnvironmentFuns(signature, expectedFuns, "");
		AbstractTests.testTypeEnvironmentPreds(signature, expectedPreds, "");
	}

	protected void doTest(final TranslationApproach translationApproach,
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
	protected void doTest(final TranslationApproach translationApproach,
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

	protected void doTest(final TranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed, final boolean perf) {
		if (perf) {
			doTest(translationApproach, lemmaName, inputHyps, inputGoal, te,
				expectedSolverResult, expectedUnsatCoreStr, expectedGoalNeed,
				new StringBuilder());
		} else {
			doTest(translationApproach, lemmaName, inputHyps, inputGoal, te,
					expectedSolverResult, expectedUnsatCoreStr, expectedGoalNeed);
		}
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

	protected void doTest(final TranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed) {
		final StringBuilder debugBuilder = new StringBuilder();

		final ISolverConfig solverConfig = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next();

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

		final String testedSolverConfigId = solverConfig.getID();
		final SMTLIBVersion testedSmtlibVersion = solverConfig
				.getSmtlibVersion();

		ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses, parsedGoal, ff);
		final SMTProverCallTestResult iter2Result = smtProverCallTest("Unsat core extraction",
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
	}

	protected void doTest(final TranslationApproach translationApproach,
			final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {

		final ISolverConfig solverConfig = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs().iterator().next();

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

		final String testedSolverConfigId = solverConfig.getID();
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

		final ISMTSolver solver = getPreferenceManager().getSMTSolversPrefs()
				.get(getPreferenceManager().getSolverConfigsPrefs()
						.getEnabledConfigs().iterator().next().getSolverId());
		final SolverKind solverKind = solver.getKind();
		if (!solverKind.equals(Z3)) {
			setSolverPreferences(Z3, V2_0);
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
		if (!solverKind.equals(CVC3)) {
			setSolverPreferences(CVC3, V2_0);
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
		if (!solverKind.equals(ALT_ERGO)) {
			setSolverPreferences(ALT_ERGO, V2_0);
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
		if (!solverKind.equals(VERIT)) {
			setSolverPreferences(VERIT, V2_0);
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
