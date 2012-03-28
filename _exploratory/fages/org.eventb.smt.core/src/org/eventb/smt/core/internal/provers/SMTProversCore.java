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

import static org.eventb.smt.core.SMTCore.PLUGIN_ID;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;
import static org.eventb.smt.core.internal.preferences.BundledSolverRegistry.getBundledSolverRegistry;
import static org.eventb.smt.core.internal.preferences.SolverConfigRegistry.getSolverConfigRegistry;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.provers.SolverKind.VERIT;

import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.smt.core.internal.preferences.BundledSolverRegistry;
import org.eventb.smt.core.internal.preferences.ExtensionLoadingException;
import org.eventb.smt.core.internal.preferences.SolverConfigRegistry;
import org.eventb.smt.core.internal.translation.SMTThroughPP;
import org.eventb.smt.core.internal.translation.Translator;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.osgi.framework.BundleContext;

/**
 * This is the main class of the SMT solvers plugin.
 */
public class SMTProversCore extends Plugin {
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

	private void logError(final String message, final Throwable exception) {
		plugin.getLog().log(smtError(message, exception));
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
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

		final IPreferences smtPrefs = getPreferenceManager().getSMTPrefs();
		final IPreferences smtDefaultPrefs = getPreferenceManager()
				.getDefaultSMTPrefs();
		try {
			final BundledSolverRegistry registry = getBundledSolverRegistry();
			for (final String solverId : registry.getIDs()) {
				final ISMTSolver solver = registry.get(solverId);
				try {
					smtDefaultPrefs.addSolver(solver);
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding a bundled solver to the default preferences.",
							iae);
				}
				try {
					smtPrefs.addSolver(solver);
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding a bundled solver to the preferences.",
							iae);
				}

				// FIXME what if several veriT extensions are added
				if (solver != null && solver.getKind().equals(VERIT)) {
					final String veriTPath = solver.getPath().toOSString();
					smtDefaultPrefs.setVeriTPath(veriTPath);
					smtPrefs.setVeriTPath(veriTPath);
				}

				smtDefaultPrefs.save();
				smtPrefs.save();
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

		try {
			final SolverConfigRegistry registry = getSolverConfigRegistry();
			for (final String configId : registry.getIDs()) {
				final ISolverConfig solverConfig = registry.get(configId);
				try {
					smtDefaultPrefs.addSolverConfig(solverConfig);
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding an SMT-solver configuration to the default preferences.",
							iae);
				}
				try {
					smtPrefs.addSolverConfig(solverConfig);
				} catch (IllegalArgumentException iae) {
					logError(
							"An error occured while adding an SMT-solver configuration to the preferences.",
							iae);
				}

				smtDefaultPrefs.save();
				smtPrefs.save();
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
