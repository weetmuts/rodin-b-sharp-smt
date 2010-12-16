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

/**
 * @author guyot
 * 
 */
public class SMTAtom extends SMTFormula {
	final SMTPredicateSymbol predicate;
	final SMTTerm[] terms;

	public SMTAtom(final SMTPredicateSymbol symbol, final SMTTerm... terms) {
		this.predicate = symbol;
		this.terms = terms;
	}

	@Override
	public void toString(StringBuilder builder) {
		if (this.predicate.isPropositional()) {
			builder.append(this.predicate);
		} else {
			builder.append(OPAR);
			builder.append(this.predicate.getSymbol());
			for (final SMTTerm term : terms) {
				builder.append(SPACE);
				builder.append(term);
			}
			builder.append(CPAR);
		}
	}
}
