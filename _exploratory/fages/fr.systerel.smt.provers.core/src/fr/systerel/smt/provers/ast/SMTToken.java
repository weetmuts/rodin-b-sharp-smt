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

import fr.systerel.smt.provers.ast.responses.SMTStatus;

/**
 * Common class for SMT-LIB tokens.
 */
public abstract class SMTToken extends SMTNode<SMTToken> {

	// =========================================================================
	// Constants
	// =========================================================================
	/**
	 * <code>NUMERAL</code> is the tag for the numeral tokens.
	 * 
	 * @see SMTNumeral
	 */
	public final static int NUMERAL = FIRST_TOKEN + 0;
	
	/**
	 * <code>IDENTIFIER</code> is the tag for the identifiers.
	 * 
	 * @see SMTIdentifier
	 */
	public final static int IDENTIFIER = FIRST_TOKEN + 1;

	/**
	 * <code>FIRST_BOOLEAN</code> is the tag for the first boolean value.
	 * 
	 * @see SMTBoolean
	 */
	public final static int FIRST_BOOLEAN = FIRST_TOKEN + 2;
	
	/**
	 * <code>FIRST_STATUS</code> is the tag for the first status.
	 * 
	 * @see SMTStatus
	 */
	public final static int FIRST_STATUS = FIRST_TOKEN + 4;
	
	// =========================================================================
	// Variables
	// =========================================================================
	/** The identifier. */
	final String identifier;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Creates a new identifier.
	 * 
	 * @param identifier
	 *            the identifier
	 * @param tag
	 *            the tag
	 */
	protected SMTToken(String identifier, int tag) {
		super(tag);
		if (identifier == null) { //FIXME is this test useful or needed?
			throw new NullPointerException();
		}
		this.identifier = identifier;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append(identifier);
	}

}
