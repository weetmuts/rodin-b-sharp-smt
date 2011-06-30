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

import java.util.Set;

/**
 * The difference between verit term and normal terms is that the symbol of the
 * term is a predicate symbol, and not function symbol.
 * 
 * Each instance of this class is used to store predicates that are used as
 * arguments of macros. Since the macros arguments are terms, and the SMT preds
 * can be used as arguments to the macros, this class was created to solve this
 * problem, providing predicate symbols inside terms.
 * 
 * @author vitor
 * 
 */
public class SMTVeriTTerm extends SMTTerm {

	/**
	 * The predicate symbol of this term.
	 */
	private final SMTPredicateSymbol symbol;

	/**
	 * gets the predicate symbol
	 * 
	 * @return the predicate symbol
	 */
	public SMTPredicateSymbol getSymbol() {
		return symbol;
	}

	private static void checkIfPredIsDefinedInSignature(
			final SMTPredicateSymbol pred, final SMTSignature signature) {
		final Set<SMTPredicateSymbol> preds = signature.getPreds();
		for (final SMTPredicateSymbol predicate : preds) {
			if (predicate.getName().equals(pred.getName())) {
				return;
			}
		}
		throw new IllegalArgumentException("The predicate " + pred.toString()
				+ " is not defined in the signature");
	}

	/**
	 * Constructs a new verit term.
	 * 
	 * @param symbol
	 *            the predicate symbol of the term
	 */
	SMTVeriTTerm(final SMTPredicateSymbol symbol, final SMTSignature signature) {
		checkIfPredIsDefinedInSignature(symbol, signature);
		this.symbol = symbol;
		// VeriT uses Bool sort.
		sort = VeriTBooleans.getInstance().getBooleanSort();
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(symbol.name);
	}

	@Override
	public String toString() {
		return symbol.name;
	}

}
