/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.macros;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;

import org.eventb.smt.core.SMTLIBVersion;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

/**
 * This class is used to create macros that are enumerations. These macros are
 * produced by the rule 19 of the article Integration of SMT-Solvers in B and
 * Event-B Development Environments, from author DEHARBE, David, 2010, which
 * translates sets defined in extension. But this macro is specific for
 * enumerations of single values, not maplets.
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class SMTEnumMacro extends SMTMacro {

	final SMTLIBVersion version;

	/**
	 * The assigned variable of the enumeration macro.
	 */
	private final SMTVarSymbol assignedVar;

	/**
	 * They represent the elements of the enumeration.
	 */
	private final SMTTerm[] terms;

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
	SMTEnumMacro(final SMTLIBVersion version, final String macroName,
			final SMTVarSymbol assignedVar, final SMTTerm[] terms,
			final int precedence) {
		super(macroName, precedence);
		this.version = version;
		this.assignedVar = assignedVar;
		this.terms = terms;
	}

	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * Retrieves the assigned variable
	 * 
	 * @return the assigned variable
	 */
	public SMTVarSymbol getAssignedVar() {
		return assignedVar;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}

	@Override
	public void toString(final StringBuilder sb, final int offset) {
		if (version == V1_2) {
			sb.append("(");
			sb.append(super.getMacroName());
			sb.append(" (lambda ");
			assignedVar.toString(sb);
			sb.append(" . ");
			if (terms.length == 1) {
				sb.append("(= ");
				assignedVar.getNameWithQMark(sb);
				sb.append(" ");
				terms[0].toString(sb, offset);
				sb.append(")))");
			} else {
				sb.append("(or");
				for (final SMTTerm term : terms) {
					sb.append("\n\t\t(= ");
					assignedVar.getNameWithQMark(sb);
					sb.append(" ");
					term.toString(sb, offset);
					sb.append(")");
				}
				sb.append("\n )))");
			}
		} else {
			sb.append(super.getMacroName());
			sb.append(" (");
			assignedVar.toString(sb);
			sb.append(") (Int Bool) ");
			if (terms.length == 1) {
				sb.append(" (= ");
				sb.append(assignedVar.getName());
				sb.append(" ");
				terms[0].toString(sb, offset);
				sb.append(")");
			} else {
				sb.append(" (or");
				for (final SMTTerm term : terms) {
					sb.append("\n\t\t(= ");
					sb.append(assignedVar.getName());
					sb.append(" ");
					term.toString(sb, offset);
					sb.append(")");
				}
				sb.append("\n )");
			}
		}
	}
}
