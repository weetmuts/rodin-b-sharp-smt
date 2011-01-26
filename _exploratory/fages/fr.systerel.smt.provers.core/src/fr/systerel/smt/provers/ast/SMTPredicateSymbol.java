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
 * @author guyot
 * 
 */
public class SMTPredicateSymbol extends SMTSymbol {
	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). Remind that it
	 * is possible to associate a predicate predicate to the empty sequence
	 * rank, denoting that the predicate is a propositional predicate.
	 */
	final private SMTSortSymbol[] argSorts;

	private boolean isAMembershipPredicate = false;

	public SMTPredicateSymbol(final boolean isAMembershipPredicate,
			final String symbolName, final SMTSortSymbol argSorts[], final boolean predefined) {
		super(symbolName, predefined);
		this.isAMembershipPredicate = isAMembershipPredicate;
		this.argSorts = argSorts.clone();
	}

	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final boolean predefined) {
		this(false, symbolName, argSorts, predefined);
	}

	public boolean isPropositional() {
		return argSorts.length == 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(argSorts);
		result = prime * result + (isAMembershipPredicate ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SMTPredicateSymbol other = (SMTPredicateSymbol) obj;
		if (!Arrays.equals(argSorts, other.argSorts))
			return false;
		if (isAMembershipPredicate != other.isAMembershipPredicate)
			return false;
		return true;
	}

	public boolean hasRank(final SMTSortSymbol[] argSorts2) {
		return Arrays.equals(this.argSorts, argSorts2);
	}

	public boolean isAMembershipPredicate() {
		return this.isAMembershipPredicate;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(this.name);
		for (SMTSortSymbol sort : this.argSorts) {
			buffer.append(SPACE);
			buffer.append(sort);
		}
		buffer.append(CPAR);
		return buffer.toString();
	}
}
