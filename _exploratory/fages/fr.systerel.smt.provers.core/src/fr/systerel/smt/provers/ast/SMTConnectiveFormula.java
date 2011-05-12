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
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * Common class for SMT-LIB formulas built from connectives.
 */
public class SMTConnectiveFormula extends SMTFormula {
	private final SMTConnective connective;
	private final SMTFormula[] formulas;

	/**
	 * Creates a new connective formula with the specified connective.
	 */
	SMTConnectiveFormula(final SMTConnective connective,
			final SMTFormula... formulas) {
		this.connective = connective;
		this.formulas = formulas.clone();
	}

	/**
	 * Returns the children of this node.
	 * 
	 * @return a list of children
	 */
	public SMTFormula[] getFormulas() {
		return formulas.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		builder.append(OPAR);
		builder.append(connective);
		for (final SMTFormula formula : formulas) {
			builder.append(SPACE);
			formula.toString(builder, printPoint);
		}
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		String s = OPAR + connective;
		for (final SMTFormula formula : formulas) {
			s += SPACE + formula;
		}
		s += CPAR;
		return s;
	}

}
