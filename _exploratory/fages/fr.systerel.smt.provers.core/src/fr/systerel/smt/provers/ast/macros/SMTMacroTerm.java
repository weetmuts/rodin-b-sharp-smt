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
import fr.systerel.smt.provers.ast.SMTTerm;

/**
 * This class is used to represent terms that are macros. It stores the name of
 * the macro and the arguments of the macro.
 */
public class SMTMacroTerm extends SMTTerm {

	/**
	 * The macro symbol
	 */
	private final SMTMacroSymbol macroSymbol;

	/**
	 * The arguments of the macro term.
	 */
	SMTTerm[] args;

	@Override
	public void toString(final StringBuilder builder) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.getName());
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.getName());
			for (final SMTTerm term : args) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}

	public SMTMacroSymbol getMacroSymbol() {
		return macroSymbol;
	}

	/**
	 * Constructs a new macro term.
	 * 
	 * @param macro
	 *            the macro symbol for the term
	 * @param argTerms
	 *            the arguments of the macro term.
	 */
	public SMTMacroTerm(final SMTMacroSymbol macro, final SMTTerm[] argTerms) {
		macroSymbol = macro;
		this.args = argTerms;
		sort = macro.getReturnSort();
	}

	/**
	 * returns the arguments of the macro term.
	 * 
	 * @return the arguments of the macro term.
	 */
	public SMTTerm[] getArgs() {
		return args;
	}

}
