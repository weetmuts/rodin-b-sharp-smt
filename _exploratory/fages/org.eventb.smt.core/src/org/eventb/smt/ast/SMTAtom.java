/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
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

import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;

/**
 * This class represents an atom of the SMT-LIB language (v1.2)
 */
class SMTAtom extends SMTFormula {

	/**
	 * The predicate symbol of the atom
	 */
	final SMTPredicateSymbol predicateSymbol;

	/**
	 * The argument terms of the predicate symbol
	 */
	final SMTTerm[] terms;

	/**
	 * Creates a new SMTAtom. If the given terms sort, number and order don't
	 * match the one expected by the predicate symbol, throws an exception.
	 * 
	 * @param symbol
	 *            the predicate symbol of the new atom
	 * @param terms
	 *            the argument terms of the predicate
	 */
	SMTAtom(final SMTPredicateSymbol symbol, final SMTTerm[] terms) {
		checkPredicateRank(symbol, terms);
		predicateSymbol = symbol;
		this.terms = terms.clone();
	}

	/**
	 * Gets the predicate symbol of the atom
	 * 
	 * @return the predicate symbol of the atom
	 */
	public SMTPredicateSymbol getPredicate() {
		return predicateSymbol;
	}

	/**
	 * Gets the terms of the atom
	 * 
	 * @return the terms of the atom
	 */
	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * Checks if the arguments sort, order and number match the expected.
	 * 
	 * @param symbol
	 *            The operator
	 * @param terms
	 *            The terms to be checked
	 */
	private static void checkPredicateRank(final SMTPredicateSymbol symbol,
			final SMTTerm terms[]) {
		final SMTSortSymbol[] expectedSortArgs = symbol.getArgSorts();
		final boolean wellSorted;
		if (symbol.isAssociative()) {
			wellSorted = checkAssociativeRank(expectedSortArgs[0], terms);
		} else {
			wellSorted = checkNonAssociativeRank(expectedSortArgs, terms);
		}
		if (!wellSorted) {
			throw new IllegalArgumentException(
					incompatiblePredicateRankException(symbol, terms,
							new StringBuilder()));
		}
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
	 * @see #checkPredicateRank(SMTPredicateSymbol, SMTTerm[])
	 */
	protected static String incompatiblePredicateRankException(
			final SMTPredicateSymbol expectedSymbol, final SMTTerm[] args,
			final StringBuilder sb) {
		sb.append("Arguments of function symbol: ");
		sb.append(expectedSymbol);
		sb.append(": ");
		String sep = "";
		for (final SMTSortSymbol expectedArg : expectedSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			expectedArg.toString(sb);
		}
		sb.append(" does not match: ");
		for (final SMTTerm arg : args) {
			sb.append(sep);
			sep = " ";
			arg.getSort().toString(sb);
		}
		return sb.toString();
	}

	@Override
	public void toString(final StringBuilder builder, final int offset,
			final boolean printPoint) {
		if (predicateSymbol.isPropositional()) {
			builder.append(predicateSymbol.getName());
		} else {
			builder.append(OPAR);
			builder.append(predicateSymbol.getName());
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder, offset);
			}
			builder.append(CPAR);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1, false);
		return builder.toString();
	}
}
