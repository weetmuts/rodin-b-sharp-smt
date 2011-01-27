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
public class SMTFunctionSymbol extends SMTSymbol {
	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). It was chosen
	 * to distinguish between the result sort and the argument sorts by putting
	 * them in two distinct fields. Consequently, argSorts can be null whereas
	 * resultSort cannot.
	 */
	final private SMTSortSymbol[] argSorts;
	final private SMTSortSymbol resultSort;
	// TODO use this field when creating a new FunApplication object, and adapt
	// the way the rank is verified
	@SuppressWarnings("unused")
	final private boolean associative;

	public static final boolean ASSOCIATIVE = true;

	SMTFunctionSymbol(final String symbolName, final SMTSortSymbol argSorts[],
			final SMTSortSymbol resultSort, final boolean associative,
			final boolean predefined) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		// Must not be null
		this.resultSort = resultSort;
		this.associative = associative;
	}

	/**
	 * If the argSorts tab is empty, then this symbol is a base term: function
	 * constant.
	 */
	public boolean isConstant() {
		return argSorts.length == 0;
	}

	public SMTSortSymbol getResultSort() {
		return this.resultSort;
	}

	public boolean hasRank(final SMTSortSymbol[] argSorts2,
			final SMTSortSymbol resultSort2) {
		return Arrays.equals(this.argSorts, argSorts2)
				&& this.resultSort.equals(resultSort2);
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
		buffer.append(SPACE);
		buffer.append(this.resultSort);
		buffer.append(CPAR);
		return buffer.toString();
	}
}
