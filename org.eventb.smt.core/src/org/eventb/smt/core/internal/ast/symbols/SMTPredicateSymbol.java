/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.symbols;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import java.util.Arrays;

import org.eventb.smt.core.SMTLIBVersion;

/**
 * This class represents SMT Predicate Symbols. *
 */
public class SMTPredicateSymbol extends SMTSymbol implements
		Comparable<SMTPredicateSymbol> {

	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). Remind that it
	 * is possible to associate a predicate to the empty sequence rank, denoting
	 * that the predicate is a propositional predicate.
	 */
	private final SMTSortSymbol[] argSorts;

	/**
	 * True if the predicate is associative, false otherwise.
	 */
	private final boolean isAssociative;

	/**
	 * This constant is used to name membership predicates.
	 */
	public final static String MS_PREDICATE_NAME = "MS";

	/**
	 * Constructs a new predicate symbol.
	 * 
	 * @param symbolName
	 *            The name of the symbol
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 * @param argSorts
	 *            the expected sorts of the arguments.
	 */
	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol[] argSorts, final boolean predefined,
			final SMTLIBVersion smtlibVersion) {
		super(symbolName, predefined, smtlibVersion);
		this.argSorts = argSorts.clone();
		isAssociative = false;
	}

	/**
	 * return true if it's associative, false otherwise.
	 * 
	 * @return true if it's associative, false otherwise.
	 */
	public boolean isAssociative() {
		return isAssociative;
	}

	/**
	 * return a list with the expected sorts of the arguments.
	 * 
	 * @return a list with the expected sorts of the arguments.
	 */
	public SMTSortSymbol[] getArgSorts() {
		return argSorts;
	}

	/**
	 * Constructs a new predicate symbol.
	 * 
	 * @param symbolName
	 *            The name of the symbol
	 * @param argSorts
	 *            the expected sorts of the arguments.
	 * @param isAssociative
	 *            true if the predicate is associative, false otherwise
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 */
	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final boolean isAssociative,
			final boolean predefined, final SMTLIBVersion smtlibVersion) {
		super(symbolName, predefined, smtlibVersion);
		this.argSorts = argSorts.clone();
		this.isAssociative = isAssociative;
	}

	/**
	 * returns true if the predicate symbol is propositional, false otherwise.
	 * 
	 * @return true if the predicate symbol is propositional, false otherwise.
	 */
	public boolean isPropositional() {
		return argSorts.length == 0;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(final StringBuilder builder) {
		if (smtlibVersion == V1_2) {
			builder.append(OPAR);
			builder.append(name);
			for (final SMTSortSymbol sort : argSorts) {
				builder.append(SPACE);
				builder.append(sort);
			}
			builder.append(CPAR);
		} else {
			/**
			 * smtlibVersion == V2_0
			 */
			String separator = "";
			builder.append(name);
			builder.append(SPACE);
			builder.append(OPAR);
			for (final SMTSortSymbol sort : argSorts) {
				builder.append(separator);
				builder.append(sort);
				separator = SPACE;
			}
			builder.append(CPAR);
			builder.append(SPACE);
			builder.append(BOOL_V2);
		}
	}

	@Override
	public int compareTo(final SMTPredicateSymbol symbol) {
		final int nameComp = name.compareTo(symbol.getName());
		if (nameComp == 0) {
			if (argSorts.length < symbol.argSorts.length) {
				return -1;
			} else if (argSorts.length > symbol.argSorts.length) {
				return 1;
			} else {
				for (int i = 0; i < argSorts.length; i++) {
					final int argComp = argSorts[i]
							.compareTo(symbol.argSorts[i]);
					if (argComp != 0) {
						return argComp;
					}
				}
				return 0;
			}
		} else {
			return nameComp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(argSorts);
		return result;
	}

	/**
	 * This class represents the predicate symbol equals (=)
	 */
	public static class SMTEqual extends SMTPredicateSymbol {
		public SMTEqual(final SMTSortSymbol[] sort,
				final SMTLIBVersion smtlibVersion) {
			super(EQUAL, sort, PREDEFINED, smtlibVersion);
		}
	}
}
