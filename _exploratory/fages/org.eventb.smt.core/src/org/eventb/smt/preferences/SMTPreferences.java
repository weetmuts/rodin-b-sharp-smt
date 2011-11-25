/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.preferences;

import static org.eventb.smt.preferences.Messages.SMTPreferences_IllegalSMTSolverSettings;
import static org.eventb.smt.preferences.Messages.SMTPreferences_NoSMTSolverSelected;
import static org.eventb.smt.preferences.Messages.SMTPreferences_NoSMTSolverSet;
import static org.eventb.smt.preferences.Messages.SMTPreferences_VeriTPathNotSet;
import static org.eventb.smt.provers.core.SMTProversCore.PREFERENCES_PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.smt.provers.internal.core.SMTSolver;
import org.eventb.smt.translation.SMTLIBVersion;

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
			Messages.SMTPreferences_TranslationPathNotSet);

	public static final String SEPARATOR1 = ",,";
	public static final String SEPARATOR2 = ";";
	public static final String TRANSLATION_PATH_ID = "translationpath";
	public static final String VERIT_PATH_ID = "veritpath";
	public static final String SOLVER_INDEX_ID = "solverindex";
	public static final String SOLVER_PREFERENCES_ID = "solverpreferences";
	public static final String DEFAULT_SOLVER_PREFERENCES = "";
	public static final String DEFAULT_TRANSLATION_PATH = System
			.getProperty("java.io.tmpdir");
	public static final int DEFAULT_SOLVER_INDEX = -1;
	public static final String DEFAULT_VERIT_PATH = "";

	/**
	 * Creates a list with all solverConfig detail elements from the preferences
	 * String
	 * 
	 * @param preferences
	 *            The String that contains the details of the solverConfig
	 * @return The list of solvers and its details parsed from the preferences
	 *         String
	 */
	public static List<SMTSolverConfiguration> parsePreferencesString(
			final String preferences) throws PatternSyntaxException {
		final List<SMTSolverConfiguration> solverDetail = new ArrayList<SMTSolverConfiguration>();

		final String[] rows = preferences.split(SEPARATOR2);
		for (final String row : rows) {
			if (row.length() > 0) {
				final String[] columns = row.split(SEPARATOR1);
				solverDetail.add(new SMTSolverConfiguration(columns[0],
						SMTSolver.getSolver(columns[1]), columns[2],
						columns[3], SMTLIBVersion.getVersion(columns[4])));
			}
		}
		return solverDetail;
	}

	public static SMTSolverConfiguration getSolverConfiguration() {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_PLUGIN_ID, SOLVER_PREFERENCES_ID,
				DEFAULT_SOLVER_PREFERENCES, null);
		final int selectedSolverIndex = preferencesService.getInt(
				PREFERENCES_PLUGIN_ID, SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX,
				null);
		final List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(solverPreferences);
		try {
			return solverConfigs.get(selectedSolverIndex);
		} catch (final IndexOutOfBoundsException ioobe) {
			if (solverConfigs.size() > 0) {
				throw NoSMTSolverSelectedException;
			} else {
				throw NoSMTSolverSetException;
			}
		}
	}

	public static SMTSolverConfiguration getSolverConfiguration(
			final String configId) {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_PLUGIN_ID, SOLVER_PREFERENCES_ID,
				DEFAULT_SOLVER_PREFERENCES, null);
		final List<SMTSolverConfiguration> solverConfigs = parsePreferencesString(solverPreferences);
		for (final SMTSolverConfiguration solverConfig : solverConfigs) {
			if (solverConfig.getId().equals(configId)) {
				return solverConfig;
			}
		}
		return null;
	}

	public static String getTranslationPath() {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		return preferencesService.getString(PREFERENCES_PLUGIN_ID,
				TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH, null);
	}

	public static String getVeriTPath() {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		return preferencesService.getString(PREFERENCES_PLUGIN_ID,
				VERIT_PATH_ID, DEFAULT_VERIT_PATH, null);
	}
}