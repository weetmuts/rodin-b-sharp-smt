/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *******************************************************************************/
package fr.systerel.smt.provers.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.osgi.framework.BundleContext;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.translation.Translator;
import fr.systerel.smt.provers.internal.core.ExternalSMTThroughPP;
import fr.systerel.smt.provers.internal.core.ExternalSMTThroughVeriT;
import fr.systerel.smt.provers.internal.core.SMTInput;

/**
 * This is the main class of the SMT solvers plugin.
 */
public class SMTProversCore extends Plugin {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "fr.systerel.smt.provers.core";
	/**
	 * Debug variables
	 */
	private static final String DEBUG = PLUGIN_ID + "/debug/";
	private static final String DEBUG_TRANSLATOR = DEBUG + "translator";
	private static final String RODIN_SEQUENT = "rodin_sequent";
	/**
	 * Default delay for time-out of the Smt provers (value 30 seconds).
	 */
	public static long DEFAULT_DELAY = 3 * 1000;
	public static long NO_DELAY = 0;
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
	 * @param smtPreferences
	 *            preferences set for this calling
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughPP(
			final SMTPreferences smtPreferences, final boolean restricted) {
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughPP(smtPreferences), //
				new SMTInput(restricted, DEFAULT_DELAY, RODIN_SEQUENT));
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
	 * @param smtPreferences
	 *            preferences set for this calling
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughVeriT(
			final SMTPreferences smtPreferences, final boolean restricted) {
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughVeriT(smtPreferences), //
				new SMTInput(restricted, DEFAULT_DELAY, RODIN_SEQUENT));
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
