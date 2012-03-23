/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

import static java.lang.System.getProperty;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferencesError_cannot_execute;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferencesError_cannot_read;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferencesError_invalid_file;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferencesError_missing_path;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferencesError_no_file;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferences_IllegalSMTSolverSettings;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferences_NoSMTSolverSelected;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferences_NoSMTSolverSet;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferences_TranslationPathNotSet;
import static org.eventb.smt.core.internal.preferences.Messages.SMTPreferences_VeriTPathNotSet;
import static org.eventb.smt.core.internal.preferences.SMTPreferences.USE_DEFAULT_SCOPE;

import java.io.File;

import org.eventb.smt.core.internal.preferences.BundledSolverRegistry;
import org.eventb.smt.core.internal.preferences.SMTPreferences;
import org.eventb.smt.core.internal.preferences.SolverConfigRegistry;

/**
 * @author Systerel (yguyot)
 * 
 */
public class PreferenceManager {
	public static final String DEFAULT_TRANSLATION_PATH = getProperty("java.io.tmpdir"); //$NON-NLS-1$
	public static final String DEFAULT_SELECTED_CONFIG = "";
	public static final String TRANSLATION_PATH_ID = "translationpath"; //$NON-NLS-1$
	public static final String VERIT_PATH_ID = "veritpath"; //$NON-NLS-1$
	public static final String SELECTED_CONFIG_ID = "selectedconfig"; //$NON-NLS-1$
	public static final String SOLVER_CONFIGS_ID = "solverconfigs"; //$NON-NLS-1$
	public static final String SOLVERS_ID = "solvers"; //$NON-NLS-1$

	public static final IllegalArgumentException TranslationPathNotSetException = new IllegalArgumentException(
			SMTPreferences_TranslationPathNotSet);
	public static final IllegalArgumentException VeriTPathNotSetException = new IllegalArgumentException(
			SMTPreferences_VeriTPathNotSet);
	public static final IllegalArgumentException NoSMTSolverSetException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSet);
	public static final IllegalArgumentException NoSMTSolverSelectedException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSelected);
	public static final IllegalArgumentException IllegalSMTSolverSettingsException = new IllegalArgumentException(
			SMTPreferences_IllegalSMTSolverSettings);

	public static IPreferences getDefaultSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences(USE_DEFAULT_SCOPE);
		smtPrefs.load();
		return smtPrefs;
	}

	public static IPreferences getSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences(!USE_DEFAULT_SCOPE);
		smtPrefs.load();
		return smtPrefs;
	}

	public static boolean isPathValid(final String path,
			final StringBuilder error) {
		if (path == null) {
			return false;
		}
		if (path.isEmpty()) {
			error.append(SMTPreferencesError_missing_path);
			return false;
		}
		final File file = new File(path);
		try {
			if (!file.exists()) {
				error.append(SMTPreferencesError_no_file);
				return false;
			}
			if (!file.isFile()) {
				error.append(SMTPreferencesError_invalid_file);
				return false;
			}
			if (!file.canExecute()) {
				error.append(SMTPreferencesError_cannot_execute);
				return false;
			}
			return true;

		} catch (SecurityException se) {
			error.append(SMTPreferencesError_cannot_read);
			return false;
		}
	}

	public static IRegistry<ISMTSolver> getBundledSolverRegistry() {
		return BundledSolverRegistry.getBundledSolverRegistry();
	}

	public static IRegistry<ISolverConfig> getSolverConfigRegistry() {
		return SolverConfigRegistry.getSolverConfigRegistry();
	}
}
