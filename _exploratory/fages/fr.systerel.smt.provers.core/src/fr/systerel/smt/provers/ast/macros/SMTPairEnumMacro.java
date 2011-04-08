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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.SMTMacroTerm;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVarSymbol;

public class SMTPairEnumMacro extends SMTMacro {

	SMTPairEnumMacro(String macroName, SMTVarSymbol var1, SMTVarSymbol var2,
			SMTMacroTerm[] terms, int precedence) {
		super(macroName, precedence);
		this.var1 = var1;
		this.var2 = var2;
		this.terms = terms;
	}

	public SMTVarSymbol getVar1() {
		return var1;
	}

	public void setVar1(SMTVarSymbol var1) {
		this.var1 = var1;
	}

	public SMTVarSymbol getVar2() {
		return var2;
	}

	public void setVar2(SMTVarSymbol var2) {
		this.var2 = var2;
	}

	private SMTVarSymbol var1;
	private SMTVarSymbol var2;
	private SMTMacroTerm[] terms;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(OPAR);
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		sb.append(var1);
		sb.append(SPACE);
		sb.append(var2);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append(elemToString(var1.getNameWithQMark(),
					var2.getNameWithQMark(), terms[0].getArgTerms()[0],
					terms[0].getArgTerms()[1]));
			sb.append(CPAR);
			sb.append(CPAR);
		} else {
			sb.append("(or");
			for (SMTMacroTerm term : terms) {
				sb.append("\n\t\t");
				sb.append(elemToString(var1.getNameWithQMark(),
						var2.getNameWithQMark(), term.getArgTerms()[0],
						term.getArgTerms()[1]));
			}
			sb.append("\n");
			sb.append(CPAR);
			sb.append(CPAR);
			sb.append(CPAR);
		}
		return sb.toString();
	}

	private String elemToString(String var1, String var2, SMTTerm term1,
			SMTTerm term2) {
		StringBuffer sb = new StringBuffer();
		sb.append("(= (pair ");
		sb.append(var1);
		sb.append(SPACE);
		sb.append(var2);
		sb.append(CPAR);
		sb.append("(pair ");
		sb.append(term1);
		sb.append(SPACE);
		sb.append(term2);
		sb.append(CPAR);
		sb.append(CPAR);
		return sb.toString();
	}

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

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
