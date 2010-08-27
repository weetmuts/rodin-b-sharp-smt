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
package fr.systerel.smt.provers.astV1_2;

/**
 * The SMT logics.
 */
public enum SMTLogic {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant QF_LIA
	 */
	QF_LIA("QF_LIA"), ;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The logic name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private SMTLogic(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the logic name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the logic with the specified name.
	 * 
	 * @param name
	 *            the logic name
	 * @return the logic whose name is <tt>name</tt>
	 */
	public final static SMTLogic fromName(final String name) {
		for (SMTLogic l : SMTLogic.values())
			if (l.name.equals(name))
				return l;
		throw new IllegalArgumentException(name);
	}

}
