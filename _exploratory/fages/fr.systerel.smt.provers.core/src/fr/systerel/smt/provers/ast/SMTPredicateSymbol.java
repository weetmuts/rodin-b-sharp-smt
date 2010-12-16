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
public class SMTPredicateSymbol implements SMTSymbol {
	/**
	 * The predicate predicate
	 */
	final private String symbol;
	/**
	 * The rank (as defined in SMT-LIB Signature definition). Remind that it is
	 * possible to associate a predicate predicate to the empty sequence rank,
	 * denoting that the predicate is a propositional predicate.
	 */
	final private SMTSort[] argSorts;

	public SMTPredicateSymbol(final String symbol, final SMTSort... argSorts) {
		this.symbol = symbol;
		this.argSorts = argSorts;
	}

	public boolean isPropositional() {
		return this.argSorts == null;
	}

	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(OPAR);
		buffer.append(this.symbol);
		for (SMTSort sort : this.argSorts) {
			buffer.append(SPACE);
			buffer.append(sort);
		}
		buffer.append(CPAR);
		return buffer.toString();
	}
}
