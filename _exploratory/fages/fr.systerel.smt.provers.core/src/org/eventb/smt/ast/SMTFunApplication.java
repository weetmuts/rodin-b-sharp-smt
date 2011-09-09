/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;

/**
 * This class represents function application.
 * 
 * @author guyot
 * 
 */
public class SMTFunApplication extends SMTTerm {

	/**
	 * the function symbol of the term.
	 */
	final SMTFunctionSymbol symbol;

	/**
	 * the arguments of the term.
	 */
	final SMTTerm[] args;

	public SMTFunctionSymbol getSymbol() {
		return symbol;
	}

	public SMTTerm[] getArgs() {
		return args;
	}

	/**
	 * Constructs a new function application.
	 * 
	 * @param symbol
	 *            the symbol of the function
	 * @param terms
	 *            the terms of the function
	 */
	public SMTFunApplication(final SMTFunctionSymbol symbol,
			final SMTTerm... terms) {
		verifyFunctionRank(symbol, terms);
		this.symbol = symbol;
		args = terms.clone();
		sort = symbol.getResultSort();
	}

	/**
	 * This method checks if the arguments match in number and sort the expected
	 * arguments.
	 * 
	 * @param symbol
	 *            the function symbol
	 * @param terms
	 *            the terms to check
	 */
	private static void verifyFunctionRank(final SMTFunctionSymbol symbol,
			final SMTTerm terms[]) {
		final SMTSortSymbol[] expectedSortArgs = symbol.getArgSorts();
		final boolean wellSorted;
		if (symbol.isAssociative()) {
			wellSorted = verifyAssociativeRank(expectedSortArgs[0], terms);
		} else {
			wellSorted = verifyNonAssociativeRank(expectedSortArgs, terms);
		}
		if (!wellSorted) {
			throw new IllegalArgumentException(
					incompatibleFunctionRankExceptionMessage(symbol, terms,
							new StringBuilder()));
		}
	}

	/**
	 * this method builds a message to describe the problem when the arguments
	 * are incompatible to the expected arguments of the function symbol.
	 * 
	 * @param functionSymbol
	 *            the function symbol
	 * @param args
	 *            the arguments
	 * @return the exception message
	 */
	private static String incompatibleFunctionRankExceptionMessage(
			final SMTFunctionSymbol functionSymbol, final SMTTerm[] args,
			final StringBuilder sb) {
		sb.append("Arguments of function symbol: ");
		sb.append(functionSymbol);
		sb.append(": ");
		String sep = "";
		for (final SMTSortSymbol expectedArg : functionSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			expectedArg.toString(sb);
		}
		sb.append(" do not match: ");
		for (final SMTTerm arg : args) {
			sb.append(sep);
			sep = " ";
			arg.getSort().toString(sb);
		}
		return sb.toString();
	}

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		if (symbol.isConstant()) {
			builder.append(symbol.getName());
		} else {
			builder.append(OPAR);
			builder.append(symbol.getName());
			for (final SMTTerm arg : args) {
				builder.append(SPACE);
				arg.toString(builder, offset);
			}
			builder.append(CPAR);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}
}
