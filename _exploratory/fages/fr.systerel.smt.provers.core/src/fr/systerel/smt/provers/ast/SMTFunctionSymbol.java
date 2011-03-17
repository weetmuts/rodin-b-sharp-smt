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
public class SMTFunctionSymbol extends SMTSymbol implements
		Comparable<SMTFunctionSymbol> {
	public boolean isAssociative() {
		return associative;
	}

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
	final private boolean associative;

	public static final boolean ASSOCIATIVE = true;

	public SMTFunctionSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final SMTSortSymbol resultSort,
			final boolean associative, final boolean predefined) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		// Must not be null
		this.resultSort = resultSort;
		this.associative = associative;
	}

	public SMTFunctionSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final SMTSortSymbol resultSort,
			final boolean associative, final boolean predefined, boolean isN_ARY) {
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

	public SMTSortSymbol[] getArgSorts() {
		return argSorts;
	}

	public SMTSortSymbol getResultSort() {
		return resultSort;
	}

	public boolean hasRank(final SMTSortSymbol[] argSorts2,
			final SMTSortSymbol resultSort2) {
		return Arrays.equals(argSorts, argSorts2)
				&& resultSort.equals(resultSort2);
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
		buffer.append(SPACE);
		buffer.append(resultSort);
		buffer.append(CPAR);
		return buffer.toString();
	}

	/**
	 * This method compares the current function symbol with the given function
	 * symbol. Its aim is to order them. If they've got the same name, they must
	 * be ordered in respect with their ranks. Remind that two functions can't
	 * have two different result sorts if their argument sorts are all the same.
	 */
	@Override
	public int compareTo(final SMTFunctionSymbol symbol) {
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
}
