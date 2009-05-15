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
 * The SMT-LIB sorts.
 */
public enum Sort {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant INT
	 */
	INT("Int"),

	/**
	 * Constant BOOL
	 */
	BOOL("Bool"),
	
	;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The sort name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private Sort(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the sort name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the sort with the specified name.
	 * 
	 * @param name
	 *            the sort name
	 * @return the sort whose name is <tt>name</tt>
	 */
	public final static Sort fromName(final String name) {
		for (Sort t : Sort.values())
			if (t.name.equals(name))
				return t;
		throw new IllegalArgumentException(name);
	}

}
