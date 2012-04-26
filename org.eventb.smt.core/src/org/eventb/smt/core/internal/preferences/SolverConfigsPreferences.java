/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import static org.eventb.smt.core.internal.log.SMTStatus.smtWarning;
import static org.eventb.smt.core.preferences.PreferenceManager.SOLVER_CONFIGS_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.configExists;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.eventb.smt.core.internal.log.SMTStatus;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigsPreferences extends AbstractPreferences implements
		ISolverConfigsPreferences {
	private static final SolverConfigsPreferences CONFIGS_PREFS = new SolverConfigsPreferences(
			!USE_DEFAULT_SCOPE);
	private static final SolverConfigsPreferences DEFAULT_CONFIGS_PREFS = new SolverConfigsPreferences(
			USE_DEFAULT_SCOPE);

	public static final String DEFAULT_CONFIGS = ""; //$NON-NLS-1$

	private Map<String, ISolverConfig> solverConfigs;

	private SolverConfigsPreferences(boolean useDefaultScope) {
		super(useDefaultScope);
	}

	private void add(final ISolverConfig solverConfig,
			final Map<String, ISMTSolver> solvers, final boolean replace)
			throws IllegalArgumentException {
		// FIXME exception thrown ?
		try {
			final String solverId = solverConfig.getSolverId();
			final ISMTSolver solver = solvers.get(solverId);
			if (isValidPath(solver.getPath().toOSString())) {
				final String id = solverConfig.getID();
				if (replace) {
					removeConfigsWithNameOf(solverConfig);
					solverConfigs.put(id, solverConfig);
				} else if (!solverConfigs.containsKey(id)
						&& !configExists(solverConfig.getName())) {
					try {
						int numericID = Integer.parseInt(id);
						idCounter = numericID;
					} catch (NumberFormatException e) {
						// do nothing
					}
					solverConfigs.put(id, solverConfig);
				}
			} else {
				throw new IllegalArgumentException(
						"Could not add the SMT-solver configuration: the solver path is invalid."); //$NON-NLS-1$
			}
		} catch (NullPointerException npe) {
			SMTStatus.smtError("Null pointer exception thrown.", npe);
		}
	}

	public static SolverConfigsPreferences getSolverConfigsPrefs(
			final boolean reload) {
		CONFIGS_PREFS.load(reload);
		return CONFIGS_PREFS;
	}

	public static SolverConfigsPreferences getDefaultSolverConfigsPrefs(
			final boolean reload) {
		DEFAULT_CONFIGS_PREFS.load(reload);
		return DEFAULT_CONFIGS_PREFS;
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
	private static Map<String, ISolverConfig> parse(final String preferences)
			throws PatternSyntaxException {
		final Map<String, ISolverConfig> solverConfigs = new LinkedHashMap<String, ISolverConfig>();

		final String[] rows = preferences.split(SEPARATOR);
		for (final String row : rows) {
			if (row.length() > 0) {
				final ISolverConfig solverConfig = SolverConfiguration
						.parseConfig(row);
				// TODO if not editable check the solver exists
				final String id = solverConfig.getID();
				final String name = solverConfig.getName();
				if (nameAlreadyInUse(solverConfigs.values(), name)) {
					smtWarning("The configuration \'" + id
							+ "\' was not added "
							+ "because of its duplicated name \'" + name
							+ "\'.");
				} else {
					if (solverConfigs.containsKey(id)) {
						smtWarning("The configuration ID \'" + id
								+ "\' (name:\'" + name + "\') already exists "
								+ "and will be overwritten.");
					}
					solverConfigs.put(id, solverConfig);
				}
				solverConfigs.put(solverConfig.getID(), solverConfig);
			}
		}
		return solverConfigs;
	}

	private static final boolean nameAlreadyInUse(
			final Collection<ISolverConfig> configs, final String name) {
		for (final ISolverConfig config : configs) {
			if (config.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static final String toString(
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

	// FIXME same code as in SolverConfigsPreferences
	private void removeConfigsWithNameOf(final ISolverConfig config) {
		final String name = config.getName();
		final Iterator<ISolverConfig> configsIterator = solverConfigs.values()
				.iterator();
		while (configsIterator.hasNext()) {
			final ISolverConfig curConfig = configsIterator.next();
			if (curConfig.getName().equals(name)) {
				configsIterator.remove();
				smtWarning("The configuration \'" + curConfig.getID()
						+ "\' was removed "
						+ "because of its duplicated name \'" + name + "\'.");
			}
		}
	}

	@Override
	public String configNameToId(final String name) {
		for (Map.Entry<String, ISolverConfig> configEntry : solverConfigs
				.entrySet()) {
			if (configEntry.getValue().getName().equals(name)) {
				return configEntry.getKey();
			}
		}
		return null;
	}

	@Override
	public void load(boolean reload) {
		if (loaded && !reload) {
			return;
		}
		solverConfigs = parse(prefsNode.get(SOLVER_CONFIGS_ID, DEFAULT_CONFIGS));
		idCounter = getHighestID(solverConfigs);
		loaded = true;
	}

	@Override
	public void loadDefault() {
		solverConfigs = new LinkedHashMap<String, ISolverConfig>(
				getDefaultSolverConfigsPrefs(!FORCE_RELOAD).getSolverConfigs());
	}

	@Override
	public void save() {
		prefsNode.put(SOLVER_CONFIGS_ID, toString(solverConfigs));
	}

	@Override
	public boolean validId(final String id) {
		return !id.isEmpty() && !solverConfigs.containsKey(id);
	}

	@Override
	public Map<String, ISolverConfig> getSolverConfigs() {
		return solverConfigs;
	}

	@Override
	public ISolverConfig getSolverConfig(final String configId) {
		return solverConfigs.get(configId);
	}

	@Override
	public void add(final ISolverConfig solverConfig, final boolean replace)
			throws IllegalArgumentException {
		// FIXME shall reload solvers ?
		final Map<String, ISMTSolver> solvers = getPreferenceManager()
				.getSMTSolversPrefs().getSolvers();
		add(solverConfig, solvers, replace);
	}

	@Override
	public void add(final ISolverConfig solverConfig)
			throws IllegalArgumentException {
		add(solverConfig, !FORCE_REPLACE);
	}

	@Override
	public void setConfigEnabled(String configID, boolean enabled) {
		solverConfigs.get(configID).setEnabled(enabled);
	}

	@Override
	public void removeSolverConfig(final String configID) {
		solverConfigs.remove(configID);
	}

	// FIXME how to filter with efficiency ?
	@Override
	public List<ISolverConfig> getEnabledConfigs() {
		final List<ISolverConfig> enabledConfigs = new ArrayList<ISolverConfig>();
		final Iterator<ISolverConfig> configsIterator = solverConfigs.values()
				.iterator();
		while (configsIterator.hasNext()) {
			final ISolverConfig config = configsIterator.next();
			if (config.isEnabled()) {
				enabledConfigs.add(config);
			}
		}
		return enabledConfigs;
	}

	@Override
	public String freshID() {
		return freshID(solverConfigs);
	}

	@Override
	public String freshCopyName(final String originalName) {
		int i = 1;
		String copyName = originalName + " (copy" + i + ")";
		while (configExists(copyName)) {
			i++;
			copyName = originalName + " (copy" + i + ")";
		}
		return copyName;
	}

	@Override
	public Set<ISolverConfig> relatedConfigs(String solverId) {
		final Set<ISolverConfig> relatedConfigs = new HashSet<ISolverConfig>();
		for (final ISolverConfig config : solverConfigs.values()) {
			if (config.getSolverId().equals(solverId)) {
				relatedConfigs.add(config);
			}
		}
		return relatedConfigs;
	}
}
