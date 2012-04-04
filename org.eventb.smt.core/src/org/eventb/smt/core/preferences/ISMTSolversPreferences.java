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

import java.util.Map;

/**
 * @author Systerel (yguyot)
 * 
 */
public interface ISMTSolversPreferences extends IPreferences {
	/**
	 * Returns a map of solvers accessible via their ids.
	 * 
	 * @return the map of solvers
	 */
	public Map<String, ISMTSolver> getSolvers();

	/**
	 * Returns the solver with the given ID
	 * 
	 * @param solverId
	 *            the ID of the wanted solver
	 * @return the solver with the given ID
	 */
	public ISMTSolver get(final String solverId);

	/**
	 * Adds a solver into the map
	 * 
	 * @param solver
	 *            the solver to add
	 */
	public void add(final ISMTSolver solver);

	/**
	 * Adds a solver into the map
	 * 
	 * @param solver
	 *            the solver to add
	 * @param replace
	 *            if true, any previously existing solver at the same ID will be
	 *            replaced, if false, do the same as
	 *            <code>add(ISMTSolver)</code>
	 */
	public void add(final ISMTSolver solver, final boolean replace);

	/**
	 * Removes the specified solver from the map
	 * 
	 * @param solverToRemove
	 *            the solver to remove
	 */
	public void remove(final String solverToRemove);

	/**
	 * Returns a fresh solver ID which is a numeral currently not present in the
	 * map keys
	 * 
	 * @return a fresh solver ID
	 */
	public String freshID();
}
