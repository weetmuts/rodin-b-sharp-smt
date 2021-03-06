/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.symbols;

/**
 * This class stores predefined SMT Symbols
 */
public abstract class SMTSymbol {

	protected final String name;
	protected final boolean predefined;

	public static final String INT = "Int";
	public static final String INTS = "INTS";
	public static final String BOOL = "BOOL";
	public static final String BOOL_V2 = "Bool";
	public static final String BOOLS = "BOOLS";
	public static final String EQUAL = "=";
	public static final String NOTEQUAL = "!=";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String UMINUS = "~";
	public static final String MINUS = "-";
	public static final String PLUS = "+";
	public static final String MUL = "*";
	public static final String DISTINCT = "distinct";

	public static final String BENCHMARK = "benchmark";
	public static final String LOGIC = "logic";
	public static final String THEORY = "theory";
	public static final String U_SORT = "U";

	public static final boolean PREDEFINED = true;
	public static final String DIV = "divi";
	public static final String EXPN = "expn";
	public static final String MOD = "mod";

	/**
	 * Constructs a new instance of SMTSymbol
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 */
	protected SMTSymbol(final String symbolName, final boolean predefined) {
		assert symbolName != null;
		this.name = symbolName;
		this.predefined = predefined;
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
