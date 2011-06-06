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

import static fr.systerel.smt.provers.ast.Messages.SMTSymbol_NullNameException;

/**
 * This class stores predefined SMT Symbols
 */
public abstract class SMTSymbol {
	protected final String name;
	protected final boolean predefined;

	public static final String INT = "Int"; //$NON-NLS-1$
	public static final String BOOL_SORT = "BOOL"; //$NON-NLS-1$
	public static final String EQUAL = "="; //$NON-NLS-1$
	public static final String NOTEQUAL = "!="; //$NON-NLS-1$
	public static final String LT = "<"; //$NON-NLS-1$
	public static final String LE = "<="; //$NON-NLS-1$
	public static final String GT = ">"; //$NON-NLS-1$
	public static final String GE = ">="; //$NON-NLS-1$
	public static final String UMINUS = "~"; //$NON-NLS-1$
	public static final String MINUS = "-"; //$NON-NLS-1$
	public static final String PLUS = "+"; //$NON-NLS-1$
	public static final String MUL = "*"; //$NON-NLS-1$
	public static final String DISTINCT = "distinct"; //$NON-NLS-1$

	public static final String BENCHMARK = "benchmark"; //$NON-NLS-1$
	public static final String LOGIC = "logic"; //$NON-NLS-1$
	public static final String THEORY = "theory"; //$NON-NLS-1$
	public static final String U_SORT = "U"; //$NON-NLS-1$

	public static final boolean PREDEFINED = true;
	public static final String DIV = "divi"; //$NON-NLS-1$
	public static final String EXPN = "expn"; //$NON-NLS-1$
	public static final String MOD = "mod"; //$NON-NLS-1$

	/**
	 * Constructs a new instance of SMTSymbol
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 */
	protected SMTSymbol(final String symbolName, final boolean predefined) {
		if (symbolName != null) {
			name = symbolName;
			this.predefined = predefined;
		} else {
			throw new IllegalArgumentException(SMTSymbol_NullNameException);
		}
	}

	/**
	 * returns the name of the symbol
	 * 
	 * @return the name of the symbol
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns true if it's predefined, false otherwise.
	 * 
	 * @return true if it's predefined, false otherwise.
	 */
	public boolean isPredefined() {
		return predefined;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Prints into the StringBuilder the string representation of the SMT term.
	 * 
	 * @param builder
	 *            the StringBuilder that will store the string representation of
	 *            the SMTTerm.
	 */
	public abstract void toString(final StringBuilder builder);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SMTSymbol other = (SMTSymbol) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
