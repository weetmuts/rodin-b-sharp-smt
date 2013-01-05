/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import static org.eventb.smt.core.SMTCore.getBundledSolvers;
import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
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
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.prefs.SimplePreferences;
import org.eventb.smt.core.internal.provers.SMTConfiguration;
import org.eventb.smt.core.internal.provers.SMTPPCall;
import org.eventb.smt.core.internal.provers.SMTProverCall;
import org.eventb.smt.core.internal.provers.SMTVeriTCall;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.ISMTConfiguration;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.eventb.smt.utils.Theory;
import org.junit.After;

public abstract class CommonSolverRunTests extends AbstractTests {

	/**
	 * bundled configs ids
	 */
	public static final String BUNDLED_VERIT_ID_PREFIX = "bundled_veriT";
	public static final String BUNDLED_CVC3_ID_PREFIX = "bundled_CVC3";

	public static final String BUNDLED_VERIT_PP_SMT2_ID = "veriT SMT2";
	public static final String BUNDLED_CVC3_PP_SMT2_ID = "CVC3 SMT2";

	public static final IPath DEFAULT_TEST_TRANSLATION_PATH = new Path(
			System.getProperty("user.home")).append("rodin_smtlib_temp_files");

	/**
	 * bundled solvers (used to make new configurations)
	 */
	public static final ISolverDescriptor BUNDLED_VERIT = findBundledSolverByName("veriT (bundled)");
	public static final ISolverDescriptor BUNDLED_CVC3 = findBundledSolverByName("CVC3 (bundled)");

	/**
	 * Bundled veriT configurations
	 */
	public static final IConfigDescriptor BUNDLED_VERIT_PP_SMT1 = makeConfig(
			BUNDLED_VERIT_ID_PREFIX, BUNDLED_VERIT,
			"--enable-e --max-time=2.9", USING_PP, V1_2);
	public static final IConfigDescriptor BUNDLED_VERIT_VT_SMT1 = makeConfig(
			BUNDLED_VERIT_ID_PREFIX, BUNDLED_VERIT,
			"--enable-e --max-time=2.9", USING_VERIT, V1_2);
	public static final IConfigDescriptor BUNDLED_VERIT_VT_SMT2 = makeConfig(
			BUNDLED_VERIT_ID_PREFIX, BUNDLED_VERIT,
			"-i smtlib2 --disable-print-success --enable-e --max-time=2.9",
			USING_VERIT, V2_0);

	/**
	 * Bundled CVC3 configurations
	 */
	public static final IConfigDescriptor BUNDLED_CVC3_PP_SMT1 = makeConfig(
			BUNDLED_CVC3_ID_PREFIX, BUNDLED_CVC3, "-lang smt", USING_PP, V1_2);
	public static final IConfigDescriptor BUNDLED_CVC3_VT_SMT1 = makeConfig(
			BUNDLED_CVC3_ID_PREFIX, BUNDLED_CVC3, "-lang smt", USING_VERIT,
			V1_2);
	public static final IConfigDescriptor BUNDLED_CVC3_VT_SMT2 = makeConfig(
			BUNDLED_CVC3_ID_PREFIX, BUNDLED_CVC3, "-lang smt2", USING_VERIT,
			V2_0);

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

	protected ISMTConfiguration configuration;

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

	private static ISolverDescriptor findBundledSolverByName(String name) {
		final ISolverDescriptor[] solvers = getBundledSolvers();
		for (ISolverDescriptor solver : solvers) {
			if (name.equals(solver.getName())) {
				return solver;
			}
		}
		throw new IllegalArgumentException("No bundled solver named " + name);
	}

	public CommonSolverRunTests(final SolverKind solverKind,
			final Set<Theory> theories,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion, final boolean getUnsatCore) {
		this();
		this.theories = theories;
		setSolverPreferences(solverKind, translationApproach, smtlibVersion);
	}

	protected static IConfigDescriptor makeConfig(final String id,
			final ISolverDescriptor solver, final String args,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion) {
		final String newID = id + "_" + translationApproach.toString()
				+ (smtlibVersion.equals(V1_2) ? "_SMT1" : "_SMT2");
		return newConfigDescriptor(newID, solver.getName(), args,
				translationApproach, smtlibVersion);
	}

