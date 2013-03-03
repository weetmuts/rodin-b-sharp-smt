/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.provers;


/**
 * This enum enumerates the solvers.
 * 
 * @author vitor
 * 
 */
public enum SolverKind {
	ALT_ERGO("alt-ergo"), //
	CVC3("cvc3"), //
	CVC4("cvc4"), //
	MATHSAT5("mathsat5"), //
	OPENSMT("opensmt"), //
	VERIT("verit"), //
	Z3("z3"), //
	UNKNOWN;

	private final String solverName;

	private SolverKind(final String name) {
		solverName = name;
	}

	private SolverKind() {
		solverName = "unknown";
	}

	/**
	 * 
	 * @param name
	 *            the name of the solver we want to get, must not be null
	 * @return the solver which name is the given name, or UNKNOWN
	 */
	public static SolverKind parseKind(final String name) {
		for (final SolverKind solver : SolverKind.values()) {
			if (solver.solverName.equals(name)) {
				return solver;
			}
		}
		return UNKNOWN;
	}

	@Override
	public String toString() {
		return solverName;
	}

	public void toString(final StringBuilder sb) {
		sb.append(solverName);
	}
}
