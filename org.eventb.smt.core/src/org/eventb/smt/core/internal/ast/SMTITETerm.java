/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

/**
 * This class represents an ITE (If Then Else) term in SMT-LIB grammar.
 */
public final class SMTITETerm extends SMTTerm {

	private final String ITE = "ite";

	/** The formula. */
	private final SMTFormula formula;

	/** The term to be used if <tt>formula</tt> is <tt>true</tt>. */
	private final SMTTerm tTerm;

	/** The term to be used if <tt>formula</tt> is <tt>false</tt>. */
	private final SMTTerm fTerm;

	/**
	 * Creates a new ITE term.
	 * 
	 * @param formula
	 *            the formula to be checked for satisfiability
	 * @param tTerm
	 *            the term to be used if the formula is satisfiable
	 * @param fTerm
	 *            the term to be used if the formula is not satisfiable
	 */
	SMTITETerm(final SMTFormula formula, final SMTTerm tTerm,
			final SMTTerm fTerm) {
		this.formula = formula;
		this.tTerm = tTerm;
		this.fTerm = fTerm;
	}

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		final String sep = " ";
		builder.append('(');
		builder.append(ITE);
		builder.append(sep);
		formula.toString(builder, offset, false);
		builder.append(sep);
		tTerm.toString(builder, offset);
		builder.append(sep);
		fTerm.toString(builder, offset);
		builder.append(')');
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}
}
