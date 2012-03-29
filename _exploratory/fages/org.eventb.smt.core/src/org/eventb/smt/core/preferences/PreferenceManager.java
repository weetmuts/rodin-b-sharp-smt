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
import static org.eventb.smt.core.internal.preferences.AbstractPreferences.IDS_UPPER_BOUND;
import static org.eventb.smt.core.internal.preferences.AbstractPreferences.RANDOM;
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

import java.io.File;
import java.util.Set;

import org.eventb.smt.core.internal.log.SMTStatus;
import org.eventb.smt.core.internal.preferences.AbstractPreferences;
import org.eventb.smt.core.internal.preferences.BundledSolverRegistry;
import org.eventb.smt.core.internal.preferences.SMTSolversPreferences;
import org.eventb.smt.core.internal.preferences.SolverConfigRegistry;
import org.eventb.smt.core.internal.preferences.SolverConfigsPreferences;
import org.eventb.smt.core.internal.preferences.SolverConfiguration;
import org.eventb.smt.core.internal.preferences.TranslationPreferences;

/**
 * @author Systerel (yguyot)
 * 
 */
public class PreferenceManager {
	private static final PreferenceManager SINGLETON = new PreferenceManager();

	public static final String DEFAULT_TRANSLATION_PATH = getProperty("java.io.tmpdir"); //$NON-NLS-1$
	public static final String DEFAULT_SELECTED_CONFIG = "";
	public static final String TRANSLATION_PATH_ID = "translationpath"; //$NON-NLS-1$
	public static final String VERIT_PATH_ID = "veritpath"; //$NON-NLS-1$
	public static final String SELECTED_CONFIG_ID = "selectedconfig"; //$NON-NLS-1$
	public static final String SOLVER_CONFIGS_ID = "solverconfigs"; //$NON-NLS-1$
	public static final String SOLVERS_ID = "solvers"; //$NON-NLS-1$
	public static final boolean FORCE_REPLACE = AbstractPreferences.FORCE_REPLACE;
	public static final boolean FORCE_RELOAD = AbstractPreferences.FORCE_RELOAD;

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

	private PreferenceManager() {
		// do nothing
	}

	public static final PreferenceManager getPreferenceManager() {
		return SINGLETON;
	}

	public ISMTSolversPreferences getSMTSolversPrefs(final boolean reload) {
		return SMTSolversPreferences.getSMTSolversPrefs(reload);
	}

	public ISMTSolversPreferences getDefaultSMTSolversPrefs(final boolean reload) {
		return SMTSolversPreferences.getDefaultSMTSolversPrefs(reload);
	}

	public ISMTSolversPreferences getSMTSolversPrefs() {
		return getSMTSolversPrefs(!FORCE_RELOAD);
	}

	public ISMTSolversPreferences getDefaultSMTSolversPrefs() {
		return getDefaultSMTSolversPrefs(!FORCE_RELOAD);
	}

	public ISolverConfigsPreferences getSolverConfigsPrefs(final boolean reload) {
		return SolverConfigsPreferences.getSolverConfigsPrefs(reload);
	}

	public ISolverConfigsPreferences getDefaultSolverConfigsPrefs(
			final boolean reload) {
		return SolverConfigsPreferences.getDefaultSolverConfigsPrefs(reload);
	}

	public ISolverConfigsPreferences getSolverConfigsPrefs() {
		return getSolverConfigsPrefs(!FORCE_RELOAD);
	}

	public ISolverConfigsPreferences getDefaultSolverConfigsPrefs() {
		return getDefaultSolverConfigsPrefs(!FORCE_RELOAD);
	}

	public ITranslationPreferences getTranslationPrefs(final boolean reload) {
		return TranslationPreferences.getTranslationPrefs(reload);
	}

	public ITranslationPreferences getDefaultTranslationPrefs(
			final boolean reload) {
		return TranslationPreferences.getDefaultTranslationPrefs(reload);
	}

	public ITranslationPreferences getTranslationPrefs() {
		return getTranslationPrefs(!FORCE_RELOAD);
	}

	public ITranslationPreferences getDefaultTranslationPrefs() {
		return getDefaultTranslationPrefs(!FORCE_RELOAD);
	}

	public static boolean isPathValid(final String path,
			final StringBuilder error) {
		if (path == null) {
			error.append(SMTPreferencesError_missing_path);
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

	public static final int parseTimeOut(final String timeOutStr) {
		return SolverConfiguration.parseTimeOut(timeOutStr);
	}

	public static boolean configExists(final String name) {
		for (final ISolverConfig config : SINGLETON.getSolverConfigsPrefs()
				.getSolverConfigs().values()) {
			if (name.equals(config.getName()))
				return true;
		}
		return false;
	}

	public static boolean solverExists(final String name) {
		for (final ISMTSolver solver : SINGLETON.getSMTSolversPrefs()
				.getSolvers().values()) {
			if (name.equals(solver.getName()))
				return true;
		}
		return false;
	}

	public static String freshID(final Set<String> usedIDs) {
		if (usedIDs.size() == IDS_UPPER_BOUND) {
			SMTStatus.smtError("Too many items.", null);
			return null;
		}
		int randomID = RANDOM.nextInt(IDS_UPPER_BOUND);
		while (usedIDs.contains(randomID)) {
			randomID = RANDOM.nextInt(IDS_UPPER_BOUND);
		}
		return Integer.toString(randomID);
	}

	public static String freshConfigID() {
		return freshID(SINGLETON.getSolverConfigsPrefs().getSolverConfigs()
				.keySet());
	}

	public static String freshSolverID() {
		return freshID(SINGLETON.getSMTSolversPrefs().getSolvers().keySet());
	}

	public static IRegistry<ISMTSolver> getBundledSolverRegistry() {
		return BundledSolverRegistry.getBundledSolverRegistry();
	}

	public static IRegistry<ISolverConfig> getSolverConfigRegistry() {
		return SolverConfigRegistry.getSolverConfigRegistry();
	}
}
