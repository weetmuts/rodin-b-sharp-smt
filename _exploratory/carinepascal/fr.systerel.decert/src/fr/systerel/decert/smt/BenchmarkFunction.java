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

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Type;

/**
 * This class represents a SMT-LIB function.
 */
public final class BenchmarkFunction {

	/** The function name. */
	private final String name;

	/** The function signature. */
	private final List<Sort> signature;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a function of arity 1 from a variable.
	 * 
	 * @param name
	 *            the variable name
	 * @param type
	 *            the variable type
	 */
	public BenchmarkFunction(String name, Type type) {
	    this.name = name;
		signature = new ArrayList<Sort>();
		if (type instanceof IntegerType)
			signature.add(Sort.INT);
		else if (type instanceof BooleanType)
			signature.add(Sort.BOOL);
		else
			assert false; // not implemented yet
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the function name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the function signature.
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
		BenchmarkFunction other = (BenchmarkFunction) obj;
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
