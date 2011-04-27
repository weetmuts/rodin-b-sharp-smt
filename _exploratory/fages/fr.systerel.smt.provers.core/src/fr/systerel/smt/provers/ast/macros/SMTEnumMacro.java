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
package fr.systerel.smt.provers.ast.macros;

import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVarSymbol;

/**
 * This class is used to create macros that are enumerations. These macros are
 * produced by the rule 19 of the article Integration of SMT-Solvers in B and
 * Event-B Development Environments, from author DEHARBE, David, 2010, which
 * translates sets defined in extension. But this macro is specific for
 * enumerations of single values, not maplets.
 * 
 * @author vitor
 * 
 */
public class SMTEnumMacro extends SMTMacro {

	/**
	 * Constructs a new macro with the name, the assigned variable, the terms of
	 * the enumeration and the precedence.
	 * 
	 * @param macroName
	 *            the name of the macro
	 * @param assignedVar
	 *            The assigned var
	 * @param terms
	 *            The terms that are the elements of the enumeration
	 * @param precedence
	 *            the precedence of the macro. See {@link SMTMacro} for more
	 *            details.
	 */
	SMTEnumMacro(final String macroName, final SMTVarSymbol assignedVar,
			final SMTTerm[] terms, int precedence) {
		super(macroName, precedence);
		this.assignedVar = assignedVar;
		this.terms = terms;
	}

	/**
	 * Retrieves the assigned variable
	 * 
	 * @return the assigned variable
	 */
	public SMTVarSymbol getAssignedVar() {
		return assignedVar;
	}

	/**
	 * The assigned variable of the enumeration macro.
	 */
	private SMTVarSymbol assignedVar;

	/**
	 * They represent the elements of the enumeration.
	 */
	private SMTTerm[] terms;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		assignedVar.toString(sb);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append("(= ");
			sb.append(assignedVar.getNameWithQMark());
			sb.append(" ");
			terms[0].toString(sb);
			sb.append(")))");
		} else {
			sb.append("(or");
			for (SMTTerm term : terms) {
				sb.append("\n\t\t(= ");
				sb.append(assignedVar.getNameWithQMark());
				sb.append(" ");
				term.toString(sb);
				sb.append(")");
			}
			sb.append("\n )))");
		}
	}
}
