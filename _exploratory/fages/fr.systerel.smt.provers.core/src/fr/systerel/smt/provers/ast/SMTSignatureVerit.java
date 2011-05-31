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
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;
import fr.systerel.smt.provers.ast.macros.SMTPairEnumMacro;
import fr.systerel.smt.provers.ast.macros.SMTSetComprehensionMacro;

/**
 * This is the SMTSignature to be used by the SMT translation process through
 * veriT.
 * 
 */
// FIXME this class must be refactored
public class SMTSignatureVerit extends SMTSignature {

	/**
	 * This boolean is used to check if it is necessary to add the function pair
	 * and the sort Pair or not.
	 */
	private boolean isPrintPairSortAndPairFunctionAdded = false;

	/**
	 * This boolean is used to check if it is necessary to add the functions and
	 * the assumptions of the functions first and snd or not
	 */
	private boolean isFstAndSndAssumptionsAdded = false;

	/**
	 * This boolean is used to check if the axiom of equality of pairs are added
	 * or not
	 */
	private boolean isPairEqualityAxiomAdded = false;

	/**
	 * this set stores the macros that will be print into the benchmark.
	 */
	private final SortedSet<SMTMacro> macros = new TreeSet<SMTMacro>();

	/**
	 * The factory of macros
	 */
	private final SMTMacroFactory ms = new SMTMacroFactory();

	/**
	 * This variable stores additional assumptions produced by the translation
	 * of min,max, finite and cardinality operators
	 */
	private Set<SMTFormula> additionalAssumptions = new HashSet<SMTFormula>();

	/**
	 * Returns the additional assumptions used for the translation of the
	 * event-B PO
	 * 
	 * @return the additional assumptions used for the translation of the
	 *         event-B PO
	 */
	public Set<SMTFormula> getAdditionalAssumptions() {
		return additionalAssumptions;
	}

	public void addPairEqualityAxiom(final SMTFormula formula) {
		if (!isPairEqualityAxiomAdded) {
			isPairEqualityAxiomAdded = true;
			additionalAssumptions.add(formula);
		}
	}

	public void addPairSortAndFunction() {
		if (isPrintPairSortAndPairFunctionAdded == false) {
			sorts.add(SMTMacroFactory.PAIR_SORT);
			funs.add(SMTMacroFactory.PAIR_SYMBOL);
			isPrintPairSortAndPairFunctionAdded = true;
		}
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	@Override
	public SMTSortSymbol freshSort(final String name) {
		final String freshName;

		if (name.equals("\u2124")) { // INTEGER
			freshName = SMTSymbol.INT;
		} else if (name.equals("BOOL")) {
			freshName = SMTMacroSymbol.BOOL_SORT_VERIT;
		} else {
			freshName = freshSymbolName(name);
		}
		final SMTSortSymbol freshSort = new SMTSortSymbol(freshName,
				!SMTSymbol.PREDEFINED);
		final boolean successfullyAdded = sorts.add(freshSort);
		if (!successfullyAdded) {
			// TODO Throw an exception, this case should not be reached because
			// freshSymbolName should always be successful.
		}
		return freshSort;
	}

	public SortedSet<SMTMacro> getMacros() {
		return macros;
	}

	public void setAdditionalAssumptions(
			final Set<SMTFormula> additionalAssumptions) {
		this.additionalAssumptions = additionalAssumptions;
	}

	public SMTSignatureVerit(final SMTLogic logic) {
		super(logic);
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
	public String freshSymbolName(final String name) {
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

		return freshSymbolName(names, name);
	}

	@Override
	public void toString(final StringBuilder sb) {
		super.toString(sb);
		extramacrosSection(sb);
	}

	public void addAdditionalAssumption(final SMTFormula formula) {
		additionalAssumptions.add(formula);
	}

	public void addFstAndSndAuxiliarAssumptionsAndFunctions() {
		if (!isFstAndSndAssumptionsAdded) {
			funs.add(SMTMacroFactory.FST_SYMBOL);
			funs.add(SMTMacroFactory.SND_SYMBOL);
			additionalAssumptions
					.add(SMTMacroFactory.createFstAssumption(this));
			additionalAssumptions
					.add(SMTMacroFactory.createSndAssumption(this));
			isFstAndSndAssumptionsAdded = true;
		}
	}

	@Override
	public void removeUnusedSymbols(final Set<SMTSymbol> symbols) {
		// TODO Implement this method (take care with pair function and sort)
	}

}
