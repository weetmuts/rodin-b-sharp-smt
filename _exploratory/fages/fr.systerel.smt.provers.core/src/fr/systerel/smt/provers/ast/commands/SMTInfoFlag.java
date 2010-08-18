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
package fr.systerel.smt.provers.ast.commands;

/**
 * The information flag of an SMT script command.
 */
public enum SMTInfoFlag {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant ERROR_BEHAVIOR
	 */
	ERROR_BEHAVIOR("error-behavior"),

	/**
	 * Constant NAME
	 */
	NAME("name"),

	/**
	 * Constant AUTHORS
	 */
	AUTHORS("authors"),
	
	/**
	 * Constant VERSION
	 */
	VERSION("version"),
	
	/**
	 * Constant STATUS
	 */
	STATUS("status"),
	
	/**
	 * Constant REASON_UNKNOWN
	 */
	REASON_UNKNOWN("reason-unknown"),
	
	/**
	 * Constant ALL_STATISTICS
	 */
	ALL_STATISTICS("all-statistics"),

	;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The flag name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private SMTInfoFlag(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the flag name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the flag with the specified name.
	 * 
	 * @param name
	 *            the flag name
	 * @return the flag whose name is <tt>name</tt>
	 */
	public final static SMTInfoFlag fromName(final String name) {
		for (SMTInfoFlag t : SMTInfoFlag.values())
			if (t.name.equals(name))
				return t;
		throw new IllegalArgumentException(name);
	}

}
