/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.smt.ast;

/**
 * This class represents an ITE (If Then Else) term in SMT-LIB grammar.
 */
public final class SMTITETerm extends SMTTerm {
	
	/** The formula. */
	private final SMTFormula formula;
	
	/** The term to be used if <tt>formula</tt> is <tt>true</tt>. */
	private final SMTTerm tTerm;
	
	/** The term to be used if <tt>formula</tt> is <tt>false</tt>. */
	private final SMTTerm fTerm;

	/**
	 * Creates a new ITE term.
	 * 
	 * @param tag
	 *            the tag
	 * @param formula
	 *            the formula to be checked for satisfiability
	 * @param tTerm
	 *            the term to be used if the formula is satisfiable
	 * @param fTerm
	 *            the term to be used if the formula is not satisfiable
	 */
	SMTITETerm(int tag, SMTFormula formula, SMTTerm tTerm, SMTTerm fTerm) {
		super(tag);
		this.formula = formula;
		this.tTerm = tTerm;
		this.fTerm = fTerm;
		assert tag == SMTNode.ITE;
		assert formula != null;
		assert tTerm != null;
		assert fTerm != null;
	}

	@Override
	protected void toString(StringBuilder builder) {
		String sep = " ";
		builder.append('(');
		builder.append("ite");
		builder.append(sep);
		formula.toString(builder);
		builder.append(sep);
		tTerm.toString(builder);
		builder.append(sep);
		fTerm.toString(builder);
		builder.append(')');
	}
}
