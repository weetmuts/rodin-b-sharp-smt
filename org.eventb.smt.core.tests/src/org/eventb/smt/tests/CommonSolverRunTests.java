/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests;

import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.eventb.smt.utils.Theory.getComboLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.SMTSignature;
import org.eventb.smt.core.internal.provers.SMTConfiguration;
import org.eventb.smt.core.internal.provers.SMTPPCall;
import org.eventb.smt.core.internal.provers.SMTProverCall;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.utils.Theory;
import org.junit.After;

public abstract class CommonSolverRunTests extends AbstractTests {

	protected static final NullProofMonitor MONITOR = new NullProofMonitor();

	protected final List<SMTProverCall> smtProverCalls = new ArrayList<SMTProverCall>();

	static File smtFolder;

	protected static final boolean GET_UNSAT_CORE = true;
	protected static final boolean PERFORMANCE = true;

	/**
	 * H /\ ¬ G is UNSAT, so H |- G is VALID
	 */
	protected static final boolean VALID = true;
	protected static final boolean TRIVIAL = true;

	protected Set<org.eventb.smt.utils.Theory> theories;
	protected String poName;

	protected SMTConfiguration configuration;

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

	public CommonSolverRunTests(ConfigProvider provider, Set<Theory> theories, boolean getUnsatCore) {
		this.theories = theories;
		this.configuration = provider.config();
	}

	public static IConfigDescriptor makeConfig(final ISolverDescriptor solver,
			final String args) {
		return makeConfig(solver.getName(), solver, args);
	}

	public static IConfigDescriptor makeConfig(final String id,
			final ISolverDescriptor solver, final String args) {
		final String newID = id + "_test";
		return newConfigDescriptor(newID, solver.getName(), args, true);
	}

	protected void printPerf(final StringBuilder debugBuilder,
			final String lemmaName, final String solverId,
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
				parsedGoal, parsedGoal.getFactory());

		final SMTProverCall smtProverCall = callSolver(lemmaName, sequent,
				debugBuilder);

		printPerf(debugBuilder, lemmaName, configuration.getSolverName(),
				smtProverCall);

		assertEquals(expectedTrivial, smtProverCall.benchmarkIsNull());
		assertEquals("The result of the SMT prover wasn't the expected one.",
				expectedSolverResult, smtProverCall.isValid());
	}

	/*
	 * Calls the SMT solver, ensuring that it will be cleaned up eventually.
	 */
	private SMTProverCall callSolver(String lemmaName, ISimpleSequent sequent,
			StringBuilder debugBuilder) {
		final SMTProverCall result = getCall(lemmaName, sequent, debugBuilder);
		smtProverCalls.add(result);
		result.run();
		return result;
	}

	private SMTProverCall getCall(String lemmaName, ISimpleSequent sequent,
			StringBuilder debugBuilder) {
		return new SMTPPCall(sequent, MONITOR, debugBuilder, configuration, lemmaName);
	}

	private void appendError(StringBuilder sb, String callMessage, String lemmaName, String message) {
		sb.append(callMessage);
		sb.append(" (");
		sb.append(lemmaName);
		sb.append(") ");
		sb.append(configuration.getName());
		sb.append(": ");
		sb.append(message);
	}

	protected SMTProverCallTestResult smtProverCallTest(
			final String callMessage, final String lemmaName,
			final ISimpleSequent sequent, final ITypeEnvironment te,
			final boolean expectedSolverResult,
			final List<Predicate> expectedUnsatCore,
			final boolean expectedGoalNeed, final StringBuilder debugBuilder) {
		final StringBuilder errorBuilder = new StringBuilder("");
		final SMTProverCall smtProverCall = callSolver(lemmaName, sequent,
				debugBuilder);

		if (smtProverCall.isValid() != expectedSolverResult) {
			appendError(errorBuilder, callMessage, lemmaName,
					"SMT prover result: expected " + expectedSolverResult + " but was " + smtProverCall.isValid());
			return new SMTProverCallTestResult(smtProverCall, errorBuilder);
		}

		if (expectedUnsatCore != null) {
			final Set<Predicate> actualNeededHyps = smtProverCall.neededHypotheses();
			if (actualNeededHyps == null) {
				appendError(errorBuilder, callMessage, lemmaName,
						"The SMT prover did not extract needed hypotheses.");
				return new SMTProverCallTestResult(smtProverCall, errorBuilder);
			} else {
				final Set<Predicate> expectedNeededHyps = new HashSet<Predicate>(expectedUnsatCore);
				if (!expectedNeededHyps.equals(actualNeededHyps)) {
					final Set<Predicate> missingHyps = new HashSet<Predicate>(expectedNeededHyps);
					missingHyps.removeAll(actualNeededHyps);
					final Set<Predicate> unwantedHyps = new HashSet<Predicate>(actualNeededHyps);
					unwantedHyps.removeAll(expectedNeededHyps);
					appendError(errorBuilder, callMessage, lemmaName, "Wrong UNSAT Core:");
					if (!missingHyps.isEmpty())
						errorBuilder.append("\nmissing: " + missingHyps);
					if (!unwantedHyps.isEmpty())
						errorBuilder.append("\nunwanted: " + unwantedHyps);
					errorBuilder.append("\n");
				}
			}
		}
		if (smtProverCall.isGoalNeeded() != expectedGoalNeed) {
			appendError(errorBuilder, callMessage, lemmaName,
					"SMT goal needed: expected " + expectedGoalNeed + " but was " + smtProverCall.isGoalNeeded());
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
				parsedGoal, parsedGoal.getFactory());

		// FIXME should not be PP because could this is used by veriT tests
		final SMTThroughPP translator = new SMTThroughPP();
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
		final ITypeEnvironmentBuilder teb = te.makeBuilder();
		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, teb));
		}

		final Predicate goal = parse(inputGoal, teb);

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
		final ITypeEnvironmentBuilder teb = te.makeBuilder();
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

		ISimpleSequent sequent = SimpleSequents.make(parsedHypotheses,
				parsedGoal, parsedGoal.getFactory());
		final SMTProverCallTestResult iter2Result = smtProverCallTest(
				"Unsat core extraction", lemmaName, sequent, te,
				expectedSolverResult, expectedHypotheses, expectedGoalNeed,
				debugBuilder);
		final String iter2ErrorBuffer = iter2Result.getErrorBuffer().toString();
		if (!iter2ErrorBuffer.isEmpty()) {
			debugBuilder.append(iter2ErrorBuffer).append("\n");
			printPerf(debugBuilder, lemmaName, testedSolverName,
					iter2Result.getSmtProverCall());
			fail(iter2ErrorBuffer);
		}
	}

	protected void doTTeTest(final String lemmaName,
			final List<String> inputHyps, final String inputGoal,
			final ITypeEnvironment te, final Set<String> expectedFuns,
			final Set<String> expectedPreds, final Set<String> expectedSorts) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();
		final ITypeEnvironmentBuilder teb = te.makeBuilder();
		for (final String hyp : inputHyps) {
			hypotheses.add(parse(hyp, teb));
		}

		final Predicate goal = parse(inputGoal, teb);

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
