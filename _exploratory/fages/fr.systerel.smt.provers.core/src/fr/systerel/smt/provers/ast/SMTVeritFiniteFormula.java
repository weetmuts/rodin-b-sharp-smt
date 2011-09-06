/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

/**
 * This class was created to handle VeriT finite Formula. In the paper
 * "Integration of SMT-Solvers in B and Event-B Development Environments", from
 * DEHARBE, David, dated of December 17, 2010, it explains in the rule 24 how to
 * translate the Event-B finite formula.
 * 
 * In the formalized translation, it must be added a new assumption: (finite p t
 * f k).
 * 
 * This class represents this assumption, where: finite is a macro symbol, p is
 * a predicate symbol, and t, f and k are functions and t are terms.
 * 
 * 
 * @author vitor
 * 
 */
public class SMTVeritFiniteFormula extends SMTFormula {

	/**
	 * the macro symbol of the formula
	 */
	private final SMTMacroSymbol finitePred;

	/**
	 * The p argument
	 */
	private final SMTPredicateSymbol pArgument;

	/**
	 * The k argument
	 */
	private final SMTFunctionSymbol kArgument;

	/**
	 * The f argument
	 */
	private final SMTFunctionSymbol fArgument;

	/**
	 * The terms
	 */
	private final SMTTerm[] terms;

	/**
	 * gets the macro symbol of the formula
	 * 
	 * @return the finite macro symbol
	 */
	public SMTMacroSymbol getFinitePred() {
		return finitePred;
	}

	/**
	 * gets the p argument
	 * 
	 * @return the p argument
	 */
	public SMTPredicateSymbol getpArgument() {
		return pArgument;
	}

	/**
	 * gets the k argument
	 * 
	 * @return the k argument
	 */
	public SMTFunctionSymbol getkArgument() {
		return kArgument;
	}

	/**
	 * gets the f argument
	 * 
	 * @return the f argument
	 */
	public SMTFunctionSymbol getfArgument() {
		return fArgument;
	}

	/**
	 * gets the terms of the formula
	 * 
	 * @return the terms of the formula
	 */
	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * Constructs a new finite formula
	 * 
	 * @param finitePredSymbol
	 *            the macro symbol which represents finite macro
	 * @param pArgument
	 *            the p argument
	 * @param fArgument
	 *            the f argument
	 * @param kArgument
	 *            the k argument
	 * @param terms
	 *            the terms
	 * 
	 * @see SMTVeritFiniteFormula
	 */
	public SMTVeritFiniteFormula(final SMTMacroSymbol finitePredSymbol,
			final SMTPredicateSymbol pArgument,
			final SMTFunctionSymbol fArgument,
			final SMTFunctionSymbol kArgument, final SMTTerm[] terms,
			final SMTSignatureVerit signature) {
		SMTMacroFactory.checkIfMacroIsDefinedInTheSignature(finitePredSymbol,
				signature);
		finitePred = finitePredSymbol;
		this.terms = terms;
		this.pArgument = pArgument;
		this.kArgument = kArgument;
		this.fArgument = fArgument;
	}

	@Override
	public void toString(final StringBuilder builder, final int offset,
			final boolean printPoint) {
		builder.append(OPAR);
		builder.append(finitePred.name);
		builder.append(SPACE);
		builder.append(pArgument.name);
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			term.toString(builder, offset);
		}
		builder.append(SPACE);
		builder.append(fArgument.name);
		builder.append(SPACE);
		builder.append(kArgument.name);
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		this.toString(sb, -1, false);
		return sb.toString();
	}

}
