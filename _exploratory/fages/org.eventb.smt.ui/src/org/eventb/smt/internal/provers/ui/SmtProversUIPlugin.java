/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.provers.ui;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.getDefaultAutoTactics;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.parsePreferencesString;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.contains;
import static org.eventb.smt.internal.preferences.ui.SMTSolverConfigurationDialog.SHOW_ERRORS;
import static org.eventb.smt.internal.preferences.ui.SMTSolverConfigurationDialog.validPath;
import static org.eventb.smt.internal.provers.core.AutoTactics.makeSMTPPTactic;
import static org.eventb.smt.verit.core.VeriTProverCore.getVeriTConfig;
import static org.eventb.smt.verit.core.VeriTProverCore.getVeriTPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class SmtProversUIPlugin extends AbstractUIPlugin {
	/**
	 * plug-in id
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.ui";

	private static final String AUTO_TACTIC_SMTPP_PROFILE_ID = "Default Auto Tactic with SMT Profile";

	/**
	 * the shared instance
	 */
	private static SmtProversUIPlugin plugin;

	/**
	 * The constructor
	 */
	public SmtProversUIPlugin() {
		// Do nothing
	}

	/**
	 * Checks SMT-solver configurations which were added automatically by the
	 * plug-in. Particularly checks if paths are correct. If such a path is not
	 * correct, removes it.
	 * 
	 * FIXME the preferences should be located in the core plug-in
	 */
	private static void removeIncorrectInternalConfigs() {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		final String preferences = preferenceStore
				.getString(SOLVER_PREFERENCES_ID);
		List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(preferences);
		for (Iterator<SMTSolverConfiguration> configsIter = solverConfigs
				.iterator(); configsIter.hasNext();) {
			final SMTSolverConfiguration config = configsIter.next();
			final String path = config.getPath();
			if (path != null) {
				if (config.getPath().contains(
						"configuration/org.eclipse.osgi/bundles")) {
					if (!validPath(path, !SHOW_ERRORS)) {
						configsIter.remove();
					}
				}
			} else {
				configsIter.remove();
			}
		}
		preferenceStore.setValue(SOLVER_PREFERENCES_ID,
				SMTSolverConfiguration.toString(solverConfigs));
	}

	/**
	 * Adds the given SMT-solver configuration to the SMT preferences.
	 * 
	 * FIXME the preferences should be located in the core plug-in
	 * 
	 * @param solverConfig
	 *            the configuration to add
	 */
	private static void addSolverConfig(
			final SMTSolverConfiguration solverConfig) {
		if (validPath(solverConfig.getPath(), !SHOW_ERRORS)) {
			IPreferenceStore preferenceStore = plugin.getPreferenceStore();
			final String preferences = preferenceStore
					.getString(SOLVER_PREFERENCES_ID);
			List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(preferences);
			if (!contains(solverConfigs, solverConfig)) {
				solverConfigs.add(solverConfig);
			}
			preferenceStore.setValue(SOLVER_PREFERENCES_ID,
					SMTSolverConfiguration.toString(solverConfigs));
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver configuration: invalid path.");
		}
	}

	/**
	 * Forces the selected solver index to <code>0</code> when a configuration
	 * for an integrated solver is added and no configuration exists yet.
	 */
	private static void setSelectedSolverIndex() {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		/**
		 * We assume that an invalid solver index (> number of configurations)
		 * can't be, because the only other way to modify the number of
		 * configurations is handled correctly.
		 */
		if (preferenceStore.getInt(SOLVER_INDEX_ID) < 0) {
			preferenceStore.setValue(SOLVER_INDEX_ID, 0);
		}
	}

	/**
	 * Sets veriT path to the path of the integrated veriT solver.
	 */
	private static void setVeriTPath() {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		final String veriTPath = getVeriTPath();
		if (validPath(veriTPath, !SHOW_ERRORS)) {
			preferenceStore.setValue(VERIT_PATH_ID, veriTPath);
		} else if (!validPath(preferenceStore.getString(VERIT_PATH_ID),
				!SHOW_ERRORS)) {
			preferenceStore.setToDefault(VERIT_PATH_ID);
		}
	}

	/**
	 * Adds a new tactics profile in the Rodin platform preferences which is the
	 * default auto tactics profile plus the SMT tactic inserted right after the
	 * BoundedGoalWithFiniteHyps tactic.
	 * 
	 */
	private static void addSMTProfile() {
		/**
		 * Accesses the Event-B UI preferences
		 */
		final IPreferenceStore eventbUIPrefStore = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		/**
		 * Loads the list of tactics profiles
		 */
		final TacticsProfilesCache profiles = new TacticsProfilesCache(
				eventbUIPrefStore);
		profiles.load();
		/**
		 * Gets the default auto tactics profile
		 */
		final String defaultAutoTactics = getDefaultAutoTactics();
		final IPrefMapEntry<ITacticDescriptor> defaultAuto = profiles
				.getEntry(defaultAutoTactics);
		final ITacticDescriptor defaultAutoTac = defaultAuto.getValue();
		final ICombinedTacticDescriptor defaultAutoDesc = (ICombinedTacticDescriptor) defaultAutoTac;
		final String combinatorId = defaultAutoDesc.getCombinatorId();
		/**
		 * Makes a copy of the unmodifiable list.
		 */
		final List<ITacticDescriptor> combinedTactics = new ArrayList<ITacticDescriptor>(
				defaultAutoDesc.getCombinedTactics());
		/**
		 * Adds the SMT PP tactic right after the
		 * BoundedGoalWithFiniteHypotheses tactic. We used this position for
		 * performance tests.
		 */
		final int afterBoundedGoalWithFiniteHyps = 5;
		combinedTactics.add(afterBoundedGoalWithFiniteHyps, makeSMTPPTactic());
		final ICombinatorDescriptor combinator = getAutoTacticRegistry()
				.getCombinatorDescriptor(combinatorId);
		final ICombinedTacticDescriptor defaultAutoWithSMTPP = combinator
				.combine(combinedTactics, "SMTTactic");
		/**
		 * If the SMT profile does not exist yet, stores it in the preferences.
		 */
		if (profiles.getEntry(AUTO_TACTIC_SMTPP_PROFILE_ID) == null) {
			profiles.add(AUTO_TACTIC_SMTPP_PROFILE_ID, defaultAutoWithSMTPP);
			profiles.store();
		}
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		removeIncorrectInternalConfigs();
		boolean addSMTProfile = true;
		try {
			addSolverConfig(getVeriTConfig());
		} catch (IllegalArgumentException iae) {
			addSMTProfile = false;
			throw iae;
		}
		// TODO uncomment when fragments are created for each target platform
		// addSolverConfig(getCvc3Config());
		setSelectedSolverIndex();
		setVeriTPath();
		if (addSMTProfile)
			addSMTProfile();
	}

	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		final IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SmtProversUIPlugin getDefault() {
		plugin.getPreferenceStore();
		return plugin;
	}

	public static IPreferenceStore getDefaultPreferenceStore() {
		return plugin.getPreferenceStore();
	}

}
