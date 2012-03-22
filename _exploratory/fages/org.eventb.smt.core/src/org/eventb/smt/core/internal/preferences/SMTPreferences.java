/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.preferences;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.AbstractPreferences;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * The SMT preferences class
 */
public class SMTPreferences extends AbstractPreferences {
	public static final boolean USE_DEFAULT_SCOPE = true;

	public static final String SEPARATOR = ";"; //$NON-NLS-1$
	public static final String DEFAULT_SOLVERS = ""; //$NON-NLS-1$
	public static final String DEFAULT_CONFIGS = ""; //$NON-NLS-1$
	public static final String DEFAULT_VERIT_PATH = ""; //$NON-NLS-1$

	public static final IEclipsePreferences SMT_PREFS = ConfigurationScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);
	public static final IEclipsePreferences DEFAULT_SMT_PREFS = DefaultScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);

	private final IEclipsePreferences prefsNode;
	private Map<String, ISMTSolver> solvers;
	private Map<String, ISolverConfig> solverConfigs;
	private String selectedConfigID;
	private String translationPath;
	private String veriTPath;

	private Map<String, ISMTSolver> defaultSolvers;
	private Map<String, ISolverConfig> defaultSolverConfigs;
	private String defaultVeriTPath;

	public SMTPreferences(boolean useDefaultScope) {
		if (useDefaultScope) {
			prefsNode = DEFAULT_SMT_PREFS;
		} else {
			prefsNode = SMT_PREFS;
		}
		defaultSolvers = new LinkedHashMap<String, ISMTSolver>();
		defaultSolverConfigs = new LinkedHashMap<String, ISolverConfig>();
		defaultVeriTPath = DEFAULT_VERIT_PATH;
	}

	public SMTPreferences(final boolean useDefaultScope,
			final Map<String, ISMTSolver> solvers,
			final Map<String, ISolverConfig> solverConfigs,
			final String selectedConfigID, final String translationPath,
			final String veriTPath) {
		this(useDefaultScope);
		this.solvers = solvers;
		this.solverConfigs = solverConfigs;
		this.selectedConfigID = selectedConfigID;
		this.translationPath = translationPath;
		this.veriTPath = veriTPath;
	}

	/**
	 * Creates a map with all solver configuration elements from the preferences
	 * string
	 * 
	 * @param preferences
	 *            The string that contains the solver configuration
	 * @return The map of configs and its details parsed from the preferences
	 *         string
	 */
	private static Map<String, ISolverConfig> parseConfigs(
			final String preferences) throws PatternSyntaxException {
		final Map<String, ISolverConfig> solverConfigs = new LinkedHashMap<String, ISolverConfig>();

		final String[] rows = preferences.split(SEPARATOR);
		for (final String row : rows) {
			if (row.length() > 0) {
				final ISolverConfig solverConfig = SolverConfiguration
						.parseConfig(row);
				// TODO if not editable check the solver exists
				solverConfigs.put(solverConfig.getID(), solverConfig);
			}
		}
		return solverConfigs;
	}

	/**
	 * Creates a map with all solver elements from the preferences string
	 * 
	 * @param preferences
	 *            The string that contains the details of the solver
	 * @return The map of solvers and its details parsed from the preferences
	 *         string
	 */
	private static Map<String, ISMTSolver> parseSolvers(final String preferences)
			throws PatternSyntaxException {
		final Map<String, ISMTSolver> solvers = new LinkedHashMap<String, ISMTSolver>();

		final String[] rows = preferences.split(SEPARATOR);
		for (final String row : rows) {
			if (row.length() > 0) {
				final ISMTSolver solver = SMTSolver.parseSolver(row);
				final String path = solver.getPath().toOSString();
				if (path == null) {
					continue;
				}
				/**
				 * Checks if the configuration was added automatically by the
				 * plug-in, then if its path is not correct, it is not added to
				 * the list.
				 */
				if (!solver.isEditable() || isPathValid(path)) {
					solvers.put(solver.getID(), solver);
				}
			}
		}
		return solvers;
	}

	public static final String configsToString(
			final Map<String, ISolverConfig> solverConfigs) {
		final StringBuilder sb = new StringBuilder();

		String separator = "";
		for (final ISolverConfig solverConfig : solverConfigs.values()) {
			sb.append(separator);
			solverConfig.toString(sb);
			separator = SEPARATOR;
		}

		return sb.toString();
	}

	public static final String solversToString(
			final Map<String, ISMTSolver> solvers) {
		final StringBuilder sb = new StringBuilder();

		String separator = "";
		for (final ISMTSolver solver : solvers.values()) {
			sb.append(separator);
			solver.toString(sb);
			separator = SEPARATOR;
		}

		return sb.toString();
	}

	public void load() {
		solvers = parseSolvers(prefsNode.get(SOLVERS_ID, DEFAULT_SOLVERS));
		solverConfigs = parseConfigs(prefsNode.get(SOLVER_CONFIGS_ID,
				DEFAULT_CONFIGS));
		selectedConfigID = prefsNode.get(SELECTED_CONFIG_ID,
				DEFAULT_SELECTED_CONFIG);
		translationPath = prefsNode.get(TRANSLATION_PATH_ID,
				DEFAULT_TRANSLATION_PATH);
		veriTPath = prefsNode.get(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
	}

	@Override
	public void save() {
		prefsNode.put(SOLVERS_ID, solversToString(solvers));
		prefsNode.put(SOLVER_CONFIGS_ID, configsToString(solverConfigs));
		prefsNode.put(SELECTED_CONFIG_ID, selectedConfigID);
		prefsNode.put(TRANSLATION_PATH_ID, translationPath);
		prefsNode.put(VERIT_PATH_ID, veriTPath);
	}

	@Override
	public Map<String, ISMTSolver> getSolvers() {
		return solvers;
	}

	@Override
	public Map<String, ISolverConfig> getSolverConfigs() {
		return solverConfigs;
	}

	@Override
	public ISolverConfig getSelectedConfig() {
		if (solverConfigs.size() <= 0)
			throw NoSMTSolverSetException;
		if (selectedConfigID == null || selectedConfigID.isEmpty())
			throw NoSMTSolverSelectedException;
		return solverConfigs.get(selectedConfigID);
	}

	@Override
	public ISMTSolver getSolver(final String solverId) {
		return solvers.get(solverId);
	}

	@Override
	public ISolverConfig getSolverConfig(final String configId) {
		return solverConfigs.get(configId);
	}

	@Override
	public String getSelectedConfigID() {
		return selectedConfigID;
	}

	@Override
	public String getTranslationPath() {
		return translationPath;
	}

	@Override
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

	@Override
	public boolean validId(final String id) {
		return !id.isEmpty() && !solverConfigs.containsKey(id);
	}

	private static void addSolverConfig(
			Map<String, ISolverConfig> solverConfigs,
			final ISolverConfig solverConfig) throws IllegalArgumentException {
		// FIXME exception thrown ?
		final String solverId = solverConfig.getSolverId();
		final ISMTSolver solver = SMTPreferences.getSMTPrefs().getSolver(
				solverId);
		if (isPathValid(solver.getPath().toOSString())) {
			final String id = solverConfig.getID();
			if (!solverConfigs.containsKey(id)) {
				solverConfigs.put(id, solverConfig);
			}
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver configuration: invalid path."); //$NON-NLS-1$
		}
	}

	private static void addSolver(Map<String, ISMTSolver> solvers,
			final ISMTSolver solver) throws IllegalArgumentException {
		if (isPathValid(solver.getPath().toOSString())) {
			final String id = solver.getID();
			if (!solvers.containsKey(id)) {
				solvers.put(id, solver);
			}
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver: invalid path."); //$NON-NLS-1$
		}
	}

	@Override
	public void addSolver(final ISMTSolver solver)
			throws IllegalArgumentException {
		addSolver(solvers, solver);
	}

	@Override
	public void addSolverToDefault(final ISMTSolver solver)
			throws IllegalArgumentException {
		addSolver(defaultSolvers, solver);
	}

	@Override
	public void removeSMTSolver(final String solverID) {
		solvers.remove(solverID);
	}

	@Override
	public void addSolverConfig(final ISolverConfig solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(solverConfigs, solverConfig);
	}

	@Override
	public void addSolverConfigToDefault(final ISolverConfig solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(defaultSolverConfigs, solverConfig);
	}

	@Override
	public void removeSolverConfig(final String configID) {
		solverConfigs.remove(configID);
		if (selectedConfigID.equals(configID)) {
			if (!solverConfigs.isEmpty()) {
				selectedConfigID = (String) solverConfigs.keySet().toArray()[0];
			}
		}
	}

	@Override
	public void setSelectedConfigID(final boolean selectionRequested,
			final String configID) {
		/**
		 * If there is only one solver set in the table, it is selected for SMT
		 * proofs
		 */
		if (solverConfigs.size() == 1) {
			selectedConfigID = (String) solverConfigs.keySet().toArray()[0];
		} else {
			/**
			 * Else, if a selection was requested, the corresponding
			 * configuration is selected for SMT proofs.
			 */
			if (selectionRequested) {
				selectedConfigID = configID;
			} else {
				/**
				 * Else if the current selected solver is not valid...
				 */
				if (!solverConfigs.containsKey(selectedConfigID)) {
					/**
					 * if there is some configurations set in the list, the
					 * first one is selected for SMT proofs, else the selected
					 * configuration index is set to -1.
					 */
					if (solverConfigs.size() > 1) {
						selectedConfigID = (String) solverConfigs.keySet()
								.toArray()[0];
					} else {
						selectedConfigID = DEFAULT_SELECTED_CONFIG;
					}
				}
			}
		}
	}

	/**
	 * @param translationPath
	 *            the translationPath to set
	 */
	@Override
	public void setTranslationPath(String translationPath) {
		this.translationPath = getValidPath(this.translationPath,
				translationPath, DEFAULT_TRANSLATION_PATH);
	}

	/**
	 * Sets veriT path to the path of the integrated veriT solver.
	 */
	@Override
	public void setVeriTPath(final String veriTPath) {
		this.veriTPath = getValidPath(this.veriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}

	@Override
	public void setDefaultVeriTPath(final String veriTPath) {
		this.defaultVeriTPath = getValidPath(this.defaultVeriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}
}