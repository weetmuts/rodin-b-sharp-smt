/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.SMTFactory.CPAR;
import static org.eventb.smt.ast.SMTFactory.OPAR;
import static org.eventb.smt.ast.SMTFactory.SPACE;

import org.eventb.smt.ast.macros.SMTMacroFactoryV1_2;
import org.eventb.smt.ast.macros.SMTMacroFactoryV2_0;
import org.eventb.smt.ast.macros.SMTMacroSymbol;

/**
 * This class is used to create atoms with macros being its symbols. This class
 * was created because some macros are used in a predicate level (example of
 * macros: ismin, ismax, in).
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

	/**
	 * Constructs a new veriT atom, using using SMTSignature
	 * 
	 * @param symbol
	 *            the macro symbol
	 * @param terms
	 *            the terms
	 */
	SMTVeriTAtom(final SMTMacroSymbol symbol, final SMTTerm terms[],
			final SMTSignature signature) {
		if (signature instanceof SMTSignatureV1_2Verit) {
			SMTMacroFactoryV1_2.checkIfMacroIsDefinedInTheSignature(symbol,
					(SMTSignatureV1_2Verit) signature);
		} else if (signature instanceof SMTSignatureV2_0Verit) {
			SMTMacroFactoryV2_0.checkIfMacroIsDefinedInTheSignature(symbol,
					(SMTSignatureV2_0Verit) signature);
		} else {
			throw new IllegalArgumentException(
					"Wrong signature type. It must be veriT type.");
		}
		macroSymbol = symbol;
		this.terms = terms.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final int offset,
			final boolean printPoint) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.getName());
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.getName());
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder, offset);
			}
			builder.append(CPAR);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder, -1, false);
		return builder.toString();
	}
}
