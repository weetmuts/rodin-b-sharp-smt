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
import static org.eventb.smt.ast.SMTFactory.POINT;
import static org.eventb.smt.ast.SMTFactory.SPACE;

import org.eventb.smt.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.ast.symbols.SMTVarSymbol;
import org.eventb.smt.translation.SMTLIBVersion;

/**
 * This class is used to represent formulas which the operator is {@code FORALL}
 * or {@code EXISTS}.
 */
public class SMTQuantifiedFormula extends SMTFormula {
	private final SMTLIBVersion smtlibVersion;

	private final SMTQuantifierSymbol quantifier;
	private final SMTVarSymbol[] qVars;
	private final SMTFormula formula;

	/**
	 * Constructs a new SMT quantified formula
	 * 
	 * @param quantifier
	 *            the quantifier symbol (FORALL or EXISTS)
	 * @param qVars
	 *            the bound vars of the formula
	 * @param formula
	 *            the bound formula
	 */
	SMTQuantifiedFormula(final SMTQuantifierSymbol quantifier,
			final SMTVarSymbol[] qVars, final SMTFormula formula,
			final SMTLIBVersion smtlibVersion) {
		this.smtlibVersion = smtlibVersion;
		this.quantifier = quantifier;
		this.qVars = qVars.clone();
		this.formula = formula;
	}

	public SMTFormula getFormula() {
		return formula;
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
		builder.append(quantifier);

		switch (smtlibVersion) {
		case V1_2:
			for (final SMTVarSymbol qVar : qVars) {
				builder.append(SPACE);
				qVar.toString(builder);
			}
			if (printPoint) {
				builder.append(SPACE);
				builder.append(POINT);
				builder.append(SPACE);
			}	
			break;

		default:
			String separator = "";

			builder.append(SPACE);
			builder.append(OPAR);
			for (final SMTVarSymbol qVar : qVars) {
				builder.append(separator);
				qVar.toString(builder);
				separator = SPACE;
			}
			builder.append(CPAR);
			break;
		}

		builder.append(SPACE);
		builder.append(newLine);
		SMTNode.indent(builder, newOffset);
		if (formula instanceof SMTQuantifiedFormula && printPoint) {
			((SMTQuantifiedFormula) formula).toString(builder, newOffset, true);
		} else {
			formula.toString(builder, newOffset, printPoint);
		}
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		this.toString(sb, -1, false);
		return sb.toString();
	}
}
