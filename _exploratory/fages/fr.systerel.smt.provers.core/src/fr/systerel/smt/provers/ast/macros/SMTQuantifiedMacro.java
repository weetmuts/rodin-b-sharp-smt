/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.macros;

import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTQuantifiedFormula;
import fr.systerel.smt.provers.ast.SMTVarSymbol;

public class SMTQuantifiedMacro extends SMTMacro {

	/**
	 * the ?u variable defined in {@link SMTQuantifiedMacro}
	 */
	final SMTVarSymbol lambdaVar;

	/**
	 * The ?x1 ... ?xn variables defined in {@link SMTQuantifiedMacro}
	 */
	final SMTVarSymbol[] qVars;

	/**
	 * The smtP formula defined in {@link SMTQuantifiedMacro}
	 */
	final SMTFormula formula;

	/**
	 * Initializes the class with the necessary parameters to create the set
	 * comprehension macro.
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param quantifiedVariables
	 *            The boundVariables of the set comprehension macro
	 * @param lambdaVar
	 *            the ?u var defined in {@link SMTQuantifiedMacro}
	 * @param formula
	 *            The <code>smtP</code> formula defined in
	 *            {@link SMTQuantifiedMacro}
	 * @param precedence
	 *            The precedence of the macro. See {@link SMTMacro} for more
	 *            details.
	 */
	SMTQuantifiedMacro(final String macroName,
			final SMTVarSymbol[] quantifiedVariables,
			final SMTVarSymbol lambdaVar, final SMTFormula formula,
			final int precedence) {
		super(macroName, precedence);
		qVars = quantifiedVariables;
		this.lambdaVar = lambdaVar;
		this.formula = formula;
	}

	public SMTFormula getFormula() {
		return formula;
	}

	/**
	 * Retrieve the lambda variable.
	 * 
	 * @return the lambda variable
	 */
	public SMTVarSymbol getLambdaVar() {
		return lambdaVar;
	}

	/**
	 * get the bound identifiers
	 * 
	 * @return the bound identifiers
	 */
	public SMTVarSymbol[] getqVars() {
		return qVars;
	}

	@Override
	public void toString(final StringBuilder sb) {
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append("(lambda");
		lambdaVar.toString(sb);
		sb.append(" . ");
		sb.append("(exists ");
		for (final SMTVarSymbol qVar : qVars) {
			qVar.toString(sb);
		}
		sb.append(" . ");
		if (formula instanceof SMTQuantifiedFormula) {
			((SMTQuantifiedFormula) formula).toString(sb, true);
		} else {
			formula.toString(sb, true);
		}
		sb.append(")))");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}
}
