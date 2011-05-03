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
package fr.systerel.smt.provers.ast;

public class SMTVeriTTerm extends SMTTerm {

	private final SMTPredicateSymbol symbol;

	SMTVeriTTerm(final SMTPredicateSymbol symbol) {
		this.symbol = symbol;
		// VeriT uses Bool sort.
		sort = VeritPredefinedTheory.getInstance().getBooleanSort();
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(symbol.name);
	}

}
