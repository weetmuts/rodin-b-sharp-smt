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
package org.eventb.smt.core.internal.provers;

import static org.eventb.core.seqprover.tactics.BasicTactics.failTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.smt.core.SMTCore.PLUGIN_ID;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;
import static org.eventb.smt.core.internal.preferences.configurations.SolverConfigRegistry.getSolverConfigRegistry;
import static org.eventb.smt.core.internal.preferences.solvers.BundledSolverRegistry.getBundledSolverRegistry;
import static org.eventb.smt.core.internal.provers.AutoTactics.makeAllSMTSolversTactic;
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_REPLACE;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.provers.SolverKind.VERIT;

import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.preferences.ExtensionLoadingException;
import org.eventb.smt.core.internal.preferences.configurations.SolverConfigRegistry;
import org.eventb.smt.core.internal.preferences.solvers.BundledSolverRegistry;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.core.internal.translation.Translator;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;
import org.eventb.smt.core.preferences.ITranslationPreferences;
import org.osgi.framework.BundleContext;

/**
 * This is the main class of the SMT solvers plugin.
 */
public class SMTProversCore extends Plugin {

	/**
	 * The shared instance.
	 */
	private static SMTProversCore plugin;

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
	public static String NO_SOLVER_CONFIGURATION_ERROR = "No SMT configuration set";
	public static String DISABLED_SOLVER_CONFIGURATION_ERROR = "The indicated SMT configuration is disabled";
	public static String NO_SUCH_SOLVER_CONFIGURATION_ERROR = "No such SMT configuration";

	public static final long DEFAULT_TIMEOUT_DELAY = 1000;
	public static final boolean DEFAULT_RESTRICTED_VALUE = true;

	private ITacticDescriptor allSMTSolversTacticDesc;

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

	private void logError(final String message, final Throwable exception) {
		plugin.getLog().log(smtError(message, exception));
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay, String configId) {
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();

		if (configId == null
				|| !configsPrefs.getSolverConfigs().containsKey(configId)) {
			return failTac(NO_SUCH_SOLVER_CONFIGURATION_ERROR);
		}

		final ISolverConfig config = configsPrefs.getSolverConfig(configId);
		if (config.isEnabled()) {
			final IReasoner smtReasoner = new ExternalSMT();
			final IReasonerInput smtInput = new SMTInput(restricted,
					timeOutDelay, config);
			return reasonerTac(smtReasoner, smtInput);
		} else {
			return failTac(DISABLED_SOLVER_CONFIGURATION_ERROR);
		}
	}

	public ITacticDescriptor getAllSMTSolversTactic() {
		if (allSMTSolversTacticDesc == null) {
			updateAllSMTSolversTactic();
		}

		return allSMTSolversTacticDesc;
	}

	public void updateAllSMTSolversTactic() {
		allSMTSolversTacticDesc = makeAllSMTSolversTactic();
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

		final ISMTSolversPreferences solversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		final ISMTSolversPreferences defaultSolversPrefs = getPreferenceManager()
				.getDefaultSMTSolversPrefs();
		final ITranslationPreferences translationPrefs = getPreferenceManager()
				.getTranslationPrefs();
		final ITranslationPreferences defaultTranslationPrefs = getPreferenceManager()
				.getDefaultTranslationPrefs();
		try {
			final BundledSolverRegistry registry = getBundledSolverRegistry();
			for (final String solverId : registry.getIDs()) {
				final ISMTSolver solver = registry.get(solverId);
				try {
					defaultSolversPrefs.add(solver, FORCE_REPLACE);
					defaultSolversPrefs.save();
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding a bundled solver to the default preferences.",
							iae);
				}

				try {
					solversPrefs.add(solver, FORCE_REPLACE);
					solversPrefs.save();
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding a bundled solver to the preferences.",
							iae);
				}

				// FIXME what if several veriT extensions are added
				if (solver != null && solver.getKind().equals(VERIT)) {
					defaultTranslationPrefs.setVeriTPath(solver);
					defaultTranslationPrefs.save();

					translationPrefs.setVeriTPath(solver);
					translationPrefs.save();
				}
			}
		} catch (ExtensionLoadingException ele) {
			logError(
					"An error occured while loading the bundled solver registry.",
					ele);
		} catch (InvalidRegistryObjectException iroe) {
			logError(
					"An error occured while loading the bundled solver registry.",
					iroe);
		}

		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		final ISolverConfigsPreferences defaultConfigsPrefs = getPreferenceManager()
				.getDefaultSolverConfigsPrefs();
		try {
			final SolverConfigRegistry registry = getSolverConfigRegistry();
			for (final String configId : registry.getIDs()) {
				final ISolverConfig solverConfig = registry.get(configId);
				try {
					defaultConfigsPrefs.add(solverConfig, FORCE_REPLACE);
					defaultConfigsPrefs.save();
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding an SMT-solver configuration to the default preferences.",
							iae);
				}
				try {
					configsPrefs.add(solverConfig, FORCE_REPLACE);
					configsPrefs.save();
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding an SMT-solver configuration to the preferences.",
							iae);
				}
			}
		} catch (ExtensionLoadingException ele) {
			logError(
					"An error occured while loading the bundled solver registry.",
					ele);
		} catch (InvalidRegistryObjectException iroe) {
			logError(
					"An error occured while loading the bundled solver registry.",
					iroe);
		}

		updateAllSMTSolversTactic();
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
