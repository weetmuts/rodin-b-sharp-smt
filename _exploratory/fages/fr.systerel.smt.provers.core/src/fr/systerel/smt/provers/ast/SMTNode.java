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

import fr.systerel.smt.provers.ast.commands.SMTCommand;
import fr.systerel.smt.provers.ast.commands.SMTCommandOption;

/**
 * This is the base class for all nodes of an SMT-LIB AST (Abstract Syntax
 * Tree).
 */
public abstract class SMTNode<T extends SMTNode<T>> {
	// =========================================================================
	// Constants
	// =========================================================================
	/**
	 * <code>NO_TAG</code> is used to indicate that a tag value is invalid or
	 * absent. It is different from all valid tags.
	 */
	public final static int NO_TAG = 0;
	
	/**
	 * First tag for a script command.
	 * 
	 * @see SMTCommand
	 */
	public final static int FIRST_COMMAND = 101;
	
	/**
	 * First tag for a script response.
	 * 
	 * @see SMTResponse
	 */
	public final static int FIRST_RESPONSE = 201;
	
	/**
	 * First tag for a script command option.
	 * 
	 * @see SMTCommandOption
	 */
	public final static int FIRST_COMMAND_OPTION = 301;
	
	/**
	 * First tag for a token.
	 * 
	 * @see SMTToken
	 */
	public final static int FIRST_TOKEN = 401;
	
	/**
	 * First tag for a term.
	 * 
	 * @see SMTTerm
	 */
	public final static int FIRST_TERM = 501;

	// =========================================================================
	// Variables
	// =========================================================================
	/** The tag for this AST node. */
	private final int tag;

	// =========================================================================
	// Constructor
	// =========================================================================
	/**
	 * Creates a new AST node.
	 * 
	 * @param tag
	 *            the tag
	 */
	protected SMTNode(int tag) {
		this.tag = tag;
	}

	// =========================================================================
	// Getters
	// =========================================================================
	/**
	 * Returns the tag of this AST node.
	 * <p>
	 * Each node has an attached tag that represents the operator associated to
	 * it.
	 * 
	 * @return the tag
	 */
	public final int getTag() {
		return tag;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	/**
	 * Builds the string representation of the SMT noode.
	 * <p>
	 * 
	 * @param builder
	 *            the string builder containing the result
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract void toString(StringBuilder builder);
}
