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
 * This class represents an SMTAtom
 */
class SMTAtom extends SMTFormula {
	final SMTPredicateSymbol predicate;
	final SMTTerm[] terms;

	/**
	 * 
	 * @param symbol
	 * @param terms
	 */
	SMTAtom(final SMTPredicateSymbol symbol, final SMTTerm terms[]) {
		verifyPredicateRank(symbol, terms);
		this.predicate = symbol;
		this.terms = terms.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		if (predicate.isPropositional()) {
			builder.append(predicate.name);
		} else {
			builder.append(OPAR);
			builder.append(predicate.name);
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}

	/**
	 * This method verifies if the real arguments match in sort, order and
	 * number the expected arguments.
	 * 
	 * @param symbol
	 *            The operator
	 * @param terms
	 *            The terms to be checked
	 */
	private static void verifyPredicateRank(final SMTPredicateSymbol symbol,
			final SMTTerm terms[]) {
		SMTSortSymbol[] expectedSortArgs = symbol.getArgSorts();

		if (symbol.acceptsAnInfiniteNumberOfArgs()) {
			SMTSortSymbol expectedSort = expectedSortArgs[0];
			if (expectedSort instanceof SMTPolymorphicSortSymbol) {
				return;
			} else {
				for (SMTTerm term : terms) {
					if (!term.getSort().equals(expectedSort)) {
						throw incompatiblePredicateRankException(symbol, terms);
					}
				}
				return;
			}
		}
		// Check if the number of arguments expected match the number of terms
		else if (expectedSortArgs.length == terms.length) {

			// Check if the sort symbols are the same
			for (int i = 0; i < terms.length; i++) {
				SMTSortSymbol argSort = terms[i].getSort();
				SMTSortSymbol expectedSortArg = expectedSortArgs[i];

				// If one of the expected sorts are polymorphic, ignore it
				if (expectedSortArg instanceof SMTPolymorphicSortSymbol) {
					continue;
				}
				if (argSort instanceof SMTPolymorphicSortSymbol) {
					continue;
				}

				if (!expectedSortArg.equals(argSort)) {
					throw incompatiblePredicateRankException(symbol, terms);
				}
			}
			return;
		}
		throw incompatiblePredicateRankException(symbol, terms);
	}

	/**
	 * Constructs a string message that explains the error if the rank of the
	 * symbol is not compatible with its arguments, and returns an
	 * {@link IllegalArgumentException} with thee message.
	 * 
	 * @param expectedSymbol
	 *            the symbol
	 * @param args
	 *            the arguments
	 * @return an {@link IllegalArgumentException} with the detailed message.
	 * 
	 * @see #verifyPredicateRank(SMTPredicateSymbol, SMTTerm[])
	 */
	protected static IllegalArgumentException incompatiblePredicateRankException(
			final SMTPredicateSymbol expectedSymbol, final SMTTerm[] args) {
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
}