	protected static void setTranslationPreferences() {
		SimplePreferences.setTranslationPath(DEFAULT_TEST_TRANSLATION_PATH);
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
					configuration = new SMTConfiguration(BUNDLED_CVC3_VT_SMT1,
							BUNDLED_CVC3);
					break;

				case VERIT:
					configuration = new SMTConfiguration(BUNDLED_VERIT_VT_SMT1,
							BUNDLED_VERIT);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind "
							+ kind.name());
				}
			} else {
				/**
				 * smtlibVersion.equals(V2_0)
				 */
				switch (kind) {

				case CVC3:
					configuration = new SMTConfiguration(BUNDLED_CVC3_VT_SMT2,
							BUNDLED_CVC3);
					break;

				case VERIT:
					configuration = new SMTConfiguration(BUNDLED_VERIT_VT_SMT2,
							BUNDLED_VERIT);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind "
							+ kind.name());
				}
			}
		} else {
			if (smtlibVersion.equals(V1_2)) {
				switch (kind) {

				case CVC3:
					configuration = new SMTConfiguration(BUNDLED_CVC3_PP_SMT1,
							BUNDLED_CVC3);
					break;

				case VERIT:
					configuration = new SMTConfiguration(BUNDLED_VERIT_PP_SMT1,
							BUNDLED_VERIT);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind "
							+ kind.name());
				}
			} else {
				/**
				 * smtlibVersion.equals(V2_0)
				 */
				switch (kind) {

				case CVC3:
					configuration = SMTCore.getSMTConfiguration("CVC3 SMT2");
					break;

				case VERIT:
					configuration = SMTCore.getSMTConfiguration("veriT SMT2");
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind "
							+ kind.name());
				}
			}
		}
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
			final StringBuilder debugBuilder) {
		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, ff);

		final SMTProverCall smtProverCall;
		final TranslationApproach approach = configuration.getTranslationApproach();
		switch (approach) {
		case USING_VERIT:
			smtProverCall = new SMTVeriTCall(sequent, MONITOR, debugBuilder,
					configuration, lemmaName);
			break;

		case USING_PP:
			smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
					configuration, lemmaName);
			break;
		default:
			fail("unknown translation approach: " + approach);
			return;
		}

		smtProverCalls.add(smtProverCall);
		smtProverCall.run();

		printPerf(debugBuilder, lemmaName, configuration.getSolverName(),
				configuration.getSmtlibVersion(),
				approach, smtProverCall);

		assertEquals(expectedTrivial, smtProverCall.benchmarkIsNull());
		assertEquals("The result of the SMT prover wasn't the expected one.",
				expectedSolverResult, smtProverCall.isValid());
	}

	protected SMTProverCallTestResult smtProverCallTest(
			final String callMessage, final String lemmaName,
			final ISimpleSequent sequent, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
		final SMTProverCall smtProverCall;
		final StringBuilder errorBuilder = new StringBuilder("");

		switch (configuration.getTranslationApproach()) {
		case USING_VERIT:
			smtProverCall = new SMTVeriTCall(sequent, MONITOR, debugBuilder,
					configuration, lemmaName);
			break;

		default: // USING_PP
			smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
					configuration, lemmaName);
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
				 * errorBuilder.append(" (").append(lemmaName); errorBuilder .
				 * append(") The expected unsat-core is smaller than the ");
				 * errorBuilder .append(solverConfig.getId()).append(" one.");
				 * return new SMTProverCallTestResult(smtProverCall,
				 * errorBuilder);
				 */
			}
		} else if (expectedContainsExtracted) {
			errorBuilder.append(callMessage);
			errorBuilder.append(" (").append(lemmaName).append(") ");
			errorBuilder.append(configuration.getName());
			errorBuilder
					.append(" unsat-core is smaller than the expected one.");
			return new SMTProverCallTestResult(smtProverCall, errorBuilder);
		} else {
			/*
			 * errorBuilder.append(callMessage);
			 * errorBuilder.append(" (").append(lemmaName).append(") ");
			 * errorBuilder.append(solverConfig.getId()); errorBuilder.append(
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
				configuration.getSmtlibVersion());
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

		final String testedSolverName = configuration.getSolverName();
		final TranslationApproach testedTranslationApproach = configuration
				.getTranslationApproach();
		final SMTLIBVersion testedSmtlibVersion = configuration
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
