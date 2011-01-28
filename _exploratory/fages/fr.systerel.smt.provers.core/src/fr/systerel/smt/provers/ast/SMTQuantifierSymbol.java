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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum SMTQuantifierSymbol {
	EXISTS("exists"), //
	FORALL("forall");

	private String symbol;

	private SMTQuantifierSymbol(final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public static final List<String> getQuantifierSymbols() {
		final SMTQuantifierSymbol[] smtQuantifiers = SMTQuantifierSymbol
				.values();
		final List<String> quantifiers = new ArrayList<String>(
				smtQuantifiers.length);
		for (final SMTQuantifierSymbol quantifier : smtQuantifiers) {
			quantifiers.add(quantifier.toString());
		}
		return quantifiers;
	}
}
