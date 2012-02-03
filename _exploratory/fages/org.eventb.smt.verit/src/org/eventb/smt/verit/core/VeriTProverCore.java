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

import static org.eventb.smt.core.SMTCore.externalSMTThroughPP;
import static org.eventb.smt.core.SMTCore.externalSMTThroughVeriT;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.parsePreferencesString;
import static org.eventb.smt.internal.provers.core.SMTProversCore.DEFAULT_DELAY;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.verit.internal.core.ProverShell.getVeriTPath;

import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.ui.SmtProversUIPlugin;

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
		final SMTSolverConfiguration veriTConfig = new SMTSolverConfiguration(
				VERIT_CONFIG, VERIT, getVeriTPath(), VERIT_ARGS, V2_0);
		if (!solverConfigs.contains(veriTConfig)) {
			solverConfigs.add(veriTConfig);
		}
	}

	public static ITactic externalVeriTThroughPP(boolean restricted,
			long timeout) {
		return externalSMTThroughPP(restricted, timeout, VERIT_CONFIG);
	}

	public static ITactic externalVeriTThroughPP(final boolean restricted) {
		return externalVeriTThroughPP(restricted, DEFAULT_DELAY);
	}

	public static ITactic externalVeriTThroughVeriT(boolean restricted,
			long timeout) {
		return externalSMTThroughVeriT(restricted, timeout, VERIT_CONFIG);
	}

	public static ITactic externalVeriTThroughVeriT(final boolean restricted) {
		return externalVeriTThroughVeriT(restricted, DEFAULT_DELAY);
	}
}
