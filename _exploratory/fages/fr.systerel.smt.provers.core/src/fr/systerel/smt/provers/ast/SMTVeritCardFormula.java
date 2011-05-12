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

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

public class SMTVeritCardFormula extends SMTFormula {

	private final SMTMacroSymbol cardSymbol;
	private final SMTFunctionSymbol kArgument;
	private final SMTFunctionSymbol fArgument;
	private final SMTTerm[] terms;

	public SMTMacroSymbol getCardSymbol() {
		return cardSymbol;
	}

	public SMTFunctionSymbol getkArgument() {
		return kArgument;
	}

	public SMTFunctionSymbol getfArgument() {
		return fArgument;
	}

	public SMTTerm[] getTerms() {
		return terms;
	}

	/**
	 * TODO Comment this
	 * 
	 * @param cardSymbol
	 * @param fVarSymbol
	 * @param kVarSymbol
	 * @param terms
	 */
	public SMTVeritCardFormula(final SMTMacroSymbol cardSymbol,
			final SMTFunctionSymbol fVarSymbol,
			final SMTFunctionSymbol kVarSymbol, final SMTTerm[] terms) {
		this.cardSymbol = cardSymbol;
		this.terms = terms;
		kArgument = kVarSymbol;
		fArgument = fVarSymbol;
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		builder.append(OPAR);
		builder.append(cardSymbol.getName());
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			term.toString(builder);
		}
		builder.append(SPACE);
		builder.append(fArgument.getName());
		builder.append(SPACE);
		builder.append(kArgument.getName());
		builder.append(CPAR);
	}

	@Override
	public String toString() {
		String s = OPAR + cardSymbol.getName();
		for (final SMTTerm term : terms) {
			s += SPACE + term;
		}
		s += SPACE + fArgument.getName() + SPACE + kArgument.getName() + CPAR;
		return s;
	}

}
