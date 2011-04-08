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
package fr.systerel.smt.provers.ast;

public class SMTSetComprehensionMacro extends SMTMacro {

	SMTSetComprehensionMacro(String macroName,
			SMTVarSymbol[] quantifiedVariables, SMTVarSymbol lambdaVar,
			SMTFormula formula, SMTTerm expression, int precedence) {
		super(macroName, precedence);
		this.qVars = quantifiedVariables;
		this.lambdaVar = lambdaVar;
		this.formula = formula;
		this.expression = expression;
	}

	public SMTVarSymbol getLambdaVar() {
		return lambdaVar;
	}

	public void setLambdaVar(SMTVarSymbol lambdaVar) {
		this.lambdaVar = lambdaVar;
	}

	public SMTVarSymbol[] getqVars() {
		return qVars;
	}

	public void setqVars(SMTVarSymbol[] qVars) {
		this.qVars = qVars;
	}

	SMTVarSymbol lambdaVar;
	SMTVarSymbol[] qVars;
	SMTFormula formula;
	SMTTerm expression;

	@Override
	public void toString(StringBuffer builder) {
		// TODO
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append("(lambda");
		sb.append(lambdaVar);
		sb.append(" . ");
		sb.append("(exists ");
		for (SMTVarSymbol qVar : qVars) {
			sb.append(qVar);
		}
		sb.append(". (and (= ");
		sb.append("?" + lambdaVar.name);
		sb.append(" ");
		sb.append(expression);
		sb.append(") ");
		sb.append(formula);
		sb.append("))))");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO
		return true;
	}

	@Override
	protected void extractQSymbols() {
		// TODO Auto-generated method stub

	}

}
