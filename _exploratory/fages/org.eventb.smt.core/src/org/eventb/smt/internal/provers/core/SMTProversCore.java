/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - minor modifications
 *******************************************************************************/

package org.eventb.smt.internal.provers.core;

import static org.eventb.smt.internal.preferences.BundledSolverRegistry.getBundledSolverRegistry;
import static org.eventb.smt.internal.preferences.SMTPreferences.getDefaultSMTPrefs;
import static org.eventb.smt.internal.preferences.SMTPreferences.getSMTPrefs;
import static org.eventb.smt.internal.provers.core.SMTSolver.VERIT;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.internal.preferences.BundledSolverRegistry;
import org.eventb.smt.internal.preferences.SMTPreferences;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.translation.SMTThroughPP;
import org.eventb.smt.internal.translation.Translator;
import org.osgi.framework.BundleContext;

/**
 * This is the main class of the SMT solvers plugin.
 */
public class SMTProversCore extends Plugin {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";
	/**
	 * Debug variables
	 */
	private static final String DEBUG = PLUGIN_ID + "/debug/";
	private static final String DEBUG_TRANSLATOR = DEBUG + "translator";
	private static final String DEBUG_TRANSLATOR_DETAILS = DEBUG_TRANSLATOR
			+ "_details";
	private static final String DEBUG_PP_GATHER_SPECIAL_MS_PREDS = DEBUG
			+ "pp_gather_special_ms_preds";
	private static final String DEBUG_PP_MS_OPTIMIZATION_ON = DEBUG
			+ "pp_ms_optimization_on";
	private static final String DEBUG_PP_SET_THEORY_AXIOMS_ON = DEBUG
			+ "pp_set_theory_axioms_on";

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
		SMTThroughPP.GATHER_SPECIAL_MS_PREDS = parseOption(DEBUG_PP_GATHER_SPECIAL_MS_PREDS);
		SMTThroughPP.MS_OPTIMIZATION_ON = SMTThroughPP.GATHER_SPECIAL_MS_PREDS
				&& parseOption(DEBUG_PP_MS_OPTIMIZATION_ON);
		SMTThroughPP.SET_THEORY_AXIOMS_ON = parseOption(DEBUG_PP_SET_THEORY_AXIOMS_ON);
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

		final SMTPreferences smtPrefs = getSMTPrefs();
		final SMTPreferences smtDefaultPrefs = getDefaultSMTPrefs();
		final BundledSolverRegistry registry = getBundledSolverRegistry();

		smtPrefs.removeIncorrectInternalConfigs();
		for (final SMTSolverConfiguration solverConfig : registry
				.getSolverConfigs()) {
			try {
				smtDefaultPrefs.addSolverConfigToDefault(solverConfig);
				smtPrefs.addSolverConfig(solverConfig);
			} catch (IllegalArgumentException iae) {
				throw iae;
			} finally {
				smtDefaultPrefs.setSelectedConfigIndex(false, 0);
				smtPrefs.setSelectedConfigIndex(false, 0);
				// FIXME what if several veriT extensions are added
				if (solverConfig.getSolver().equals(VERIT)) {
					final String veriTPath = solverConfig.getPath();
					smtDefaultPrefs.setDefaultVeriTPath(veriTPath);
					smtPrefs.setVeriTPath(veriTPath);
				}
				smtDefaultPrefs.saveDefaultPrefs();
				smtPrefs.savePrefs();
			}
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
