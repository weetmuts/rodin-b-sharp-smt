/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * The SMT logics.
 */
public class SMTLogic {
	public static String QF_LIA = "QF_LIA";

	/** The logic identifier. */
	private final String name;

	public SMTLogic(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
