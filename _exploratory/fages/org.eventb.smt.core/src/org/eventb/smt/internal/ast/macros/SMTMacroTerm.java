/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.ast.macros;

import static org.eventb.smt.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.internal.ast.SMTFactory.SPACE;

import org.eventb.smt.internal.ast.SMTTerm;

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
		args = argTerms;
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

	@Override
	public void toString(final StringBuilder builder, final int offset) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.getName());
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.getName());
			for (final SMTTerm term : args) {
				builder.append(SPACE);
				term.toString(builder, offset);
			}
			builder.append(CPAR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1);
		return builder.toString();
	}

}
