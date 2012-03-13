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
import static java.util.regex.Pattern.quote;
import static org.eventb.smt.core.internal.preferences.SMTSolverConfiguration.SEPARATOR;
import static org.eventb.smt.core.provers.SMTSolver.parseSolver;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.smt.core.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.core.provers.SMTSolver;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractSolverConfiguration {
	public abstract String getID();

	public abstract String getName();

	public abstract SMTSolver getSolver();

	public abstract String getPath();

	public abstract String getArgs();

	public abstract SMTLIBVersion getSmtlibVersion();

	public abstract boolean isEditable();

	public abstract void toString(final StringBuilder builder);

	public static final AbstractSolverConfiguration newConfig() {
		return new SMTSolverConfiguration();
	}

	public static final AbstractSolverConfiguration newConfig(final String id,
			final String name, final SMTSolver solver, final String path,
			final String args, final SMTLIBVersion smtlibVersion) {
		return new SMTSolverConfiguration(id, name, solver, path, args,
				smtlibVersion);
	}

	public static final Set<String> getIDs(
			final List<AbstractSolverConfiguration> solverConfigs) {
		final Set<String> usedIds = new HashSet<String>();
		for (final AbstractSolverConfiguration solverConfig : solverConfigs) {
			usedIds.add(solverConfig.getID());
		}
		return usedIds;
	}

	public final static AbstractSolverConfiguration parse(
			final String solverConfigStr) {
		final String[] columns = solverConfigStr.split(quote(SEPARATOR));
		return new SMTSolverConfiguration(columns[0], columns[1],
				parseSolver(columns[2]), columns[3], columns[4],
				parseVersion(columns[5]), parseBoolean(columns[6]));
	}
}
