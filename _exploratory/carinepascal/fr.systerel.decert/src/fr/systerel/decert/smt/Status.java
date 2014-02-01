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
package fr.systerel.decert.smt;

/**
 * The status of SMT-LIB benchmarks.
 */
public enum Status {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant SAT
	 */
	SAT("sat"),

	/**
	 * Constant UNSAT
	 */
	UNSAT("unsat"),

	/**
	 * Constant UNKNOWN
	 */
	UNKNOWN("unknown"),

	;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The status name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private Status(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the status name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the status with the specified name.
	 * 
	 * @param name
	 *            the status name
	 * @return the status whose name is <tt>name</tt>
	 */
	public final static Status fromName(final String name) {
		for (Status t : Status.values())
			if (t.name.equals(name))
				return t;
		throw new IllegalArgumentException(name);
	}

}
