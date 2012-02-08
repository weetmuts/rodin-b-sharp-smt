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

import static org.eventb.smt.cvc3.core.Cvc3ProverCore.getCvc3Config;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.parsePreferencesString;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.contains;
import static org.eventb.smt.verit.core.VeriTProverCore.getVeriTConfig;
import static org.eventb.smt.verit.core.VeriTProverCore.getVeriTPath;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SmtProversUIPlugin extends AbstractUIPlugin {
	/**
	 * plug-in id
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.ui";

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

	private static void addSolverConfig(
			final SMTSolverConfiguration solverConfig) {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		final String preferences = preferenceStore
				.getString(SOLVER_PREFERENCES_ID);
		List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(preferences);
		if (!contains(solverConfigs, solverConfig)) {
			solverConfigs.add(solverConfig);
		}
		preferenceStore.setValue(SOLVER_PREFERENCES_ID,
				SMTSolverConfiguration.toString(solverConfigs));
	}

	private static void setSelectedSolverIndex() {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		if (preferenceStore.getInt(SOLVER_INDEX_ID) < 0) {
			preferenceStore.setValue(SOLVER_INDEX_ID, 0);
		}
	}

	private static void setVeriTPath() {
		IPreferenceStore preferenceStore = plugin.getPreferenceStore();
		preferenceStore.setValue(VERIT_PATH_ID, getVeriTPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		addSolverConfig(getVeriTConfig());
		addSolverConfig(getCvc3Config());
		setSelectedSolverIndex();
		setVeriTPath();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
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
