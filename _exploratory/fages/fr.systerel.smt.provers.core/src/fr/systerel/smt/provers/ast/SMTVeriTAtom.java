/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

import java.util.Set;

import fr.systerel.smt.provers.ast.macros.SMTMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class is used to create atoms with macros being its symbols. The
 * difference between SMTVeriTAtom and SMTVeritTerm is that some macros are used
 * in a predicate level (example of macros: ismin, ismax, in).
 * 
 * The difference between veriT Atom and normal atoms is that this one does not
 * have a predicate, but a macro as its symbol.
 * 
 */
class SMTVeriTAtom extends SMTFormula {

	/**
	 * The macro symbol of the atom
	 */
	final SMTMacroSymbol macroSymbol;

	/**
	 * the terms
	 */
	final SMTTerm[] terms;

	/**
	 * get the macro symbol
	 * 
	 * @return the macro symbol
	 */
	public SMTMacroSymbol getMacroSymbol() {
		return macroSymbol;
	}

	/**
	 * get the terms
	 * 
	 * @return the terms
	 */
	public SMTTerm[] getTerms() {
		return terms;
	}

	private static void checkIfMacroIsDefinedInTheSignature(
			final SMTMacroSymbol macro, final SMTSignatureVerit signature) {
		final Set<SMTMacro> macros = signature.getMacros();
		for (final SMTMacro smtMacro : macros) {
			if (smtMacro.getMacroName().equals(macro.getName())) {
				return;
			}
		}
		throw new IllegalArgumentException(
				"A macro cannot be created without being defined in the signature");
	}

	/**
	 * Constructs a new veriT atom
	 * 
	 * @param symbol
	 *            the macro symbol
	 * @param terms
	 *            the terms
	 */
	SMTVeriTAtom(final SMTMacroSymbol symbol, final SMTTerm terms[],
			final SMTSignatureVerit signature) {
		checkIfMacroIsDefinedInTheSignature(symbol, signature);
		macroSymbol = symbol;
		this.terms = terms.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.name);
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.name);
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}

	@Override
	public String toString() {
		if (macroSymbol.isPropositional()) {
			return macroSymbol.getName();
		} else {
			String s = OPAR + macroSymbol.getName();
			for (final SMTTerm term : terms) {
				s += SPACE + term;
			}
			s += CPAR;
			return s;
		}
	}
}
