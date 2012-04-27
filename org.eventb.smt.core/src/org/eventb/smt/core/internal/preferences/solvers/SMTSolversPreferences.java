/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences.solvers;

import static org.eventb.smt.core.internal.log.SMTStatus.smtWarning;
import static org.eventb.smt.core.preferences.PreferenceManager.SOLVERS_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.solverExists;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.eventb.smt.core.internal.preferences.AbstractPreferences;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTSolversPreferences extends AbstractPreferences implements
		ISMTSolversPreferences {

	private static final SMTSolversPreferences SOLVERS_PREFS = new SMTSolversPreferences(
			!USE_DEFAULT_SCOPE);
	private static final SMTSolversPreferences DEFAULT_SOLVERS_PREFS = new SMTSolversPreferences(
			USE_DEFAULT_SCOPE);

	public static final String DEFAULT_SOLVERS = ""; //$NON-NLS-1$

	private Map<String, ISMTSolver> solvers;

	private SMTSolversPreferences(boolean useDefaultScope) {
		super(useDefaultScope);
	}

	public static SMTSolversPreferences getSMTSolversPrefs(final boolean reload) {
		SOLVERS_PREFS.load(reload);
		return SOLVERS_PREFS;
	}

	public static SMTSolversPreferences getDefaultSMTSolversPrefs(
			final boolean reload) {
		DEFAULT_SOLVERS_PREFS.load(reload);
		return DEFAULT_SOLVERS_PREFS;
	}

	/**
	 * Creates a map with all solver elements from the preferences string
	 * 
	 * @param preferences
	 *            The string that contains the details of the solver
	 * @return The map of solvers and its details parsed from the preferences
	 *         string
	 */
	private static Map<String, ISMTSolver> parse(final String preferences)
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
				if (!solver.isEditable() || isValidPath(path)) {
					final String id = solver.getID();
					final String name = solver.getName();
					if (nameAlreadyInUse(solvers.values(), name)) {
						smtWarning("The solver \'" + id + "\' was not added "
								+ "because of its duplicated name \'" + name
								+ "\'.");
					} else {
						if (solvers.containsKey(id)) {
							smtWarning("The solver ID \'" + id + "\' (name:\'"
									+ name + "\') already exists "
									+ "and will be overwritten.");
						}
						solvers.put(id, solver);
					}
				}
			}
		}
		return solvers;
	}

	private static final boolean nameAlreadyInUse(
			final Collection<ISMTSolver> solvers, final String name) {
		for (final ISMTSolver solver : solvers) {
			if (solver.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static final String toString(final Map<String, ISMTSolver> solvers) {
		final StringBuilder sb = new StringBuilder();

		String separator = "";
		for (final ISMTSolver solver : solvers.values()) {
			sb.append(separator);
			solver.toString(sb);
			separator = SEPARATOR;
		}

		return sb.toString();
	}

	// FIXME same code as in SolverConfigsPreferences
	private void removeSolversCalledAs(final ISMTSolver solver) {
		final String name = solver.getName();
		final Iterator<ISMTSolver> solversIterator = solvers.values()
				.iterator();
		while (solversIterator.hasNext()) {
			final ISMTSolver curSolver = solversIterator.next();
			if (curSolver.getName().equals(name)) {
				solversIterator.remove();
				smtWarning("The solver \'" + curSolver.getID()
						+ "\' was removed "
						+ "because of its duplicated name \'" + name + "\'.");
			}
		}
	}

	@Override
	protected void load(final boolean reload) {
		if (loaded && !reload) {
			return;
		}
		solvers = parse(prefsNode.get(SOLVERS_ID, DEFAULT_SOLVERS));
		idCounter = getHighestID(solvers);
		loaded = true;
	}

	@Override
	public void loadDefault() {
		solvers = new LinkedHashMap<String, ISMTSolver>(
				getDefaultSMTSolversPrefs(!FORCE_RELOAD).getSolvers());
	}

	@Override
	public void save() {
		prefsNode.put(SOLVERS_ID, toString(solvers));
	}

	@Override
	public Map<String, ISMTSolver> getSolvers() {
		return solvers;
	}

	@Override
	public ISMTSolver get(final String solverId) {
		return solvers.get(solverId);
	}

	@Override
	public void add(final ISMTSolver solver, final boolean replace)
			throws IllegalArgumentException {
		if (isValidPath(solver.getPath().toOSString())) {
			final String id = solver.getID();
			if (replace) {
				removeSolversCalledAs(solver);
				solvers.put(id, solver);
			} else if (!solvers.containsKey(id)
					&& !solverExists(solver.getName())) {
				try {
					int numericID = Integer.parseInt(id);
					idCounter = numericID;
				} catch (NumberFormatException e) {
					// do nothing
				}
				solvers.put(id, solver);
			}
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver: invalid path."); //$NON-NLS-1$
		}
	}

	@Override
	public void add(final ISMTSolver solver) throws IllegalArgumentException {
		add(solver, !FORCE_REPLACE);
	}

	@Override
	public void remove(final String solverID) {
		solvers.remove(solverID);
	}

	@Override
	public String freshID() {
		return freshID(solvers);
	}
}
