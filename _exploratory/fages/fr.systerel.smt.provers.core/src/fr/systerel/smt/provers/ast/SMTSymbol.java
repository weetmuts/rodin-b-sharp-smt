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

/**
 *
 */
public abstract class SMTSymbol {
	protected final String name;
	protected final boolean predefined;

	public static final String INT = "Int";
	public static final String BOOL_SORT = "Bool";
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
	public static final String DIV = "/";
	public static final String DIV_Z3 = "div";

	SMTSymbol(final String symbolName, final boolean predefined) {
		this.name = symbolName;
		this.predefined = predefined;
	}

	public String getName() {
		return name;
	}

	public boolean isPredefined() {
		return predefined;
	}

	@Override
	public String toString() {
		return name;
	}

	public void toString(final StringBuilder buffer) {
		buffer.append(toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SMTSymbol other = (SMTSymbol) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
