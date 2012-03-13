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
import java.util.List;

import org.eventb.smt.core.internal.preferences.SMTPreferences;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractPreferences {
	public static final IllegalArgumentException IllegalSMTSolverSettingsException = new IllegalArgumentException(
			SMTPreferences_IllegalSMTSolverSettings);
	public static final IllegalArgumentException NoSMTSolverSelectedException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSelected);
	public static final IllegalArgumentException NoSMTSolverSetException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSet);
	public static final IllegalArgumentException VeriTPathNotSetException = new IllegalArgumentException(
			SMTPreferences_VeriTPathNotSet);
	public static final IllegalArgumentException TranslationPathNotSetException = new IllegalArgumentException(
			SMTPreferences_TranslationPathNotSet);

	public static final String SOLVER_PREFERENCES_ID = "solverpreferences"; //$NON-NLS-1$
	public static final String CONFIG_INDEX_ID = "configindex"; //$NON-NLS-1$
	public static final String VERIT_PATH_ID = "veritpath"; //$NON-NLS-1$
	public static final String TRANSLATION_PATH_ID = "translationpath"; //$NON-NLS-1$
	public static final int DEFAULT_CONFIG_INDEX = -1;
	public static final String DEFAULT_TRANSLATION_PATH = getProperty("java.io.tmpdir"); //$NON-NLS-1$

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

	public static AbstractPreferences getSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences(!USE_DEFAULT_SCOPE);
		smtPrefs.load();
		return smtPrefs;
	}

	public static AbstractPreferences getDefaultSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences(USE_DEFAULT_SCOPE);
		smtPrefs.load();
		return smtPrefs;
	}

	public abstract boolean validId(final String id);

	public abstract boolean selectedConfigIndexValid();

	public abstract List<AbstractSolverConfiguration> getSolverConfigs();

	public abstract AbstractSolverConfiguration getSolverConfiguration(
			final String configId);

	public abstract void addSolverConfig(
			final AbstractSolverConfiguration solverConfig);

	public abstract void addSolverConfigToDefault(
			final AbstractSolverConfiguration solverConfig);

	public abstract void removeSolverConfig(final int indexToRemove);

	public abstract int getSelectedConfigIndex();

	public abstract AbstractSolverConfiguration getSelectedSolverConfiguration();

	public abstract String getTranslationPath();

	public abstract String getVeriTPath();

	public abstract void setSelectedConfigIndex(
			final boolean selectionRequested, final int selectionIndex);

	public abstract void setTranslationPath(final String path);

	public abstract void setDefaultVeriTPath(final String path);

	public abstract void setVeriTPath(final String path);

	public abstract void save();
}
