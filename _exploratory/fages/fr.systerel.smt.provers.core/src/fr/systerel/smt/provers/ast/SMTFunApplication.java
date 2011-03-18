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
 * @author guyot
 * 
 */
public class SMTFunApplication extends SMTTerm {
	final SMTFunctionSymbol symbol;
	final SMTTerm[] args;

	public SMTFunApplication(final SMTFunctionSymbol symbol,
			final SMTTerm terms[]) {
		verifyFunctionRank(symbol, terms);
		this.symbol = symbol;
		this.args = terms.clone();
		this.sort = symbol.getResultSort();
	}

	private static void verifyFunctionRank(final SMTFunctionSymbol symbol,
			final SMTTerm terms[]) {
		SMTSortSymbol[] expectedSortArgs = symbol.getArgSorts();

		// Verify if it's associative. If so, verify if all the arguments are of
		// the same type
		if (symbol.isAssociative()) {

			for (SMTTerm term : terms) {
				if (!term.getSort().equals(expectedSortArgs[0])) {
					throw incompatibleFunctionRankException(symbol, terms);
				}
			}
			return;
		}

		// Verify if the number of arguments expected match the number of terms
		if (expectedSortArgs.length == terms.length) {

			// Verify if the sort symbols are the same
			for (int i = 0; i < terms.length; i++) {
				SMTSortSymbol argSort = terms[i].getSort();

				if (!expectedSortArgs[i].equals(argSort)) {
					throw incompatibleFunctionRankException(symbol, terms);
				}
			}
			return;
		}
		throw incompatibleFunctionRankException(symbol, terms);
	}

	private static IllegalArgumentException incompatibleFunctionRankException(
			final SMTFunctionSymbol expectedSymbol, final SMTTerm[] args) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Arguments of function symbol: ");
		sb.append(expectedSymbol);
		sb.append(": ");
		String sep = "";
		for (SMTSortSymbol expectedArg : expectedSymbol.getArgSorts()) {
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
		return new IllegalArgumentException(sb.toString());
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
