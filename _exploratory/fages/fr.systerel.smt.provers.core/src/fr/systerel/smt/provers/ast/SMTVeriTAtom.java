/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class represents an SMTAtom
 */
class SMTVeriTAtom extends SMTFormula {
	final SMTMacroSymbol predicate;
	final SMTTerm[] terms;

	/**
	 * 
	 * @param symbol
	 * @param terms
	 */
	SMTVeriTAtom(final SMTMacroSymbol symbol, final SMTTerm terms[]) {
		// TODO: Create a verification method for macros
		predicate = symbol;
		this.terms = terms.clone();
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		if (predicate.isPropositional()) {
			builder.append(predicate.name);
		} else {
			builder.append(OPAR);
			builder.append(predicate.name);
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}
}
