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

public class SMTMacroTerm extends SMTTerm {

	private final SMTMacroSymbol macroSymbol;
	SMTTerm[] args;

	@Override
	public void toString(final StringBuilder builder) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.name);
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.name);
			for (final SMTTerm term : args) {
				builder.append(SPACE);
				term.toString(builder);
			}
			builder.append(CPAR);
		}
	}

	public SMTMacroTerm(final SMTMacroSymbol macro, final SMTTerm[] argTerms,
			final SMTSortSymbol returnSort) {
		macroSymbol = macro;
		this.args = argTerms;
		sort = returnSort;
	}

	public SMTMacroTerm(final SMTMacroSymbol macro, final SMTTerm[] argTerms) {
		macroSymbol = macro;
		this.args = argTerms;
		sort = macro.getReturnSort();
	}

	public SMTTerm[] getArgs() {
		return args;
	}

}
