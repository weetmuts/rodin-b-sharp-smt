/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.symbols;

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;

import java.util.Arrays;

import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This class represents SMT function symbols.
 */
public class SMTFunctionSymbol extends SMTSymbol implements
		Comparable<SMTFunctionSymbol> {

	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). It was chosen
	 * to distinguish between the result sort and the argument sorts by putting
	 * them in two distinct fields. Consequently, argSorts can be null whereas
	 * resultSort cannot.
	 */
	final private SMTSortSymbol[] argSorts;
	final private SMTSortSymbol resultSort;

	final private boolean associative;

	public static final boolean ASSOCIATIVE = true;

	/**
	 * returns true if the function symbol is associative, false otherwise.
	 * 
	 * @return true if the function symbol is associative, false otherwise.
	 */
	public boolean isAssociative() {
		return associative;
	}

	/**
	 * Constructs a new SMT function symbol
	 * 
	 * @param symbolName
	 *            the name of the function
	 * @param argSorts
	 *            the arguments of the function
	 * @param resultSort
	 *            the result sort
	 * @param associative
	 *            determines if the function is associative
	 * @param predefined
	 *            determines if the function is predefined
	 */
	public SMTFunctionSymbol(final String symbolName,
			final SMTSortSymbol[] argSorts, final SMTSortSymbol resultSort,
			final boolean associative, final boolean predefined,
			final SMTLIBVersion smtlibVersion) {
		super(symbolName, predefined, smtlibVersion);
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

	/**
	 * returns the expected argument sorts.
	 * 
	 * @return the expected argument sorts.
	 */
	public SMTSortSymbol[] getArgSorts() {
		return argSorts;
	}

	/**
	 * returns the result sort.
	 * 
	 * @return the result sort.
	 */
	public SMTSortSymbol getResultSort() {
		return resultSort;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(final StringBuilder buffer) {
		if (smtlibVersion.equals(V1_2)) {
			buffer.append(OPAR);
			buffer.append(name);
			for (final SMTSortSymbol sort : argSorts) {
				buffer.append(SPACE);
				buffer.append(sort);
			}
			buffer.append(SPACE);
			buffer.append(resultSort);
			buffer.append(CPAR);
		} else {
			/**
			 * smtlibVersion.equals(V2_0)
			 */
			String separator = "";
			buffer.append(name);
			buffer.append(SPACE);
			buffer.append(OPAR);
			for (final SMTSortSymbol sort : argSorts) {
				buffer.append(separator);
				buffer.append(sort);
				separator = SPACE;
			}
			buffer.append(CPAR);
			buffer.append(SPACE);
			buffer.append(resultSort);
		}
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
		if (nameComp != 0) {
			return nameComp;
		}

		// code currently not reachable
		if (argSorts.length < symbol.argSorts.length) {
			return -1;
		}
		if (argSorts.length > symbol.argSorts.length) {
			return 1;
		}
		for (int i = 0; i < argSorts.length; i++) {
			final int argComp = argSorts[i].compareTo(symbol.argSorts[i]);
			if (argComp != 0) {
				return argComp;
			}
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(argSorts);
		result = prime * result + (associative ? 1231 : 1237);
		result = prime * result
				+ (resultSort == null ? 0 : resultSort.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SMTFunctionSymbol other = (SMTFunctionSymbol) obj;
		if (compareTo((SMTFunctionSymbol) obj) != 0) {
			return false;
		}
		if (associative != other.associative) {
			return false;
		}
		if (resultSort == null) {
			if (other.resultSort != null) {
				return false;
			}
		} else if (!resultSort.equals(other.resultSort)) {
			return false;
		}
		return true;
	}

}
