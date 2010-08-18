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
 * Common class for SMT-LIB terms.
 */
public abstract class SMTTerm extends SMTNode<SMTTerm> {
	// =========================================================================
	// Constants
	// =========================================================================
	/**
	 * First tag for an arithmetic term.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int FIRST_ARITHMETIC_TERM = FIRST_TERM + 0;

	// =========================================================================
	// Constructor
	// =========================================================================
	/**
	 * Creates a new term with the specified tag.
	 * 
	 * @param tag node tag of this term
	 */
	SMTTerm(int tag) {
		super(tag);
	}

}
