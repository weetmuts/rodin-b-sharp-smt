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
 * Enumeration of the kind of solvers known to this plug-in.
 * 
 * @author vitor
 */
public enum SolverKind {

	ALT_ERGO("alt-ergo"), //
	CVC3("cvc3"), //
	CVC4("cvc4"), //
	MATHSAT5("mathsat5"), //
	OPENSMT("opensmt"), //
	VERIT("verit"), //
	Z3("z3"), //
	UNKNOWN("unknown");

	private final String name;

	private SolverKind(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
