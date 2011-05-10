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
	/**
	 * verify the rank for associative symbol (predicate or function symbol).
	 * That is, given a sort and the arguments, this methods checks if all the
	 * terms has the same sort as the expected sort.
	 * 
	 * @param expectedSortArg
	 *            the expected sort.
	 * @param terms
	 *            the rank checked terms.
	 * @return true if all the terms are of the same sort of the expected sort,
	 *         false otherwise.
	 */
	protected static boolean verifyAssociativeRank(
			final SMTSortSymbol expectedSortArg, final SMTTerm[] terms) {
		for (final SMTTerm term : terms) {
			if (!term.getSort().isCompatibleWith(expectedSortArg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * verify the rank for non-associative symbol (predicate or function
	 * symbol). That is, given the expected sorts and the arguments, it checks
	 * if each sort corresponds with each argument.
	 * 
	 * @param expectedSortArgs
	 *            the sexpected sorts
	 * @param terms
	 *            the arguments
	 * @return true if, for each argument, its sort is the same of the expected
	 *         sort for its parameter index
	 */
	public static boolean verifyNonAssociativeRank(
			final SMTSortSymbol[] expectedSortArgs, final SMTTerm[] terms) {
		if (expectedSortArgs.length != terms.length) {
			return false;
		}
		for (int i = 0; i < terms.length; i++) {
			if (!expectedSortArgs[i].isCompatibleWith(terms[i].getSort())) {
				return false;
			}
		}
		return true;
	}
}
