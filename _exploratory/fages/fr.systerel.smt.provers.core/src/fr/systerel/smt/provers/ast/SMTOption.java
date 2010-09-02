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
 * The SMT options.
 */
public enum SMTOption {

	// =========================================================================
	// Constants
	// =========================================================================

	/**
	 * Constant PRODUCE_UNSAT_CORES
	 */
	PRODUCE_UNSAT_CORES("produce-unsat-cores"),
	
	/**
	 * Constant PRODUCE_PROOFS
	 */
	PRODUCE_PROOFS("produce-proofs"),;

	// =========================================================================
	// Variables
	// =========================================================================

	/** The logic name. */
	private final String name;

	// =========================================================================
	// Constructors
	// =========================================================================

	private SMTOption(final String name) {
		this.name = name;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the option name.
	 * 
	 * @return the option
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the option with the specified name.
	 * 
	 * @param name
	 *            the option name
	 * @return the option whose name is <tt>name</tt>
	 */
	public final static SMTOption fromName(final String name) {
		for (SMTOption l : SMTOption.values())
			if (l.name.equals(name))
				return l;
		throw new IllegalArgumentException(name);
	}

}
