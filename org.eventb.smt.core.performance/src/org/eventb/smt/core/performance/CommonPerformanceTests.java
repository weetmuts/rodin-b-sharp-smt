/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance;

import static org.eventb.smt.core.SMTCore.newSolverDescriptor;
import static org.eventb.smt.core.provers.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.provers.SolverKind.CVC3;
import static org.eventb.smt.core.provers.SolverKind.CVC4;
import static org.eventb.smt.core.provers.SolverKind.MATHSAT5;
import static org.eventb.smt.core.provers.SolverKind.OPENSMT;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.provers.SolverKind.Z3;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.core.internal.provers.SMTConfiguration;
import org.eventb.smt.core.internal.provers.SMTPPCall;
import org.eventb.smt.core.internal.provers.SMTProverCall;
import org.eventb.smt.core.internal.provers.SMTVeriTCall;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.utils.Theory;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class CommonPerformanceTests extends CommonSolverRunTests {
	public static final String LAST_ALTERGO_BIN = "alt-ergo-nightly-r217";
	public static final String LAST_CVC3_BIN = "cvc3-2011-10-05";
	public static final String LAST_CVC4_BIN = "cvc4-2011-12-11";
	public static final String LAST_MATHSAT5_BIN = "mathsat5-smtcomp2011";
	public static final String LAST_OPENSMT_BIN = "opensmt-20101017";
	public static final String LAST_VERIT_BIN = "veriT-dev-r2863";
	public static final String LAST_Z3_BIN = "z3-3.2";

	/**
	 * External solvers
	 */
	public static final ISolverDescriptor LAST_ALTERGO = newSolverDescriptor(
			LAST_ALTERGO_BIN, ALT_ERGO, makeSolverPath(LAST_ALTERGO_BIN));
	public static final ISolverDescriptor LAST_CVC3 = newSolverDescriptor(
			LAST_CVC3_BIN, CVC3, makeSolverPath(LAST_CVC3_BIN));
	public static final ISolverDescriptor LAST_CVC4 = newSolverDescriptor(
			LAST_CVC4_BIN, CVC4, makeSolverPath(LAST_CVC4_BIN));
	public static final ISolverDescriptor LAST_MATHSAT5 = newSolverDescriptor(
			LAST_MATHSAT5_BIN, MATHSAT5, makeSolverPath(LAST_MATHSAT5_BIN));
	public static final ISolverDescriptor LAST_OPENSMT = newSolverDescriptor(
			LAST_OPENSMT_BIN, OPENSMT, makeSolverPath(LAST_OPENSMT_BIN));
	public static final ISolverDescriptor LAST_VERIT = newSolverDescriptor(
			LAST_VERIT_BIN, VERIT, makeSolverPath(LAST_VERIT_BIN));
	public static final ISolverDescriptor LAST_Z3 = newSolverDescriptor(
			LAST_Z3_BIN, Z3, makeSolverPath(LAST_Z3_BIN));

	/**
	 * External Alt-Ergo configurations
	 */
	public static final IConfigDescriptor ALTERGO_PP_SMT1 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", USING_PP, V1_2);
	public static final IConfigDescriptor ALTERGO_VERIT_SMT1 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", USING_VERIT, V1_2);
	public static final IConfigDescriptor ALTERGO_PP_SMT2 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", USING_PP, V2_0);
	public static final IConfigDescriptor ALTERGO_VERIT_SMT2 = makeConfig(
			LAST_ALTERGO_BIN, LAST_ALTERGO, "", USING_VERIT, V2_0);

	/**
	 * External CVC3 configurations
	 */
	public static final IConfigDescriptor CVC3_PP_SMT1 = makeConfig(LAST_CVC3_BIN,
			LAST_CVC3, "-lang smt", USING_PP, V1_2);
	public static final IConfigDescriptor CVC3_VERIT_SMT1 = makeConfig(
			LAST_CVC3_BIN, LAST_CVC3, "-lang smt", USING_VERIT, V1_2);
	public static final IConfigDescriptor CVC3_PP_SMT2 = makeConfig(LAST_CVC3_BIN,
			LAST_CVC3, "-lang smt2", USING_PP, V2_0);
	public static final IConfigDescriptor CVC3_VERIT_SMT2 = makeConfig(
			LAST_CVC3_BIN, LAST_CVC3, "-lang smt2", USING_VERIT, V2_0);

	/**
	 * External CVC4 configurations
	 */
	public static final IConfigDescriptor CVC4_PP_SMT1 = makeConfig(LAST_CVC4_BIN,
			LAST_CVC4, "--lang smt", USING_PP, V1_2);
	public static final IConfigDescriptor CVC4_VERIT_SMT1 = makeConfig(
			LAST_CVC4_BIN, LAST_CVC4, "--lang smt", USING_VERIT, V1_2);
	public static final IConfigDescriptor CVC4_PP_SMT2 = makeConfig(LAST_CVC4_BIN,
			LAST_CVC4, "--lang smt2", USING_PP, V2_0);
	public static final IConfigDescriptor CVC4_VERIT_SMT2 = makeConfig(
			LAST_CVC4_BIN, LAST_CVC4, "--lang smt2", USING_VERIT, V2_0);

	/**
	 * External MathSAT5 configurations
	 */
	public static final IConfigDescriptor MATHSAT5_PP_SMT1 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "-input=smt", USING_PP, V1_2);
	public static final IConfigDescriptor MATHSAT5_VERIT_SMT1 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "-input=smt", USING_VERIT, V1_2);
	public static final IConfigDescriptor MATHSAT5_PP_SMT2 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "", USING_PP, V2_0);
	public static final IConfigDescriptor MATHSAT5_VERIT_SMT2 = makeConfig(
			LAST_MATHSAT5_BIN, LAST_MATHSAT5, "", USING_VERIT, V2_0);

	/**
	 * External OpenSMT configurations
	 */
	public static final IConfigDescriptor OPENSMT_PP_SMT1 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", USING_PP, V1_2);
	public static final IConfigDescriptor OPENSMT_VERIT_SMT1 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", USING_VERIT, V1_2);
	public static final IConfigDescriptor OPENSMT_PP_SMT2 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", USING_PP, V2_0);
	public static final IConfigDescriptor OPENSMT_VERIT_SMT2 = makeConfig(
			LAST_OPENSMT_BIN, LAST_OPENSMT, "", USING_VERIT, V2_0);

	/**
	 * External veriT configurations
	 */
	public static final IConfigDescriptor VERIT_PP_SMT1 = makeConfig(
			LAST_VERIT_BIN, LAST_VERIT, "--enable-e --max-time=2.9", USING_PP,
			V1_2);
	public static final IConfigDescriptor VERIT_VERIT_SMT1 = makeConfig(
			LAST_VERIT_BIN, LAST_VERIT, "--enable-e --max-time=2.9",
			USING_VERIT, V1_2);
	public static final IConfigDescriptor VERIT_PP_SMT2 = makeConfig(
			LAST_VERIT_BIN, LAST_VERIT,
			"-i smtlib2 --disable-print-success --enable-e --max-time=2.9",
			USING_PP, V2_0);
	public static final IConfigDescriptor VERIT_VERIT_SMT2 = makeConfig(
			LAST_VERIT_BIN, LAST_VERIT,
			"-i smtlib2 --disable-print-success --enable-e --max-time=2.9",
			USING_VERIT, V2_0);

	/**
	 * External Z3 configurations
	 */
	public static final IConfigDescriptor Z3_PP_SMT1 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "", USING_PP, V1_2);
	public static final IConfigDescriptor Z3_VERIT_SMT1 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "", USING_VERIT, V1_2);
	public static final IConfigDescriptor Z3_PP_SMT2 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "-smt2", USING_PP, V2_0);
	public static final IConfigDescriptor Z3_VERIT_SMT2 = makeConfig(LAST_Z3_BIN,
			LAST_Z3, "-smt2", USING_VERIT, V2_0);

	public CommonPerformanceTests(SolverKind solverKind, boolean bundled,
			Set<Theory> theories, TranslationApproach translationApproach,
			SMTLIBVersion smtlibVersion, boolean getUnsatCore) {
		this.theories = theories;
		setSolverPreferences(solverKind, bundled, translationApproach,
				smtlibVersion);
	}

	private static IPath makeSolverPath(final String binaryName) {
		final IPath home = Path.fromOSString(System.getProperty("user.home"));
		final IPath solverPath = home.append("bin").append(binaryName);
		if (Platform.getOS() == Platform.OS_WIN32) {
			solverPath.addFileExtension("exe");
		}
		return solverPath;
	}

	/**
	 * Sets plugin preferences with the given solver preferences
	 * 
	 */
	protected void setSolverPreferences(final SolverKind kind,
			final boolean bundled,
			final TranslationApproach translationApproach,
			final SMTLIBVersion smtlibVersion) {
		if (translationApproach == USING_VERIT) {
			if (smtlibVersion == V1_2) {
				switch (kind) {
				case ALT_ERGO:
					configuration = new SMTConfiguration(ALTERGO_VERIT_SMT1, LAST_ALTERGO);
					break;

				case CVC3:
					configuration = new SMTConfiguration(CVC3_VERIT_SMT1, LAST_CVC3);
					break;

				case CVC4:
					configuration = new SMTConfiguration(CVC4_VERIT_SMT1, LAST_CVC4);
					break;

				case MATHSAT5:
					configuration = new SMTConfiguration(MATHSAT5_VERIT_SMT1, LAST_MATHSAT5);
					break;

				case OPENSMT:
					configuration = new SMTConfiguration(OPENSMT_VERIT_SMT1, LAST_OPENSMT);
					break;

				case VERIT:
					configuration = new SMTConfiguration(VERIT_VERIT_SMT1, LAST_VERIT);
					break;

				case Z3:
					configuration = new SMTConfiguration(Z3_VERIT_SMT1, LAST_Z3);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			} else {
				switch (kind) {
				case ALT_ERGO:
					configuration = new SMTConfiguration(ALTERGO_VERIT_SMT2, LAST_ALTERGO);
					break;

				case CVC3:
					configuration = new SMTConfiguration(CVC3_VERIT_SMT2, LAST_CVC3);
					break;

				case CVC4:
					configuration = new SMTConfiguration(CVC4_VERIT_SMT2, LAST_CVC4);
					break;

				case MATHSAT5:
					configuration = new SMTConfiguration(MATHSAT5_VERIT_SMT2, LAST_MATHSAT5);
					break;

				case OPENSMT:
					configuration = new SMTConfiguration(OPENSMT_VERIT_SMT2, LAST_OPENSMT);
					break;

				case VERIT:
					configuration = new SMTConfiguration(VERIT_VERIT_SMT2, LAST_VERIT);
					break;

				case Z3:
					configuration = new SMTConfiguration(Z3_VERIT_SMT2, LAST_Z3);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			}
		} else {
			if (smtlibVersion == V1_2) {
				switch (kind) {
				case ALT_ERGO:
					configuration = new SMTConfiguration(ALTERGO_PP_SMT1, LAST_ALTERGO);
					break;

				case CVC3:
					configuration = new SMTConfiguration(CVC3_PP_SMT1, LAST_CVC3);
					break;

				case CVC4:
					configuration = new SMTConfiguration(CVC4_PP_SMT1, LAST_CVC4);
					break;

				case MATHSAT5:
					configuration = new SMTConfiguration(MATHSAT5_PP_SMT1, LAST_MATHSAT5);
					break;

				case OPENSMT:
					configuration = new SMTConfiguration(OPENSMT_PP_SMT1, LAST_OPENSMT);
					break;

				case VERIT:
					configuration = new SMTConfiguration(VERIT_PP_SMT1, LAST_VERIT);
					break;

				case Z3:
					configuration = new SMTConfiguration(Z3_PP_SMT1, LAST_Z3);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			} else {
				switch (kind) {
				case ALT_ERGO:
					configuration = new SMTConfiguration(ALTERGO_PP_SMT2, LAST_ALTERGO);
					break;

				case CVC3:
					configuration = new SMTConfiguration(CVC3_PP_SMT2, LAST_CVC3);
					break;

				case CVC4:
					configuration = new SMTConfiguration(CVC4_PP_SMT2, LAST_CVC4);
					break;

				case MATHSAT5:
					configuration = new SMTConfiguration(MATHSAT5_PP_SMT2, LAST_MATHSAT5);
					break;

				case OPENSMT:
					configuration = new SMTConfiguration(OPENSMT_PP_SMT2, LAST_OPENSMT);
					break;

				case VERIT:
					configuration = new SMTConfiguration(VERIT_PP_SMT2, LAST_VERIT);
					break;

				case Z3:
					configuration = new SMTConfiguration(Z3_PP_SMT2, LAST_Z3);
					break;

				default:
					throw new IllegalArgumentException("Unexpected solver kind"
							+ kind.name());
				}
			}
		}
		assumeTrue(configuration.getSolverPath().toFile().canExecute());
	}

	private SMTProverCallTestResult smtProverCallTest(final String callMessage,
			final String lemmaName, final ISimpleSequent sequent,
			final boolean expectedSolverResult, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		try {
			switch (configuration.getTranslationApproach()) {
			case USING_VERIT:
				smtProverCall = new SMTVeriTCall(sequent, MONITOR,
						debugBuilder, configuration, lemmaName);
				break;

			default: // USING_PP
				smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder,
						configuration, lemmaName);
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

	protected void doTest(final String lemmaName, final List<String> inputHyps,
			final String inputGoal, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<String> expectedUnsatCoreStr,
			final boolean expectedGoalNeed, final boolean perf) {
		if (perf) {
			doTest(lemmaName, inputHyps, inputGoal, te, expectedSolverResult,
					expectedUnsatCoreStr, expectedGoalNeed, new StringBuilder());
		} else {
			doTest(lemmaName, inputHyps, inputGoal, te, expectedSolverResult,
					expectedUnsatCoreStr, expectedGoalNeed);
		}
	}

	protected void doTest(final String lemmaName, final List<String> inputHyps,
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

		final String testedSolverName = configuration.getSolverName();
		final TranslationApproach testedTranslationApproach = configuration
				.getTranslationApproach();
		final SMTLIBVersion testedSmtlibVersion = configuration
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
				lemmaName, sequent, te, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter1ErrorBuffer = iter1Result.getErrorBuffer().toString();
		if (!iter1ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter1ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
					testedSmtlibVersion, testedTranslationApproach,
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
				lemmaName, sequent, te, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter2ErrorBuffer = iter2Result.getErrorBuffer().toString();
		if (!iter2ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter2ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
					testedSmtlibVersion, testedTranslationApproach,
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
				lemmaName, sequent, te, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter3ErrorBuffer = iter3Result.getErrorBuffer().toString();
		if (!iter3ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter3ErrorBuffer).append("\n");
			/**
			 * Here we print performances of the iter 2 smt prover call because
			 * we just want the unsat core to be refused
			 */
			printPerf(debugBuilder, lemmaName, testedSolverName,
					testedSmtlibVersion, testedTranslationApproach,
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

		final String solverName = configuration.getSolverName();
		if (!solverName.equals(LAST_Z3.getName())) {
			setSolverPreferences(Z3, !BUNDLED, testedTranslationApproach, V2_0);
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult z3UCCheckResult = smtProverCallTest(
					"z3 unsat-core checking", lemmaName, sequent,
					expectedSolverResult, debugBuilder);
			final String z3UCCheckErrorBuffer = z3UCCheckResult
					.getErrorBuffer().toString();
			if (!z3UCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(z3UCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverName,
						testedSmtlibVersion, testedTranslationApproach,
						iter3Result.getSmtProverCall());
				fail(z3UCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_CVC3.getName())) {
			setSolverPreferences(CVC3, testedTranslationApproach, V2_0);
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult cvc3UCCheckResult = smtProverCallTest(
					"cvc3 unsat-core checking", lemmaName, sequent,
					expectedSolverResult, debugBuilder);
			final String cvc3UCCheckErrorBuffer = cvc3UCCheckResult
					.getErrorBuffer().toString();
			if (!cvc3UCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(cvc3UCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverName,
						testedSmtlibVersion, testedTranslationApproach,
						iter3Result.getSmtProverCall());
				fail(cvc3UCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_ALTERGO.getName())) {
			setSolverPreferences(ALT_ERGO, testedTranslationApproach, V2_0);
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult altergoUCCheckResult = smtProverCallTest(
					"alt-ergo unsat-core checking", lemmaName, sequent,
					expectedSolverResult, debugBuilder);
			final String altergoUCCheckErrorBuffer = altergoUCCheckResult
					.getErrorBuffer().toString();
			if (!altergoUCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(altergoUCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverName,
						testedSmtlibVersion, testedTranslationApproach,
						iter3Result.getSmtProverCall());
				fail(altergoUCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_VERIT.getName())) {
			setSolverPreferences(VERIT, testedTranslationApproach, V2_0);
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, ff);
			final SMTProverCallTestResult veritUCCheckResult = smtProverCallTest(
					"veriT unsat-core checking", lemmaName, sequent,
					expectedSolverResult, debugBuilder);
			final String veritUCCheckErrorBuffer = veritUCCheckResult
					.getErrorBuffer().toString();
			if (!veritUCCheckErrorBuffer.isEmpty()) {
				debugBuilder.append(veritUCCheckErrorBuffer).append("\n");
				/**
				 * Here we print performances of the iter 3 smt prover call
				 * because we just want the unsat core checking to be refused
				 */
				printPerf(debugBuilder, lemmaName, testedSolverName,
						testedSmtlibVersion, testedTranslationApproach,
						iter3Result.getSmtProverCall());
				fail(veritUCCheckErrorBuffer);
			}
		}
		debugBuilder.append("\n");
		unsatCoreChecked = true;

		printPerf(debugBuilder, lemmaName, testedSolverName,
				testedSmtlibVersion, testedTranslationApproach,
				iter3Result.getSmtProverCall());
	}
}
