/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
 * The SMT sorts.
 */
public class SMTSortSymbol extends SMTSymbol implements
		Comparable<SMTSortSymbol> {
	protected SMTSortSymbol(String symbolName, final boolean predefined) {
		super(symbolName, predefined);
	}

	@Override
	public int compareTo(final SMTSortSymbol symbol) {
		return (name.compareTo(symbol.getName()));
	}

	/**
	 * Tells whether this sort is compatible with the given sort. Two sorts are
	 * compatible if either one is polymorphic or if they are equal.
	 * 
	 * @param other
	 *            the other sort to test for compatibility
	 * @return whether this sort is compatible with the given sort
	 */
	public boolean isCompatibleWith(SMTSortSymbol other) {
		if (other instanceof SMTPolymorphicSortSymbol) {
			return true;
		}
		return this.equals(other);
	}
}
