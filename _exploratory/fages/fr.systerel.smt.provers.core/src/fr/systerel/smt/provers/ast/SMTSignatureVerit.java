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

import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	private final Set<SMTMacro> macros = new HashSet<SMTMacro>();

	private Set<String> macroNames = new HashSet<String>();

	public SMTSignatureVerit(final SMTLogic logic) {
		super(logic);
	}

	public void addMacro(final SMTMacro macro) {
		macroNames.add(macro.getMacroName());
		macros.add(macro);
	}

	public SMTSymbol getSMTSymbol(final String identifierName) {
		for (SMTFunctionSymbol functionSymbol : funs) {
			if (functionSymbol.name.equals(identifierName)) {
				return functionSymbol;
			}
		}
		for (SMTPredicateSymbol predicateSymbol : preds) {
			if (predicateSymbol.name.equals(identifierName)) {
				return predicateSymbol;
			}
		}
		throw new IllegalArgumentException(
				"The translation found a variable with the name: "
						+ identifierName
						+ ", which was not translated and saved in the SMTSignature. It should not happen.");
	}

	private void extramacrosSection(final StringBuilder sb) {
		sb.append("(extramacros(");
		for (SMTMacro macro : macros) {
			sb.append("\n");
			sb.append(macro);
		}
		sb.append("\n)");
	}

	private static Set<String> getNamesFromMacro(Set<SMTMacro> macros) {
		Set<String> macroNames = new HashSet<String>();
		for (SMTMacro macro : macros) {
			macroNames.add(macro.getMacroName());
		}
		return macroNames;
	}

	@Override
	public String freshCstName(final String name) {
		Set<String> names = new HashSet<String>();
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(sorts));
		names.addAll(getSymbolNames(preds));
		names.addAll(getNamesFromMacro(macros));
		for (SMTVeriTOperator op : SMTVeriTOperator.values()) {
			names.add(op.toString());
		}
		if (reservedSymbols.contains(name) || attributeSymbols.contains(name)) {
			return freshName(names, NEW_SYMBOL_NAME);
		} else {
			return freshName(names, name);
		}
	}

	@Override
	public void toString(StringBuilder sb) {
		super.toString(sb);
		this.extramacrosSection(sb);
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

	@Override
	public Set<SMTSortSymbol> getSorts() {
		return this.sorts;
	}

	public void addSort(SMTSortSymbol sort) {
		this.sorts.add(sort);
	}

	public void addPred(SMTPredicateSymbol predSymbol) {
		this.preds.add(predSymbol);
	}

}
