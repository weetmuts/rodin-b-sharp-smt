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
package fr.systerel.smt.provers.ast;

/**
 * This is the base class for all nodes of an SMT-LIB AST (Abstract Syntax
 * Tree).
 */
public abstract class SMTNode<T extends SMTNode<T>> {

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	/**
	 * Builds the string representation of the SMT node.
	 */
	public abstract void toString(StringBuilder builder);
}
