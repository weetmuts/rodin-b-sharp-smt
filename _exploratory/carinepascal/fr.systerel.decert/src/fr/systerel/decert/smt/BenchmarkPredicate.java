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

import java.util.ArrayList;
import java.util.List;

import fr.systerel.decert.Variable;

/**
 * This class represents a SMT-LIB predicate.
 */
public final class BenchmarkPredicate {

	/** The predicate name. */
	private final String name;

	/** The predicate signature. */
	private final List<Sort> signature;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a predicate of empty arity from a variable.
	 * 
	 * @param variable
	 *            the variable to be used
	 */
	public BenchmarkPredicate(Variable variable) {
		name = variable.getName();
		signature = new ArrayList<Sort>();
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the predicate name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the predicate signature.
	 * 
	 * @return the signature
	 */
	public final List<Sort> getSignature() {
		return signature;
	}
	
	// =========================================================================
	// Other useful methods
	// =========================================================================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BenchmarkPredicate other = (BenchmarkPredicate) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}
}
