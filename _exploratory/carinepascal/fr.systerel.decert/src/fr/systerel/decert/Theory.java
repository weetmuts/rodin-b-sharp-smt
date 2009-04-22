/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert;

/**
 * This class enumerates mathematical theories.
 */
public enum Theory {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant LISTS
	 */
	LISTS("lists"),

	/**
	 * Constant ARRAYS
	 */
	ARRAYS("arrays"),

	/**
	 * Constant BASIC_SET
	 */
	BASIC_SET("basic_set"),

	/**
	 * Constant BASIC_RELATION
	 */
	BASIC_RELATION("basic_relation"),

	/**
	 * Constant FULL_SET_THEORY
	 */
	FULL_SET_THEORY("full_set_theory"),

	/**
	 * Constant INTEGER
	 */
	INTEGER("integer"),

	/**
	 * Constant LINEAR_ORDER_INT
	 */
	LINEAR_ORDER_INT("linear_order_int"),

	/**
	 * Constant LINEAR_ARITH
	 */
	LINEAR_ARITH("linear_arith"),

	/**
	 * Constant NONLINEAR_ARITH
	 */
	NONLINEAR_ARITH("nonlinear_arith"),

	/**
	 * Constant FULL_ARITH
	 */
	FULL_ARITH("full_arith"),

	/**
	 * Constant BOOLEAN
	 */
	BOOLEAN("boolean"),
	
	;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The theory name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private Theory(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the theory name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the theory with the specified name.
	 * 
	 * @param name
	 *            the theory name
	 * @return the theory whose name is <tt>name</tt>
	 */
	public final static Theory fromName(final String name) {
		for (Theory t : Theory.values())
			if (t.name.equals(name))
				return t;
		throw new IllegalArgumentException(name);
	}

}
