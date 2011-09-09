/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast.symbols;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.QVAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

/**
 * Represents quantified var symbols (constants are represented with
 * SMTFunctionSymbol)
 * 
 */
public class SMTVarSymbol extends SMTSymbol implements Comparable<SMTVarSymbol> {
	final private SMTSortSymbol sort;

	/**
	 * Constructs a new SMT var symbols.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param sort
	 *            the sort
	 * @param predefined
	 *            yes if it is predefined, otherwise no.
	 */
	public SMTVarSymbol(final String symbolName, final SMTSortSymbol sort,
			final boolean predefined) {
		super(symbolName, predefined);
		this.sort = sort;
	}

	/**
	 * returns the sort of the symbol
	 * 
	 * @return the sort
	 */
	public SMTSortSymbol getSort() {
		return sort;
	}

	/**
	 * appends in the string builder the string representation of the var symbol
	 * with a "?" before. For example: if the name is "a", it returns, "?a"
	 * 
	 * @param sb
	 *            the StringBuilder that will append the var symbol.
	 */
	public void getNameWithQMark(final StringBuilder sb) {
		sb.append(QVAR + name);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(final StringBuilder buffer) {
		buffer.append(OPAR);
		buffer.append(QVAR);
		buffer.append(name);
		buffer.append(SPACE);
		buffer.append(sort);
		buffer.append(CPAR);
	}

	@Override
	public int compareTo(final SMTVarSymbol symbol) {
		return name.compareTo(symbol.getName());
	}
}
