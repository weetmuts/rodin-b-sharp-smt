/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.preferences;

import static java.io.File.separatorChar;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferencesError_cannot_execute;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferencesError_cannot_read;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferencesError_invalid_file;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferencesError_missing_path;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferencesError_no_file;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferences_IllegalSMTSolverSettings;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferences_NoSMTSolverSelected;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferences_NoSMTSolverSet;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferences_TranslationPathNotSet;
import static org.eventb.smt.internal.preferences.Messages.SMTPreferences_VeriTPathNotSet;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.contains;
import static org.eventb.smt.internal.provers.core.SMTProversCore.PLUGIN_ID;
import static org.eventb.smt.internal.provers.core.SMTSolver.parseSolver;
import static org.eventb.smt.internal.translation.SMTLIBVersion.parseVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * The SMT preferences class
 */
public class SMTPreferences {
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

	public static final String SEPARATOR1 = ",,"; //$NON-NLS-1$
	public static final String SEPARATOR2 = ";"; //$NON-NLS-1$
	public static final String TRANSLATION_PATH_ID = "translationpath"; //$NON-NLS-1$
	public static final String VERIT_PATH_ID = "veritpath"; //$NON-NLS-1$
	public static final String CONFIG_INDEX_ID = "configindex"; //$NON-NLS-1$
	public static final String SOLVER_PREFERENCES_ID = "solverpreferences"; //$NON-NLS-1$
	public static final String DEFAULT_SOLVER_PREFERENCES = ""; //$NON-NLS-1$
	public static final String DEFAULT_TRANSLATION_PATH = getProperty("java.io.tmpdir"); //$NON-NLS-1$
	public static final int DEFAULT_CONFIG_INDEX = -1;
	public static final String DEFAULT_VERIT_PATH = ""; //$NON-NLS-1$

	public static final IEclipsePreferences SMT_PREFS = ConfigurationScope.INSTANCE
			.getNode(PLUGIN_ID);
	public static final IEclipsePreferences DEFAULT_SMT_PREFS = DefaultScope.INSTANCE
			.getNode(PLUGIN_ID);

	private List<SMTSolverConfiguration> solverConfigs;
	private int selectedConfigIndex;
	private String translationPath;
	private String veriTPath;

	private List<SMTSolverConfiguration> defaultSolverConfigs;
	private String defaultVeriTPath;

	public SMTPreferences() {
		defaultSolverConfigs = new ArrayList<SMTSolverConfiguration>(0);
		defaultVeriTPath = DEFAULT_VERIT_PATH;
	}

	public SMTPreferences(final List<SMTSolverConfiguration> solverConfigs,
			final int selectedConfigIndex, final String translationPath,
			final String veriTPath) {
		this();
		this.solverConfigs = solverConfigs;
		this.selectedConfigIndex = selectedConfigIndex;
		this.translationPath = translationPath;
		this.veriTPath = veriTPath;
	}

