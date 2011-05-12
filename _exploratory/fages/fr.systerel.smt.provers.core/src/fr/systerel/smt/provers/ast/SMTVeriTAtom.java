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
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * TODO: Comment this class
 */
class SMTVeriTAtom extends SMTFormula {

	final SMTMacroSymbol predicateSymbol;
	final SMTTerm[] terms;

	public SMTMacroSymbol getPredicate() {
		return predicateSymbol;
	}

	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * 
	 * @param symbol
	 * @param terms
	 */
	SMTVeriTAtom(final SMTMacroSymbol symbol, final SMTTerm terms[]) {
		// TODO: Create a verification method for macros
		predicateSymbol = symbol;
		this.terms = terms.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		if (predicateSymbol.isPropositional()) {
			builder.append(predicateSymbol.name);
		} else {
			builder.append(OPAR);
			builder.append(predicateSymbol.name);
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}

	@Override
	public String toString() {
		if (predicateSymbol.isPropositional()) {
			return predicateSymbol.getName();
		} else {
			String s = OPAR + predicateSymbol.getName();
			for (final SMTTerm term : terms) {
				s += SPACE + term;
			}
			s += CPAR;
			return s;
		}
	}
}
