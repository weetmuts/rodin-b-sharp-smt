/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - minor modifications
 *******************************************************************************/

package org.eventb.smt.provers.core;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.smt.preferences.SMTPreferences;
import org.eventb.smt.preferences.SMTSolverConfiguration;
import org.eventb.smt.provers.internal.core.ExternalSMTThroughPP;
import org.eventb.smt.provers.internal.core.ExternalSMTThroughVeriT;
import org.eventb.smt.provers.internal.core.SMTInput;
import org.eventb.smt.translation.Translator;
import org.osgi.framework.BundleContext;

/**
 * This is the main class of the SMT solvers plugin.
 */
public class SMTProversCore extends Plugin {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";
	public static final String PREFERENCES_PLUGIN_ID = "org.eventb.smt.ui";
	/**
	 * Debug variables
	 */
	private static final String DEBUG = PLUGIN_ID + "/debug/";
	private static final String DEBUG_TRANSLATOR = DEBUG + "translator";
	private static final String DEBUG_TRANSLATOR_DETAILS = DEBUG_TRANSLATOR
			+ "_details";
	/**
	 * Default delay for time-out of the SMT provers (value 3 seconds).
	 */
	public static long DEFAULT_DELAY = 3 * 1000;
	public static long NO_DELAY = 0;
	/**
	 * Configuration ID value used when all configurations should be applied
	 * sequentially.
	 */
	public static String ALL_SOLVER_CONFIGURATIONS = "all";
	public static String NO_SOLVER_CONFIGURATION_ERROR = "No SMT solver configuration set";
	/**
	 * The shared instance.
	 */
	private static SMTProversCore plugin;

	/**
	 * Returns the single instance of the Smt Provers for Rodin core plug-in.
	 * 
	 * @return the single instance of the Smt Provers for Rodin core plug-in
	 */
	public static SMTProversCore getDefault() {
		return plugin;
	}

	private static boolean parseOption(final String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		Translator.DEBUG = parseOption(DEBUG_TRANSLATOR);
		Translator.DEBUG_DETAILS = parseOption(DEBUG_TRANSLATOR_DETAILS);
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	private static class SMTFailureTactic implements ITactic {
		private final String message;

		private final static SMTFailureTactic SMT_SOLVER_CONFIG_ERROR = new SMTFailureTactic(
				Messages.SMTProversCore_SMTSolverConfigError);
		private final static SMTFailureTactic NO_SMT_SOLVER_SELECTED = new SMTFailureTactic(
				Messages.SMTProversCore_NoSMTSolverSelected);
		private final static SMTFailureTactic NO_SMT_SOLVER_SET = new SMTFailureTactic(
				Messages.SMTProversCore_NoSMTSolverSet);
		private final static SMTFailureTactic VERIT_PATH_NOT_SET = new SMTFailureTactic(
				Messages.SMTProversCore_VeriTPathNotSet);
		private final static SMTFailureTactic PROOF_TREE_ORIGIN_ERROR = new SMTFailureTactic(
				Messages.SMTProversCore_ProofTreeOriginError);

		private SMTFailureTactic(final String message) {
			this.message = message;
		}

		static final SMTFailureTactic getSMTSolverConfigError() {
			return SMT_SOLVER_CONFIG_ERROR;
		}

		static final SMTFailureTactic getNoSMTSolverSelected() {
			return NO_SMT_SOLVER_SELECTED;
		}

		static final SMTFailureTactic getNoSMTSolverSet() {
			return NO_SMT_SOLVER_SET;
		}

		static final SMTFailureTactic getVeriTPathNotSet() {
			return VERIT_PATH_NOT_SET;
		}

		static final SMTFailureTactic getProofTreeOriginError() {
			return PROOF_TREE_ORIGIN_ERROR;
		}

		@Override
		public Object apply(final IProofTreeNode ptNode, final IProofMonitor pm) {
			return message;
		}
	}

	public static SMTFailureTactic smtSolverError() {
		return SMTFailureTactic.getSMTSolverConfigError();
	}

	public static SMTFailureTactic noSMTSolverSelected() {
		return SMTFailureTactic.getNoSMTSolverSelected();
	}

	public static SMTFailureTactic noSMTSolverSet() {
		return SMTFailureTactic.getNoSMTSolverSet();
	}

	public static final SMTFailureTactic veriTPathNotSet() {
		return SMTFailureTactic.getVeriTPathNotSet();
	}

	public static final SMTFailureTactic proofTreeOriginError() {
		return SMTFailureTactic.getProofTreeOriginError();
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted,
			long timeout, final String configId) {
		if (configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = SMTPreferences
					.getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = BasicTactics.reasonerTac(
							new ExternalSMTThroughPP(), new SMTInput(
									restricted, timeout, solverConfigs.get(i)));
				}
				return BasicTactics.composeUntilSuccess(smtTactics);
			} else {
				return BasicTactics.failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		}
		return BasicTactics.reasonerTac( //
				new ExternalSMTThroughPP(), //
				new SMTInput(restricted, timeout, configId));
	}

	/**
	 * This tactic should be called when it is not parameterised and the user
	 * just want the current solver configuration to be used
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted, long timeout) {
		return BasicTactics.reasonerTac( //
				new ExternalSMTThroughPP(), //
				new SMTInput(restricted, timeout));
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using ppTrans. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughPP(final boolean restricted) {
		return externalSMTThroughPP(restricted, DEFAULT_DELAY);
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			long timeout, final String configId) {
		if (configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = SMTPreferences
					.getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = BasicTactics.reasonerTac(
							new ExternalSMTThroughVeriT(), new SMTInput(
									restricted, timeout, solverConfigs.get(i)));
				}
				return BasicTactics.composeUntilSuccess(smtTactics);
			} else {
				return BasicTactics.failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		}
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughVeriT(), //
				new SMTInput(restricted, timeout, configId));
	}

	/**
	 * This tactic should be called when it is not parameterised and the user
	 * just want the current solver configuration to be used
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			long timeout) {
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughVeriT(), //
				new SMTInput(restricted, timeout));
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using veriT. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughVeriT(final boolean restricted) {
		return externalSMTThroughVeriT(restricted, DEFAULT_DELAY);
	}

	/**
	 * Starts up this plug-in.
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging()) {
			configureDebugOptions();
		}
	}

	/**
	 * Stops this plug-in.
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
