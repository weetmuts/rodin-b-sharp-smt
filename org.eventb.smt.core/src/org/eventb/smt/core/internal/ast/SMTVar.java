/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * This class handles terms created from Bound Identifier Declarations in
 * Event-B
 * 
 * @author Yoann Guyot
 **/
public class SMTVar extends SMTTerm {
	final SMTVarSymbol symbol;

	/**
	 * The constructor.
	 * 
	 * @param symbol
	 *            the symbol of the constructor
	 */
	public SMTVar(final SMTVarSymbol symbol) {
		this.symbol = symbol;
	}

	/**
	 * gets the symbol of the term
	 * 
	 * @return the symbol of the term
	 */
	public SMTVarSymbol getSymbol() {
		return symbol;
	}

	@Override
	public SMTSortSymbol getSort() {
		return symbol.getSort();
	}

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		builder.append(symbol.getName());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}
}
