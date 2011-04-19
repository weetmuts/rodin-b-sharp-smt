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

import java.util.Arrays;

/**
 * 
 */
public class SMTPredicateSymbol extends SMTSymbol implements
		Comparable<SMTPredicateSymbol> {

	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). Remind that it
	 * is possible to associate a predicate predicate to the empty sequence
	 * rank, denoting that the predicate is a propositional predicate.
	 */
	final private SMTSortSymbol[] argSorts;

	final private boolean acceptsAnInfiniteNumberOfArgs;

	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final boolean predefined) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		acceptsAnInfiniteNumberOfArgs = false;
	}

	public boolean acceptsAnInfiniteNumberOfArgs() {
		return acceptsAnInfiniteNumberOfArgs;
	}

	public SMTSortSymbol[] getArgSorts() {
		return argSorts;
	}

	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final boolean predefined,
			boolean acceptsAnInfiniteNumberOfArgs) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		this.acceptsAnInfiniteNumberOfArgs = acceptsAnInfiniteNumberOfArgs;
	}

	public boolean isPropositional() {
		return argSorts.length == 0;
	}

	public boolean hasRank(final SMTSortSymbol[] argSorts2) {
		return Arrays.equals(this.argSorts, argSorts2);
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(name);
		for (SMTSortSymbol sort : argSorts) {
			buffer.append(SPACE);
			buffer.append(sort);
		}
		buffer.append(CPAR);
		return buffer.toString();
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

	public boolean equals(final SMTPredicateSymbol symbol) {
		if (this.compareTo(symbol) == 0) {
			return true;
		}
		return false;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SMTPredicateSymbol other = (SMTPredicateSymbol) obj;
		if (!Arrays.equals(argSorts, other.argSorts))
			return false;
		return true;
	}

	public static class SMTEqual extends SMTPredicateSymbol {
		public SMTEqual(final SMTSortSymbol sort[]) {
			super(EQUAL, sort, PREDEFINED);
		}
	}
}
