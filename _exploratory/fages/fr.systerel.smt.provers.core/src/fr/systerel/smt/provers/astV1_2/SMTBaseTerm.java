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
package fr.systerel.smt.provers.astV1_2;

/**
 * This class represents a base term in SMT-LIB grammar.
 */
public abstract class SMTBaseTerm extends SMTTerm {

	/** The identifier. */
	final String identifier;

	/**
	 * Creates a new base term.
	 * 
	 * @param tag
	 *            the tag
	 */
	SMTBaseTerm(String identifier, int tag) {
		super(tag);
		this.identifier = identifier;
		assert identifier != null;	
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append(identifier);
	}
}
