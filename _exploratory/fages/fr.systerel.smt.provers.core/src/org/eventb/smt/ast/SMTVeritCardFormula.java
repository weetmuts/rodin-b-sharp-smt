/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
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

import org.eventb.smt.ast.macros.SMTMacroFactory;
import org.eventb.smt.ast.macros.SMTMacroSymbol;

/**
 * This class was created to handle VeriT Card Formula. In the paper
 * "Integration of SMT-Solvers in B and Event-B Development Environments", from
 * DEHARBE, David, dated of December 17, 2010, it explains in the rule 25 how to
 * translate the Event-B cardinality term.
 * 
 * In the formalized translation, it must be added a new assumption: (card t f
 * k).
 * 
 * This class represents this assumption, where: card is a macro symbol, and t,
 * f and k are functions and t are terms.
 * 
 */
public class SMTVeritCardFormula extends SMTFormula {

	/**
	 * The card macro symbol
	 */
	private final SMTMacroSymbol cardSymbol;

	/**
	 * the k argument
	 */
	private final SMTFunctionSymbol kArgument;

	/**
	 * the f argument
	 */
	private final SMTFunctionSymbol fArgument;

	/**
	 * the terms
	 */
	private final SMTTerm[] terms;

	/**
	 * get the macro symbol
	 * 
	 * @return the macro symbol
	 */
	public SMTMacroSymbol getCardSymbol() {
		return cardSymbol;
	}

	/**
	 * get the k argument
	 * 
	 * @return the k argument
	 */
	public SMTFunctionSymbol getkArgument() {
		return kArgument;
	}

	/**
	 * get the f argument
	 * 
	 * @return the f argument
	 */
	public SMTFunctionSymbol getfArgument() {
		return fArgument;
	}

	/**
	 * get the term
	 * 
	 * @return the terms
	 */
	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * Constructs a new card formula
	 * 
	 * @param cardSymbol
	 *            the card macro symbol
	 * @param fVarSymbol
	 *            the f argument
	 * @param kVarSymbol
	 *            the k argument
	 * @param terms
	 *            the terms of the formula
	 * 
	 * @see SMTVeritCardFormula
	 */
	public SMTVeritCardFormula(final SMTMacroSymbol cardSymbol,
			final SMTFunctionSymbol fVarSymbol,
			final SMTFunctionSymbol kVarSymbol, final SMTTerm[] terms,
			final SMTSignatureVerit signature) {
		SMTMacroFactory.checkIfMacroIsDefinedInTheSignature(cardSymbol,
				signature);
		this.cardSymbol = cardSymbol;
		this.terms = terms;
		kArgument = kVarSymbol;
		fArgument = fVarSymbol;
	}

	@Override
	public void toString(final StringBuilder builder, final int offset,
			final boolean printPoint) {
		builder.append(OPAR);
		builder.append(cardSymbol.getName());
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			term.toString(builder, offset);
		}
		builder.append(SPACE);
		builder.append(fArgument.getName());
		builder.append(SPACE);
		builder.append(kArgument.getName());
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		this.toString(sb, -1, false);
		return sb.toString();
	}

}
