/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.verit.core;

import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.parsePreferencesString;
import static org.eventb.smt.internal.provers.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.verit.internal.core.ProverShell.getVeriTPath;

import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.ui.SmtProversUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Systerel (yguyot)
 * 
 */
public class VeriTProverCore extends Plugin {
	public static final String PLUGIN_ID = "org.eventb.smt.verit";

	private static final String VERIT_CONFIG = "veriT";
	private static final String VERIT_ARGS = "-i smtlib2 --disable-print-success --disable-banner --proof=- --proof-version=1 --proof-prune --enable-e --max-time=3";

	public VeriTProverCore() {
	}

	private static void setVeriTConfig() {
		SmtProversUIPlugin uiPlugin = SmtProversUIPlugin.getDefault();
		IPreferenceStore preferenceStore = uiPlugin.getPreferenceStore();
		final String preferences = preferenceStore
				.getString(SOLVER_PREFERENCES_ID);
		List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(preferences);
		final String veriTPath = getVeriTPath();
		final SMTSolverConfiguration veriTConfig = new SMTSolverConfiguration(
				VERIT_CONFIG, VERIT, veriTPath, VERIT_ARGS, V2_0);
		if (!solverConfigs.contains(veriTConfig)) {
			solverConfigs.add(veriTConfig);
		}
		preferenceStore.setValue(SOLVER_PREFERENCES_ID,
				SMTSolverConfiguration.toString(solverConfigs));
		preferenceStore.setValue(VERIT_PATH_ID, veriTPath);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		setVeriTConfig();
		super.start(context);
	}
}
