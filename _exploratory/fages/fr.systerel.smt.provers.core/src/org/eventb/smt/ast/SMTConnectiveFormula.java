/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

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
	public void toString(final StringBuilder builder, final int offset,
			final boolean printPoint) {
		final int newOffset;
		final String newLine;
		if (offset >= 0) {
			newOffset = offset + 4;
			newLine = "\n";
		} else {
			newOffset = offset;
			newLine = "";
		}
		builder.append(OPAR);
		builder.append(connective);
		for (final SMTFormula formula : formulas) {
			builder.append(SPACE);
			builder.append(newLine);
			SMTNode.indent(builder, newOffset);
			formula.toString(builder, newOffset, printPoint);
		}
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1, false);
		return builder.toString();
	}
}
