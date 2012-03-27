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

import java.util.List;
import java.util.Map;

/**
 * @author Systerel (yguyot)
 * 
 */
public interface IPreferences {
	public abstract boolean validId(final String id);

	public abstract Map<String, ISMTSolver> getSolvers();

	public abstract Map<String, ISolverConfig> getSolverConfigs();

	public abstract List<ISolverConfig> getEnabledConfigs();

	public abstract ISMTSolver getSolver(final String solverId);

	public abstract ISolverConfig getSolverConfig(final String configId);

	public abstract void addSolver(ISMTSolver solver)
			throws IllegalArgumentException;

	public abstract void addSolverToDefault(ISMTSolver solver)
			throws IllegalArgumentException;

	public abstract void removeSMTSolver(String solverToRemove);

	public abstract void addSolverConfig(final ISolverConfig solverConfig);

	public abstract void addSolverConfigToDefault(
			final ISolverConfig solverConfig);

	public abstract void setConfigEnabled(final String configID,
			final boolean enabled);

	public abstract void removeSolverConfig(final String configID);

	public abstract String getTranslationPath();

	public abstract void setTranslationPath(final String path);

	public abstract String getVeriTPath();

	public abstract void setVeriTPath(final String path);

	public abstract void setDefaultVeriTPath(final String path);

	public abstract void save();
}
