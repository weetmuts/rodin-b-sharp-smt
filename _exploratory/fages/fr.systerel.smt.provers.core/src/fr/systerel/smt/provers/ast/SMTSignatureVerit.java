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

import java.util.HashSet;
import java.util.Set;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	private final Set<String> macros = new HashSet<String>();

	public SMTSignatureVerit(final SMTLogic logic) {
		super(logic);
	}

	public void addMacro(final String macro) {
		macros.add(macro);
	}

	public SMTFunctionSymbol getSMTVariable(final String identifierName) {
		for (SMTFunctionSymbol functionSymbol : funs) {
			if (functionSymbol.name.equals(identifierName)) {
				return functionSymbol;
			}
		}
		throw new IllegalArgumentException(
				"The translation found a variable with the name: "
						+ identifierName
						+ ",which was not translated and saved in the SMTSignature. It's  problem and should not happen.");
	}

	private void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			sb.append("(extramacros(");
			for (String macro : macros) {
				sb.append("\n");
				sb.append(macro);
			}
			sb.append("\n)");
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		this.extramacrosSection(sb);
	}

	public void addSort(final String sortName, final boolean predefined) {
		final SMTSortSymbol sort = new SMTSortSymbol(sortName, predefined);
		if (!this.sorts.contains(sort)) {
			this.sorts.add(sort);
		}
	}

	public void addPred(final String predName, final SMTSortSymbol symbol) {
		SMTSortSymbol[] symbols = { symbol };
		this.preds.add(new SMTPredicateSymbol(predName, symbols,
				!SMTSymbol.PREDEFINED));
	}

	public void addPred(final String predName, final String argSortStrings[]) {
		final SMTSortSymbol[] argSorts = new SMTSortSymbol[argSortStrings.length];
		for (int i = 0; i < argSortStrings.length; i++) {
			argSorts[i] = new SMTSortSymbol(argSortStrings[i],
					!SMTSymbol.PREDEFINED);
		}
		this.preds.add(new SMTPredicateSymbol(predName, argSorts,
				!SMTSymbol.PREDEFINED));
	}

	public void addPairPred(final String predName,
			final SMTPairSortSymbol symbol) {

		SMTSortSymbol[] symbols = { symbol };
		this.preds.add(new SMTPredicateSymbol(predName, symbols,
				!SMTSymbol.PREDEFINED));
	}

	public Set<SMTSortSymbol> getSorts() {
		return this.sorts;
	}
}
