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

import static java.lang.Boolean.parseBoolean;
import static org.eventb.smt.core.provers.SMTSolver.parseSolver;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;
import static org.eventb.smt.internal.preferences.SMTSolverConfiguration.SEPARATOR;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.smt.core.provers.SMTSolver;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigFactory {
	public static final ISolverConfiguration newConfig() {
		return new SMTSolverConfiguration();
	}

	public static final ISolverConfiguration newConfig(final String id,
			final String name, final SMTSolver solver, final String path,
			final String args, final SMTLIBVersion smtlibVersion) {
		return new SMTSolverConfiguration(id, name, solver, path, args,
				smtlibVersion);
	}

	public static final Set<String> getIDs(
			final List<ISolverConfiguration> solverConfigs) {
		final Set<String> usedIds = new HashSet<String>();
		for (final ISolverConfiguration solverConfig : solverConfigs) {
			usedIds.add(solverConfig.getID());
		}
		return usedIds;
	}

	public static final String toString(
			final List<ISolverConfiguration> solverConfigs) {
		final StringBuilder sb = new StringBuilder();

		for (final ISolverConfiguration solverConfig : solverConfigs) {
			solverConfig.toString(sb);
		}

		return sb.toString();
	}

	public final static ISolverConfiguration parse(final String solverConfigStr) {
		final String[] columns = solverConfigStr.split(SEPARATOR);
		return new SMTSolverConfiguration(columns[0], columns[1],
				parseSolver(columns[2]), columns[3], columns[4],
				parseVersion(columns[5]), parseBoolean(columns[6]));
	}
}