	public static SMTPreferences getSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences();
		smtPrefs.loadPrefs();
		return smtPrefs;
	}

	public static SMTPreferences getDefaultSMTPrefs() {
		final SMTPreferences smtPrefs = new SMTPreferences();
		smtPrefs.loadDefaultPrefs();
		return smtPrefs;
	}

	/**
	 * Creates a list with all solverConfig detail elements from the preferences
	 * String
	 * 
	 * @param preferences
	 *            The String that contains the details of the solverConfig
	 * @return The list of solvers and its details parsed from the preferences
	 *         String
	 */
	private static List<SMTSolverConfiguration> parsePrefs(
			final String preferences) throws PatternSyntaxException {
		final List<SMTSolverConfiguration> solverConfig = new ArrayList<SMTSolverConfiguration>();

		final String[] rows = preferences.split(SEPARATOR2);
		for (final String row : rows) {
			if (row.length() > 0) {
				final String[] columns = row.split(SEPARATOR1);
				solverConfig.add(new SMTSolverConfiguration(columns[0],
						columns[1], parseSolver(columns[2]), columns[3],
						columns[4], parseVersion(columns[5]),
						parseBoolean(columns[6])));
			}
		}
		return solverConfig;
	}

	public void loadPrefs() {
		solverConfigs = parsePrefs(SMT_PREFS.get(SOLVER_PREFERENCES_ID,
				DEFAULT_SOLVER_PREFERENCES));
		selectedConfigIndex = SMT_PREFS.getInt(CONFIG_INDEX_ID,
				DEFAULT_CONFIG_INDEX);
		translationPath = SMT_PREFS.get(TRANSLATION_PATH_ID,
				DEFAULT_TRANSLATION_PATH);
		veriTPath = SMT_PREFS.get(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
	}

	public void loadDefaultPrefs() {
		solverConfigs = parsePrefs(DEFAULT_SMT_PREFS.get(SOLVER_PREFERENCES_ID,
				DEFAULT_SOLVER_PREFERENCES));
		selectedConfigIndex = DEFAULT_SMT_PREFS.getInt(CONFIG_INDEX_ID,
				DEFAULT_CONFIG_INDEX);
		translationPath = DEFAULT_SMT_PREFS.get(TRANSLATION_PATH_ID,
				DEFAULT_TRANSLATION_PATH);
		veriTPath = DEFAULT_SMT_PREFS.get(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
	}

	public void savePrefs() {
		SMT_PREFS.put(SOLVER_PREFERENCES_ID,
				SMTSolverConfiguration.toString(solverConfigs));
		SMT_PREFS.putInt(CONFIG_INDEX_ID, selectedConfigIndex);
		SMT_PREFS.put(TRANSLATION_PATH_ID, translationPath);
		SMT_PREFS.put(VERIT_PATH_ID, veriTPath);
	}

	public void saveDefaultPrefs() {
		DEFAULT_SMT_PREFS.put(SOLVER_PREFERENCES_ID,
				SMTSolverConfiguration.toString(defaultSolverConfigs));
		DEFAULT_SMT_PREFS.putInt(CONFIG_INDEX_ID, DEFAULT_CONFIG_INDEX);
		DEFAULT_SMT_PREFS.put(TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH);
		DEFAULT_SMT_PREFS.put(VERIT_PATH_ID, defaultVeriTPath);
	}

	public List<SMTSolverConfiguration> getSolverConfigs() {
		return solverConfigs;
	}

	public SMTSolverConfiguration getSelectedSolverConfiguration() {
		try {
			return solverConfigs.get(selectedConfigIndex);
		} catch (final IndexOutOfBoundsException ioobe) {
			if (solverConfigs.size() > 0) {
				throw NoSMTSolverSelectedException;
			} else {
				throw NoSMTSolverSetException;
			}
		}
	}

	public SMTSolverConfiguration getSolverConfiguration(final String configId) {
		for (final SMTSolverConfiguration solverConfig : solverConfigs) {
			if (solverConfig.getId().equals(configId)) {
				return solverConfig;
			}
		}
		return null;
	}

	public int getSelectedConfigIndex() {
		return selectedConfigIndex;
	}

	public String getTranslationPath() {
		return translationPath;
	}

	public String getVeriTPath() {
		return veriTPath;
	}

	private static final String getValidPath(final String currentPath,
			final String newPath, final String defaultPath) {
		if (isPathValid(newPath)) {
			return newPath;
		} else if (!isPathValid(currentPath)) {
			return defaultPath;
		} else {
			return currentPath;
		}
	}

	public static boolean isPathValid(final String path) {
		return isPathValid(path, new StringBuilder(0));
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

	public boolean validId(final String id) {
		final Set<String> usedIds = SMTSolverConfiguration
				.getIDs(solverConfigs);
		return !id.isEmpty() && !usedIds.contains(id);
	}

	private static void addSolverConfig(
			List<SMTSolverConfiguration> solverConfigs,
			final SMTSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		if (isPathValid(solverConfig.getPath())) {
			if (!contains(solverConfigs, solverConfig)) {
				solverConfigs.add(solverConfig);
			}
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver configuration: invalid path."); //$NON-NLS-1$
		}
	}

	public void addSolverConfig(final SMTSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(solverConfigs, solverConfig);
	}

	public void addSolverConfigToDefault(
			final SMTSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(defaultSolverConfigs, solverConfig);
	}

	public void removeSolverConfig(final int configIndex) {
		solverConfigs.remove(configIndex);
		if (selectedConfigIndex > configIndex) {
			selectedConfigIndex--;
		} else if (selectedConfigIndex == configIndex) {
			if (solverConfigs.size() > 0) {
				selectedConfigIndex = 0;
			} else {
				selectedConfigIndex = -1;
			}
		}
	}

	/**
	 * Tells whether the current selection index is valid or not
	 * 
	 * @return whether the current selection index is valid or not
	 */
	public boolean selectedConfigIndexValid() {
		return selectedConfigIndex >= 0
				&& selectedConfigIndex < solverConfigs.size();
	}

	public void setSelectedConfigIndex(final boolean selectionRequested,
			final int selectionIndex) {
		/**
		 * If there is only one solver set in the table, it is selected for SMT
		 * proofs
		 */
		if (solverConfigs.size() == 1) {
			selectedConfigIndex = 0;
		} else {
			/**
			 * Else, if a selection was requested, the corresponding
			 * configuration is selected for SMT proofs.
			 */
			if (selectionRequested) {
				selectedConfigIndex = selectionIndex;
			} else {
				/**
				 * Else if the current selected solver is not valid...
				 */
				if (!selectedConfigIndexValid()) {
					/**
					 * if there is some configurations set in the list, the
					 * first one is selected for SMT proofs, else the selected
					 * configuration index is set to -1.
					 */
					if (solverConfigs.size() > 1) {
						selectedConfigIndex = 0;
					} else {
						selectedConfigIndex = -1;
					}
				}
			}
		}
	}

	/**
	 * @param translationPath
	 *            the translationPath to set
	 */
	public void setTranslationPath(String translationPath) {
		this.translationPath = getValidPath(this.translationPath,
				translationPath, DEFAULT_TRANSLATION_PATH);
	}

	/**
	 * Sets veriT path to the path of the integrated veriT solver.
	 */
	public void setVeriTPath(final String veriTPath) {
		this.veriTPath = getValidPath(this.veriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}

	public void setDefaultVeriTPath(final String veriTPath) {
		this.defaultVeriTPath = getValidPath(this.defaultVeriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}

	/**
	 * Checks SMT-solver configurations which were added automatically by the
	 * plug-in. Particularly checks if paths are correct. If such a path is not
	 * correct, removes it.
	 */
	public void removeIncorrectInternalConfigs() {
		for (Iterator<SMTSolverConfiguration> configsIter = solverConfigs
				.iterator(); configsIter.hasNext();) {
			final SMTSolverConfiguration config = configsIter.next();
			final String path = config.getPath();
			if (path != null) {
				final StringBuilder builder = new StringBuilder();
				builder.append("configuration").append(separatorChar); //$NON-NLS-1$
				builder.append("org.eclipse.osgi").append(separatorChar); //$NON-NLS-1$
				builder.append("bundles"); //$NON-NLS-1$
				if (config.getPath().contains(builder.toString())
				/**
				 * for developpers only
				 */
				|| config.getPath().contains("org.eventb.smt.verit")) { //$NON-NLS-1$
					if (!isPathValid(path)) {
						configsIter.remove();
					}
				}
			} else {
				configsIter.remove();
			}
		}
	}
}