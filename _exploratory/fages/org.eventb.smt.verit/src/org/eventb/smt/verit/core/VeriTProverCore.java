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

import static org.eventb.smt.internal.preferences.SMTPreferences.getDefaultSMTPrefs;
import static org.eventb.smt.internal.preferences.SMTPreferences.getSMTPrefs;
import static org.eventb.smt.internal.provers.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V2_0;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.eventb.smt.internal.preferences.SMTPreferences;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.verit.internal.core.ProverShell;

/**
 * @author Systerel (yguyot)
 * 
 */
public class VeriTProverCore extends Plugin implements IStartup {
	public static final String PLUGIN_ID = "org.eventb.smt.verit";

	private static final String VERIT_CONFIG_ID = "integrated_verit";
	private static final String VERIT_PATH = ProverShell.getVeriTPath();
	private static final String VERIT_ARGS = "-i smtlib2 --disable-print-success --disable-banner --proof=- --proof-version=1 --proof-prune --disable-e --max-time=3";

	public static final SMTSolverConfiguration VERIT_CONFIG = new SMTSolverConfiguration(
			VERIT_CONFIG_ID, VERIT, VERIT_PATH, VERIT_ARGS, V2_0);

	public VeriTProverCore() {
	}

	@Override
	public void earlyStartup() {
		final SMTPreferences smtPrefs = getSMTPrefs();
		final SMTPreferences smtDefaultPrefs = getDefaultSMTPrefs();
		try {
			smtDefaultPrefs.addSolverConfigToDefault(VERIT_CONFIG);
			smtPrefs.addSolverConfig(VERIT_CONFIG);
			// TODO uncomment when fragments are created for each target
			// platform
			// addSolverConfig(getCvc3Config());
		} catch (IllegalArgumentException iae) {
			throw iae;
		} finally {
			smtDefaultPrefs.setSelectedConfigIndex(false, 0);
			smtDefaultPrefs.setDefaultVeriTPath(VERIT_PATH);
			smtDefaultPrefs.saveDefaultPrefs();

			smtPrefs.setSelectedConfigIndex(false, 0);
			smtPrefs.setVeriTPath(VERIT_PATH);
			smtPrefs.savePrefs();
		}
	}
}
