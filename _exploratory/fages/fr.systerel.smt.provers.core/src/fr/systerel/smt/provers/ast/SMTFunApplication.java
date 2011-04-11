/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * This class represents function application.
 * 
 * @author guyot
 * 
 */
public class SMTFunApplication extends SMTTerm {
	final SMTFunctionSymbol symbol;
	final SMTTerm[] args;

	/**
	 * Constructs a new function application.
	 * 
	 * @param symbol
	 *            the symbol of the function
	 * @param terms
	 *            the terms of the function
	 */
	public SMTFunApplication(final SMTFunctionSymbol symbol,
			final SMTTerm terms[]) {
		verifyFunctionRank(symbol, terms);
		this.symbol = symbol;
		this.args = terms.clone();
		this.sort = symbol.getResultSort();
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
		SMTSortSymbol[] expectedSortArgs = symbol.getArgSorts();

		// Verifying if it's associative. If so, verify if all the arguments are
		// of
		// the same type
		if (symbol.isAssociative()) {

			for (SMTTerm term : terms) {
				if (!term.getSort().equals(expectedSortArgs[0])) {
					throw new IllegalArgumentException(
							incompatibleFunctionRankExceptionMessage(symbol,
									terms));
				}
			}
			return;
		}

		// Verify if the number of arguments expected match the number of terms
		if (expectedSortArgs.length == terms.length) {

			// Verify if the sort symbols are the same
			for (int i = 0; i < terms.length; i++) {
				SMTSortSymbol argSort = terms[i].getSort();
				SMTSortSymbol expectedSortArg = expectedSortArgs[i];

				if (argSort instanceof SMTPolymorphicSortSymbol
						|| expectedSortArg instanceof SMTPolymorphicSortSymbol) {
					continue;
				}
				if (expectedSortArg.equals(argSort)) {
					continue;
				} else {
					throw new IllegalArgumentException(
							incompatibleFunctionRankExceptionMessage(symbol,
									terms));
				}
			}
			return;
		}
		throw new IllegalArgumentException(
				incompatibleFunctionRankExceptionMessage(symbol, terms));
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
			final SMTFunctionSymbol functionSymbol, final SMTTerm[] args) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Arguments of function symbol: ");
		sb.append(functionSymbol);
		sb.append(": ");
		String sep = "";
		for (SMTSortSymbol expectedArg : functionSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			expectedArg.toString(sb);
		}
		sb.append(" does not match: ");
		for (SMTTerm arg : args) {
			sb.append(sep);
			sep = " ";
			arg.getSort().toString(sb);
		}
		return sb.toString();
	}

	@Override
	public void toString(StringBuilder builder) {
		if (symbol.isConstant()) {
			builder.append(symbol.name);
		} else {
			builder.append(OPAR);
			builder.append(symbol.name);
			for (SMTTerm arg : args) {
				builder.append(SPACE);
				builder.append(arg);
			}
			builder.append(CPAR);
		}
	}
}
