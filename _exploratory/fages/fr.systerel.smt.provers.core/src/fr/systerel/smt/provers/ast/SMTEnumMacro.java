/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

public class SMTEnumMacro extends SMTMacro {

	SMTEnumMacro(final String macroName, final SMTVarSymbol varName,
			final SMTTerm[] terms, int precedence) {
		super(macroName, precedence);
		this.var = varName;
		this.terms = terms;
	}

	public SMTVarSymbol getVar() {
		return var;
	}

	public void setVar(SMTVarSymbol var) {
		this.var = var;
	}

	private SMTVarSymbol var;
	private SMTTerm[] terms;

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		sb.append(var);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append("(= ");
			sb.append(var.getNameWithQMark());
			sb.append(" ");
			sb.append(terms[0]);
			sb.append(")))");
		} else {
			sb.append("(or");
			for (SMTTerm term : terms) {
				sb.append("\n\t\t(= ");
				sb.append(var.getNameWithQMark());
				sb.append(" ");
				sb.append(term);
				sb.append(")");
			}
			sb.append("\n )))");
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	protected void extractQSymbols() {
		// TODO Auto-generated method stub

	}
}
