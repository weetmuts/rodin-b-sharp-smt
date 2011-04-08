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

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	private boolean isFstAndSndAssumptionsAdded = false;

	private final SortedSet<SMTMacro> macros = new TreeSet<SMTMacro>();
	private SMTMacros ms = new SMTMacros();

	/**
	 * This variable stores additional assumptions produced by the translation
	 * of min,max, finite and cardinality operators
	 */
	private Set<SMTFormula> additionalAssumptions = new HashSet<SMTFormula>();

	public Set<SMTFormula> getAdditionalAssumptions() {
		return additionalAssumptions;
	}

	public void setFstAndSndAssumptionsAdded(boolean isFstAndSndAssumptionsAdded) {
		this.isFstAndSndAssumptionsAdded = isFstAndSndAssumptionsAdded;
	}

	public void setAdditionalAssumptions(Set<SMTFormula> additionalAssumptions) {
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
		for (SMTMacro macroEl : macros) {
			String x1 = macroEl.getMacroName();
			String x2 = macro.getMacroName();
			if (x1.equals(x2))
				return;
		}
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
		for (SMTSortSymbol sortSymbol : sorts) {
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
			for (SMTMacro macro : macros) {
				sb.append("\n");
				sb.append(macro);
			}
			sb.append("\n)");
		}

	}

	private static Set<String> getNamesFromMacro(Set<SMTMacro> macros) {
		Set<String> macroNames = new HashSet<String>();
		for (SMTMacro macro : macros) {
			macroNames.add(macro.getMacroName());
			if (macro instanceof SMTEnumMacro) {
				SMTEnumMacro enumMacro = (SMTEnumMacro) macro;
				macroNames.add(enumMacro.getVar().getName());
			} else if (macro instanceof SMTPairEnumMacro) {
				SMTPairEnumMacro pairEnumMacro = (SMTPairEnumMacro) macro;
				macroNames.add(pairEnumMacro.getVar1().getName());
				macroNames.add(pairEnumMacro.getVar2().getName());
			} else if (macro instanceof SMTSetComprehensionMacro) {
				SMTSetComprehensionMacro setComprehensionMacro = (SMTSetComprehensionMacro) macro;
				macroNames.add(setComprehensionMacro.getLambdaVar().getName());
				for (SMTVarSymbol var : setComprehensionMacro.getqVars()) {
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

	public String freshCstName(final String name, String usedName) {
		Set<String> names = new HashSet<String>();
		if (name != null) {
			names.add(usedName);
		}
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(sorts));
		names.addAll(getSymbolNames(preds));
		names.addAll(getNamesFromMacro(macros));
		names.addAll(SMTMacroSymbol.getVeritSymbols());
		names.addAll(ms.getqSymbols());
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

	public void addAdditionalAssumption(SMTFormula formula) {
		additionalAssumptions.add(formula);
	}

	public void addFstOrSndAuxiliarAssumption(SMTFormula formula) {
		if (!isFstAndSndAssumptionsAdded) {
			this.additionalAssumptions.add(formula);
		}

	}

}
