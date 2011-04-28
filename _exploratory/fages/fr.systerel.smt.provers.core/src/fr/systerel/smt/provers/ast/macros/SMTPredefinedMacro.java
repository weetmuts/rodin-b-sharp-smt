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

import java.util.HashSet;
import java.util.Set;

/**
 * This class stores the macros that are predefined. They contain only the name
 * and its body. All the macros defined before the translation rules in the
 * paper “Integration of SMT-Solvers in B and Event-B Development Environments”,
 * from DEHARBE, David, and are predefined macros, like IN, EMPTY, SUBSET and
 * others.
 * 
 * @author vitor
 * 
 */
public class SMTPredefinedMacro extends SMTMacro {

	/**
	 * The body of the macro
	 */
	private final String body;

	/**
	 * This set stores the name of all identifiers of the macro that have a
	 * question mark prefixed.
	 */
	private final Set<String> qSymbols = new HashSet<String>();

	/**
	 * Initializes the class with the name of the macro, the body text and the
	 * precedence.
	 * 
	 * @param macroName
	 *            the name of the macro
	 * @param bodyText
	 *            the body text of the predefined macro
	 * @param precedence
	 *            the precedence of the macro. See {@link SMTMacro} for more
	 *            details.
	 */
	SMTPredefinedMacro(final String macroName, final String bodyText,
			final int precedence) {
		super(macroName, precedence);
		body = bodyText;
		collectQSymbols();
	}

	/**
	 * Retrieves the name of the identifiers that have a question mark as a
	 * prefix.
	 * 
	 * @return the identifiers as defined above.
	 */
	public Set<String> getQSymbols() {
		return qSymbols;
	}

	/**
	 * This method collects and saves all the identifiers of the macro that has
	 * a question mark prefixed.
	 * 
	 * @see #qSymbols qSymbols
	 */
	protected void collectQSymbols() {
		for (int i = 0; i < body.length(); i++) {
			if (body.charAt(i) == '?') {
				for (int j = i + 1; j < body.length(); j++) {
					// if (body.charAt(j) == ' ' || ) {
					if (!Character.isLetterOrDigit(body.charAt(j))) {
						++i;
						getQSymbols().add(body.substring(i, j));
						++j;
						i = j;
						break;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof SMTPredefinedMacro) {
			final SMTPredefinedMacro obj = (SMTPredefinedMacro) object;
			if (getMacroName().equals(obj.getMacroName())
					&& body.equals(obj.body)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void toString(final StringBuilder sb) {
		sb.append("(");
		sb.append(super.getMacroName());
		sb.append(body);
		sb.append(")");
	}
}
