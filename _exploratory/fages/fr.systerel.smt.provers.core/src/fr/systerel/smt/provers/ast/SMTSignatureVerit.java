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
import java.util.SortedSet;
import java.util.TreeSet;

import fr.systerel.smt.provers.ast.SMTLogic.SMTVeriTOperator;
import fr.systerel.smt.provers.ast.macros.SMTEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacro;
import fr.systerel.smt.provers.ast.macros.SMTMacroFactory;
import fr.systerel.smt.provers.ast.macros.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTSetComprehensionMacro;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	private boolean isFstAndSndAssumptionsAdded = false;

	private final SortedSet<SMTMacro> macros = new TreeSet<SMTMacro>();
	private final SMTMacroFactory ms = new SMTMacroFactory();

	/**
	 * This variable stores additional assumptions produced by the translation
	 * of min,max, finite and cardinality operators
	 */
	private Set<SMTFormula> additionalAssumptions = new HashSet<SMTFormula>();

	public Set<SMTFormula> getAdditionalAssumptions() {
		return additionalAssumptions;
	}

	public void setFstAndSndAssumptionsAdded(
			final boolean isFstAndSndAssumptionsAdded) {
		this.isFstAndSndAssumptionsAdded = isFstAndSndAssumptionsAdded;
	}

	public void setAdditionalAssumptions(
			final Set<SMTFormula> additionalAssumptions) {
		this.additionalAssumptions = additionalAssumptions;
	}

	public SMTSignatureVerit(final SMTLogic logic) {
		super(logic);
	}

	public int getMacroLength() {
		return macros.size();
	}

	public void addMacro(final SMTMacro macro) {
		// FIXME: The set should take care of unique elements. This comparison
		// should not exist
		for (final SMTMacro macroEl : macros) {
			final String x1 = macroEl.getMacroName();
			final String x2 = macro.getMacroName();
			if (x1.equals(x2)) {
				return;
			}
		}
		macros.add(macro);
	}

	public SMTSymbol getSMTSymbol(final String identifierName) {
		for (final SMTFunctionSymbol functionSymbol : funs) {
			if (functionSymbol.name.equals(identifierName)) {
				return functionSymbol;
			}
		}
		for (final SMTPredicateSymbol predicateSymbol : preds) {
			if (predicateSymbol.name.equals(identifierName)) {
				return predicateSymbol;
			}
		}
		for (final SMTSortSymbol sortSymbol : sorts) {
			if (sortSymbol.name.equals(identifierName)) {
				return sortSymbol;
			}
		}
		throw new IllegalArgumentException(
				"The translation found a variable with the name: "
						+ identifierName
						+ ", which was not translated and saved in the SMTSignature. It should not happen.");
	}

	private void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			sb.append(":extramacros(");
			for (final SMTMacro macro : macros) {
				sb.append("\n");
				macro.toString(sb);
			}
			sb.append("\n)");
		}

	}

	private static Set<String> getNamesFromMacro(final Set<SMTMacro> macros) {
		final Set<String> macroNames = new HashSet<String>();
		for (final SMTMacro macro : macros) {
			macroNames.add(macro.getMacroName());
			if (macro instanceof SMTEnumMacro) {
				final SMTEnumMacro enumMacro = (SMTEnumMacro) macro;
				macroNames.add(enumMacro.getAssignedVar().getName());
			} else if (macro instanceof SMTPairEnumMacro) {
				final SMTPairEnumMacro pairEnumMacro = (SMTPairEnumMacro) macro;
				macroNames.add(pairEnumMacro.getKey().getName());
			} else if (macro instanceof SMTSetComprehensionMacro) {
				final SMTSetComprehensionMacro setComprehensionMacro = (SMTSetComprehensionMacro) macro;
				macroNames.add(setComprehensionMacro.getLambdaVar().getName());
				for (final SMTVarSymbol var : setComprehensionMacro.getqVars()) {
					macroNames.add(var.getName());
				}
			}
		}
		return macroNames;
	}

	@Override
	public String freshCstName(final String name) {
		return freshCstName(name, null);
	}

	public String freshCstName(final String name, final Set<String> usedNames) {
		final Set<String> names = new HashSet<String>();
		if (usedNames != null) {
			names.addAll(usedNames);
		}
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(sorts));
		names.addAll(getSymbolNames(preds));
		names.addAll(getNamesFromMacro(macros));
		names.addAll(ms.getqSymbols());
		for (final SMTVeriTOperator op : SMTVeriTOperator.values()) {
			names.add(op.toString());
		}

		if (reservedSymbols.contains(name) || attributeSymbols.contains(name)) {
			return freshName(names, NEW_SYMBOL_NAME);
		} else {
			return freshName(names, name);
		}
	}

	@Override
	public void toString(final StringBuilder sb) {
		super.toString(sb);
		extramacrosSection(sb);
	}

	@Override
	public Set<SMTSortSymbol> getSorts() {
		return sorts;
	}

	public void addSort(final SMTSortSymbol sort) {
		assert !sort.name.equals("U");
		sorts.add(sort);
	}

	public void addPred(final SMTPredicateSymbol predSymbol) {
		preds.add(predSymbol);
	}

	public void addAdditionalAssumption(final SMTFormula formula) {
		additionalAssumptions.add(formula);
	}

	public void addFstOrSndAuxiliarAssumption(final SMTFormula formula) {
		if (!isFstAndSndAssumptionsAdded) {
			additionalAssumptions.add(formula);
		}
	}

	@Override
	public void removeUnusedSymbols(final Set<SMTSymbol> symbols) {
		// TODO Auto-generated method stub
		final Set<SMTFunctionSymbol> funSymbols = new HashSet<SMTFunctionSymbol>();
		final Set<SMTPredicateSymbol> predSymbols = new HashSet<SMTPredicateSymbol>();
		final Set<SMTSortSymbol> sortSymbols = new HashSet<SMTSortSymbol>();

		for (final SMTSymbol symbol : symbols) {
			if (symbol instanceof SMTFunctionSymbol) {
				funSymbols.add((SMTFunctionSymbol) symbol);
			} else if (symbol instanceof SMTPredicateSymbol) {
				predSymbols.add((SMTPredicateSymbol) symbol);
			} else if (symbol instanceof SMTSortSymbol) {
				sortSymbols.add((SMTSortSymbol) symbol);
			}
			// TODO Test for macros. macros must show only in the
			// SMTSignatureVeriT
		}
		removeUnusedSymbols(funSymbols, predSymbols, sortSymbols);
	}

	private void removeUnusedSymbols(final Set<SMTFunctionSymbol> usedFuns,
			final Set<SMTPredicateSymbol> usedPreds,
			final Set<SMTSortSymbol> usedSorts) {

		final Set<SMTFunctionSymbol> unusedFunctionSymbols = removeUnusedFunctions(usedFuns);
		final Set<SMTPredicateSymbol> unusedPredicateSymbols = removeUnusedPreds(usedPreds);
		final Set<SMTSortSymbol> unusedSortSymbols = removeUnusedSorts(usedSorts);

		if (unusedFunctionSymbols.isEmpty() && unusedPredicateSymbols.isEmpty()
				&& unusedSortSymbols.isEmpty()) {
			return;
		}
		removeUnusedSymbols(usedFuns, usedPreds, usedSorts);
	}

}
