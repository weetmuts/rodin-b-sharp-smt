/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance;

import static org.eventb.smt.tests.ConfigProvider.LAST_ALTERGO;
import static org.eventb.smt.tests.ConfigProvider.LAST_CVC3;
import static org.eventb.smt.tests.ConfigProvider.LAST_VERIT;
import static org.eventb.smt.tests.ConfigProvider.LAST_Z3;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.core.internal.provers.SMTPPCall;
import org.eventb.smt.core.internal.provers.SMTProverCall;
import org.eventb.smt.tests.CommonSolverRunTests;
import org.eventb.smt.tests.ConfigProvider;
import org.eventb.smt.utils.Theory;

/**
 * @author Yoann Guyot
 * 
 */
public abstract class CommonPerformanceTests extends CommonSolverRunTests {

	public CommonPerformanceTests(ConfigProvider provider, Set<Theory> theories, boolean getUnsatCore) {
		super(provider, theories, getUnsatCore);
		assumeTrue(configuration.getSolverPath().toFile().canExecute());
	}

	private SMTProverCallTestResult smtProverCallTest(final String callMessage,
			final String lemmaName, final ISimpleSequent sequent,
			final boolean expectedSolverResult, final StringBuilder debugBuilder) {
		SMTProverCall smtProverCall = null;
		final StringBuilder errorBuilder = new StringBuilder("");

		try {
			smtProverCall = new SMTPPCall(sequent, MONITOR, debugBuilder, configuration, lemmaName);

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
		final ITypeEnvironmentBuilder teb = te.makeBuilder();
		final FormulaFactory fac = te.getFormulaFactory();
		final List<Predicate> parsedHypotheses = new ArrayList<Predicate>();
		for (final String hyp : inputHyps) {
			parsedHypotheses.add(parse(hyp, teb));
		}
		final Predicate parsedGoal = parse(inputGoal, teb);

		final List<Predicate> expectedHypotheses = new ArrayList<Predicate>();
		for (final String expectedHyp : expectedUnsatCoreStr) {
			expectedHypotheses.add(parse(expectedHyp, teb));
		}

		// Type check goal and hypotheses
		assertTypeChecked(parsedGoal);
		for (final Predicate parsedHypothesis : parsedHypotheses) {
			assertTypeChecked(parsedHypothesis);
		}

		final String testedSolverName = configuration.getSolverName();

		/**
		 * Iter 1 : calls the prover with the expected unsat-core, to check if
		 * it is right
		 */
		debugBuilder.append("Iter 1\n");
		final Predicate goalXML = (expectedGoalNeed ? parsedGoal : parse("⊥",
				teb));

		ISimpleSequent sequent = SimpleSequents.make(expectedHypotheses,
				goalXML, fac);
		final SMTProverCallTestResult iter1Result = smtProverCallTest("Iter 1",
				lemmaName, sequent, teb, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter1ErrorBuffer = iter1Result.getErrorBuffer().toString();
		if (!iter1ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter1ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
					iter1Result.getSmtProverCall());
			fail(iter1ErrorBuffer);
		}
		debugBuilder.append("\n");

		/**
		 * Iter 2 : calls the prover and check if the unsat-core is the expected
		 * one
		 */
		debugBuilder.append("Iter 2\n");
		sequent = SimpleSequents.make(parsedHypotheses, parsedGoal, fac);
		final SMTProverCallTestResult iter2Result = smtProverCallTest("Iter 2",
				lemmaName, sequent, teb, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter2ErrorBuffer = iter2Result.getErrorBuffer().toString();
		if (!iter2ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter2ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
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
				.isGoalNeeded() ? parsedGoal : parse("⊥", teb));
		sequent = SimpleSequents.make(neededHypotheses, goalSolver, fac);
		final SMTProverCallTestResult iter3Result = smtProverCallTest("Iter 3",
				lemmaName, sequent, teb, expectedSolverResult,
				expectedHypotheses, expectedGoalNeed, debugBuilder);
		final String iter3ErrorBuffer = iter3Result.getErrorBuffer().toString();
		if (!iter3ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter3ErrorBuffer).append("\n");
			/**
			 * Here we print performances of the iter 2 smt prover call because
			 * we just want the unsat core to be refused
			 */
			printPerf(debugBuilder, lemmaName, testedSolverName,
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
		if (!solverName.equals(LAST_Z3.solverName())) {
			configuration = LAST_Z3.config();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, fac);
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
						iter3Result.getSmtProverCall());
				fail(z3UCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_CVC3.solverName())) {
			configuration = LAST_CVC3.config();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, fac);
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
						iter3Result.getSmtProverCall());
				fail(cvc3UCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_ALTERGO.solverName())) {
			configuration = LAST_ALTERGO.config();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, fac);
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
						iter3Result.getSmtProverCall());
				fail(altergoUCCheckErrorBuffer);
			}
		}
		if (!solverName.equals(LAST_VERIT.solverName())) {
			configuration = LAST_VERIT.config();
			sequent = SimpleSequents.make(neededHypotheses, goalSolver, fac);
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
						iter3Result.getSmtProverCall());
				fail(veritUCCheckErrorBuffer);
			}
		}
		debugBuilder.append("\n");
		unsatCoreChecked = true;

		printPerf(debugBuilder, lemmaName, testedSolverName,
				iter3Result.getSmtProverCall());
	}
}
