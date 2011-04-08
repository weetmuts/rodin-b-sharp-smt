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
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;

public class SMTMacroTerm extends SMTTerm {

	private SMTMacroSymbol macroSymbol;
	SMTTerm[] argTerms;

	@Override
	public void toString(StringBuilder builder) {
		if (macroSymbol.isPropositional()) {
			builder.append(macroSymbol.name);
		} else {
			builder.append(OPAR);
			builder.append(macroSymbol.name);
			for (final SMTTerm term : argTerms) {
				builder.append(SPACE);
				builder.append(term);
			}
			builder.append(CPAR);
		}
	}

	public SMTMacroTerm(SMTMacroSymbol macro, SMTTerm[] argTerms) {
		this.macroSymbol = macro;
		this.argTerms = argTerms;
		// sort = SMTMacros.POLYMORPHIC;
		sort = Booleans.getInstance().getBooleanSort();
	}

	SMTTerm[] getArgTerms() {
		return argTerms;
	}

}
