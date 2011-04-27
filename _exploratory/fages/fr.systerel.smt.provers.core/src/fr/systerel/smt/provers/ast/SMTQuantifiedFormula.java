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
 * @author guyot
 * 
 */
public class SMTQuantifiedFormula extends SMTFormula {
	private final SMTQuantifierSymbol quantifier;
	private final SMTVarSymbol[] qVars;
	private final SMTFormula formula;

	SMTQuantifiedFormula(final SMTQuantifierSymbol quantifier,
			final SMTVarSymbol[] qVars, final SMTFormula formula) {
		this.quantifier = quantifier;
		this.qVars = qVars.clone();
		this.formula = formula;
	}

	public void toString(StringBuilder builder, boolean printPoint) {
		builder.append(OPAR);
		builder.append(quantifier);
		for (final SMTVarSymbol qVar : qVars) {
			builder.append(SPACE);
			builder.append(qVar);
		}
		if (printPoint) {
			builder.append(SPACE);
			builder.append(POINT);
			builder.append(SPACE);
		}
		builder.append(SPACE);
		if (formula instanceof SMTQuantifiedFormula) {
			((SMTQuantifiedFormula) formula).toString(builder, true);
		} else {
			builder.append(formula);
		}
		builder.append(CPAR);
	}

	@Override
	public void toString(StringBuilder builder) {
		toString(builder, false);
	}
}
