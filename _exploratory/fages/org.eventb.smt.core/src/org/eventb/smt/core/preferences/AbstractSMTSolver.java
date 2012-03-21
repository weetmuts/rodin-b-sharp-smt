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
import static org.eventb.smt.core.internal.preferences.SMTSolver.SEPARATOR;
import static org.eventb.smt.core.internal.preferences.Utils.decode;
import static org.eventb.smt.core.provers.SolverKind.parseKind;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.smt.core.internal.preferences.SMTSolver;
import org.eventb.smt.core.provers.SolverKind;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractSMTSolver {
	public abstract String getID();

	public abstract String getName();

	public abstract SolverKind getKind();

	public abstract IPath getPath();

	public abstract boolean isEditable();

	public abstract void toString(final StringBuilder builder);

	public static final AbstractSMTSolver newSolver() {
		return new SMTSolver();
	}

	public static final AbstractSMTSolver newSolver(final String id,
			final String name, final SolverKind kind, final IPath path) {
		return new SMTSolver(id, name, kind, path);
	}

	public static final Set<String> getIDs(final List<AbstractSMTSolver> solvers) {
		final Set<String> usedIds = new HashSet<String>();
		for (final AbstractSMTSolver solver : solvers) {
			usedIds.add(solver.getID());
		}
		return usedIds;
	}

	/**
	 * Parses a preference string to build a solver
	 * 
	 * @param solverStr
	 *            the string to parse
	 * @return the solver represented by the string
	 */
	public final static AbstractSMTSolver parseSolver(final String solverStr) {
		final String[] columns = solverStr.split(quote(SEPARATOR));
		return new SMTSolver(decode(columns[0]), decode(columns[1]),
				parseKind(columns[2]), new Path(decode(columns[3])),
				parseBoolean(columns[4]));
	}
}
