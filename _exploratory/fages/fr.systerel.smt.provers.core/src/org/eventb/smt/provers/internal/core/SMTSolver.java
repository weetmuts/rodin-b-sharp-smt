/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

/**
 * This enum enumerates the solvers.
 * 
 * @author vitor
 * 
 */
public enum SMTSolver {
	ALT_ERGO("alt-ergo"), CVC3("cvc3"), VERIT("verit"), Z3("z3");

	private final String solver_name;

	private SMTSolver(final String name) {
		solver_name = name;
	}

	@Override
	public String toString() {
		return solver_name;
	}

	public void toString(final StringBuilder sb) {
		sb.append(solver_name);
	}
}
