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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.POINT;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

/**
 * This class is used to represent formulas which the operator is {@code FORALL}
 * or {@code EXISTS}.
 */
public class SMTQuantifiedFormula extends SMTFormula {

	private final SMTQuantifierSymbol quantifier;
	private final SMTVarSymbol[] qVars;
	private final SMTFormula formula;

	public SMTQuantifierSymbol getQuantifier() {
		return quantifier;
	}

	public SMTVarSymbol[] getqVars() {
		return qVars;
	}

	public SMTFormula getFormula() {
		return formula;
	}

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
			final SMTVarSymbol[] qVars, final SMTFormula formula) {
		this.quantifier = quantifier;
		this.qVars = qVars.clone();
		this.formula = formula;
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		builder.append(OPAR);
		builder.append(quantifier);
		for (final SMTVarSymbol qVar : qVars) {
			builder.append(SPACE);
			qVar.toString(builder);
		}
		if (printPoint) {
			builder.append(SPACE);
			builder.append(POINT);
			builder.append(SPACE);
		}
		builder.append(SPACE);
		if (formula instanceof SMTQuantifiedFormula && printPoint) {
			((SMTQuantifiedFormula) formula).toString(builder, true);
		} else {
			formula.toString(builder, printPoint);
		}
		builder.append(CPAR);
	}
}
