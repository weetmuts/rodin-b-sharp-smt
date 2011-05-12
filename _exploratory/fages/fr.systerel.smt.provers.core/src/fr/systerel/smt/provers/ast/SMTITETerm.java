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
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.ITE_TERM;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * This class represents an ITE_FORMULA (If Then Else) term in SMT-LIB grammar.
 */
public final class SMTITETerm extends SMTTerm {

	/** The formula. */
	private final SMTFormula formula;

	/** The term to be used if <tt>formula</tt> is <tt>true</tt>. */
	private final SMTTerm tTerm;

	/** The term to be used if <tt>formula</tt> is <tt>false</tt>. */
	private final SMTTerm fTerm;

	/**
	 * Creates a new ITE_FORMULA term.
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

	public SMTFormula getFormula() {
		return formula;
	}

	public SMTTerm gettTerm() {
		return tTerm;
	}

	public SMTTerm getfTerm() {
		return fTerm;
	}

	@Override
	public SMTSortSymbol getSort() {
		return tTerm.getSort();
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(OPAR);
		builder.append(ITE_TERM);
		builder.append(SPACE);
		formula.toString(builder, false);
		builder.append(SPACE);
		tTerm.toString(builder);
		builder.append(SPACE);
		fTerm.toString(builder);
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		return OPAR + ITE_TERM + SPACE + formula + SPACE + tTerm + SPACE
				+ fTerm + CPAR;
	}
}
