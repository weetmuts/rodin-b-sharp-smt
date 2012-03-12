/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - implementation
 *******************************************************************************/

package org.eventb.smt.internal.ast;

import static org.eventb.smt.core.translation.SMTLIBVersion.V2_0;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eventb.smt.internal.ast.commands.SMTDeclareFunCommand;
import org.eventb.smt.internal.ast.commands.SMTDeclareSortCommand;
import org.eventb.smt.internal.ast.commands.SMTSetLogicCommand;
import org.eventb.smt.internal.ast.macros.SMTEnumMacro;
import org.eventb.smt.internal.ast.macros.SMTMacro;
import org.eventb.smt.internal.ast.macros.SMTMacroFactoryV2_0;
import org.eventb.smt.internal.ast.macros.SMTMacroFactoryV2_0.SMTVeriTOperatorV2_0;
import org.eventb.smt.internal.ast.macros.SMTMacroSymbol;
import org.eventb.smt.internal.ast.macros.SMTPairEnumMacro;
import org.eventb.smt.internal.ast.macros.SMTPredefinedMacro;
import org.eventb.smt.internal.ast.macros.SMTSetComprehensionMacro;
import org.eventb.smt.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.internal.ast.symbols.SMTVarSymbol;
import org.eventb.smt.internal.ast.theories.SMTLogic;
import org.eventb.smt.internal.ast.theories.SMTTheory;
import org.eventb.smt.internal.ast.theories.SMTTheoryV2_0;
import org.eventb.smt.internal.ast.theories.VeriTBooleansV2_0;

public class SMTSignatureV2_0Verit extends SMTSignatureV2_0 {

	/**
	 * This boolean is used to check if it is necessary to add the function pair
	 * and the sort Pair or not.
	 */
	private boolean printPairSortAndPairFunction = false;

	/**
	 * This boolean is used to check if it is necessary to add the functions and
	 * the assumptions of the functions first and snd or not
	 */
	private boolean printFstAndSndFunctions = false;

	/**
	 * this set stores the macros that will be print into the benchmark.
	 */
	private final SortedSet<SMTMacro> macros = new TreeSet<SMTMacro>();

	/**
	 * The factory of macros
	 */
	private final SMTMacroFactoryV2_0 ms = new SMTMacroFactoryV2_0();

	/**
	 * Adds the sort Pair and the function pair into the signature (it adds only
	 * once)
	 */
	public void addPairSortAndFunction() {
		if (!printPairSortAndPairFunction) {
			sorts.add(SMTFactoryVeriT.PAIR_SORT_V2_0);
			funs.add(SMTFactoryVeriT.PAIR_SYMBOL_V2_0);
			printPairSortAndPairFunction = true;
		}
	}

	/**
	 * Gives a fresh sort. In this version of veriT, it returns standard names
	 * for Int or Bool set.
	 * 
	 * @param name
	 *            the name of the fresh sort
	 */
	@Override
	public SMTSortSymbol freshSort(final String name) {
		final String freshName;

		if (name.equals("ℤ")) { // INTEGER
			freshName = SMTSymbol.INT;
		} else if (name.equals("BOOL")) {
			return getBoolSort();
		} else {
			freshName = freshSymbolName(name);
		}
		final SMTSortSymbol freshSort = new SMTSortSymbol(freshName,
				!SMTSymbol.PREDEFINED, V2_0);
		sorts.add(freshSort);
		return freshSort;
	}

	/**
	 * This method returns the Bool sort. It first check if the
	 * {@link VeriTBooleansV2_0} theory is being used. If so, it returns that
	 * the Bool sort defined in that theory. If not, returns the Bool sort
	 * defined in
	 * {@link org.eventb.smt.internal.ast.theories.SMTTheoryV2_0.Core}
	 * 
	 * @return a Bool sort
	 */
	private SMTSortSymbol getBoolSort() {
		boolean veriTBools = false;
		for (final SMTTheory theory : getLogic().getTheories()) {
			if (theory instanceof VeriTBooleansV2_0) {
				veriTBools = true;
			}
		}
		if (veriTBools) {
			return VeriTBooleansV2_0.getInstance().getBooleanSort();
		} else {
			return SMTTheoryV2_0.Core.getInstance().getBooleanSort();
		}
	}

	/**
	 * return the macros
	 * 
	 * @return the macros
	 */
	public SortedSet<SMTMacro> getMacros() {
		return macros;
	}

	/**
	 * Constructs a new instance of the signature
	 * 
	 * @param logic
	 *            the logic of the signature
	 */
	public SMTSignatureV2_0Verit(final SMTLogic logic) {
		super(logic);
		loadMacroSymbols();
	}

	private void loadMacroSymbols() {
		for (final SMTVeriTOperatorV2_0 op : SMTVeriTOperatorV2_0.values()) {
			names.add(op.getSymbol().getMacroName());
		}
		names.add("pair");
		names.add("Pair");
	}

	/**
	 * Add a macro into the signature
	 * 
	 * @param macro
	 *            the new macro that will be added to the signature
	 */
	public void addMacro(final SMTMacro macro) {
		if (!macros.contains(macro)) {
			macros.add(macro);
		}
	}

	/**
	 * Appends a string representation of the macros section to the
	 * stringbuilder
	 * 
	 * @param sb
	 */
	private void extramacrosSection(final StringBuilder sb) {
		for (final SMTMacro macro : macros) {
			sb.append("\n");
			sb.append("(define-fun ");
			macro.toString(sb, 0);
			sb.append(")");
		}
	}

	/**
	 * Calculate and return all the used ?-var names in the macros. It is
	 * necessary to create fresh ?-vars.
	 * 
	 * @param macros
	 *            the macros that contains the ?-vars to be taken
	 * @return the ?-vars names
	 */
	private static Set<String> getQNamesFromMacro(final Set<SMTMacro> macros) {
		final Set<String> macroNames = new HashSet<String>();
		for (final SMTMacro macro : macros) {
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

	/**
	 * It compares the name with all used names in ?-vars, and then return a
	 * fresh name
	 * 
	 * @param name
	 *            the name to be compared for fresh name
	 * @return a fresh name
	 */
	public String freshQVarName(final String name) {
		return freshQVarName(name, new HashSet<String>());
	}

	/**
	 * It compares the name with all used names in ?-vars, and then return a
	 * fresh name
	 * 
	 * @param name
	 *            the name to be compared for fresh name
	 * @param usedNames
	 *            other names that will be used in the comparison
	 * @return a fresh name
	 * 
	 */
	public String freshQVarName(final String name, final Set<String> usedNames) {
		final Set<String> additionalReservedNames = new HashSet<String>();
		additionalReservedNames.addAll(ms.getqSymbols());
		additionalReservedNames.addAll(getQNamesFromMacro(macros));
		additionalReservedNames.addAll(usedNames);
		return freshSymbolName(additionalReservedNames, name);
	}

	@Override
	public String freshSymbolName(final String name) {
		return freshSymbolName(new HashSet<String>(), name, NEW_FUNCTION_NAME);
	}

	/**
	 * Returns a set with all the name of all the macros in the signature.
	 * 
	 * @return a set with all the name of all the macros in the signature.
	 */
	private Set<String> getMacroNames() {
		final Set<String> macroNames = new HashSet<String>();
		for (final SMTMacro macro : macros) {
			macroNames.add(macro.getMacroName());
		}
		return macroNames;
	}

	/**
	 * Returns a fresh symbol name. It does not compare with ?-var names.
	 * 
	 * @param name
	 *            the name to be compared for a fresh symbol name
	 * @return a fresh symbol name
	 */
	@Override
	public String freshSymbolName(final Set<String> symbolNames,
			final String name, final String newSymbolName) {
		symbolNames.addAll(getSymbolNames(funs));
		symbolNames.addAll(getSymbolNames(sorts));
		symbolNames.addAll(getSymbolNames(preds));
		symbolNames.add("true");
		symbolNames.add("false");
		symbolNames.add("Bool");
		symbolNames.addAll(getMacroNames());
		return super.freshSymbolName(symbolNames, name, newSymbolName);
	}

	/**
	 * Appends to the StringBuilder the string representation of the logic
	 * section
	 * 
	 * @param builder
	 *            the StringBuilder
	 */
	private void logicSection(final StringBuilder builder) {
		final SMTSetLogicCommand setLogicCommand = new SMTSetLogicCommand(
				logic.getName());
		setLogicCommand.toString(builder);
		builder.append("\n");
	}

	/**
	 * Appends the string representation of this signature sorts (when they are
	 * not predefined) in SMT-LIB v2.0 version syntax. It doesn't modify the
	 * buffer if the set contains only predefined symbols.
	 * 
	 * @param builder
	 */
	private void sortDeclarations(final StringBuilder builder) {
		if (!sorts.isEmpty()) {
			final TreeSet<SMTSortSymbol> sortedSorts = new TreeSet<SMTSortSymbol>(
					sorts);
			SMTDeclareSortCommand command;
			for (final SMTSortSymbol sort : sortedSorts) {
				if (!sort.isPredefined()) {
					command = new SMTDeclareSortCommand(sort);
					command.toString(builder);
					builder.append("\n");
				}
			}
		}
	}

	/**
	 * Appends the string representation of this signature funs (when they are
	 * not predefined) in SMT-LIB v2.0 version syntax. It doesn't modify the
	 * buffer if the set contains only predefined symbols.
	 * 
	 * @param builder
	 */
	private static <SMTFunOrPredSymbol extends SMTSymbol> void funDeclarations(
			final StringBuilder builder, final Set<SMTFunOrPredSymbol> elements) {
		if (!elements.isEmpty()) {
			final TreeSet<SMTFunOrPredSymbol> sortedSymbols = new TreeSet<SMTFunOrPredSymbol>(
					elements);
			SMTDeclareFunCommand command;
			for (final SMTFunOrPredSymbol symbol : sortedSymbols) {
				if (!symbol.isPredefined()) {
					command = new SMTDeclareFunCommand(symbol);
					command.toString(builder);
					builder.append("\n");
				}
			}
		}
	}

	@Override
	public void toString(final StringBuilder sb) {
		logicSection(sb);
		if (printPairSortAndPairFunction) {
			sb.append("(declare-sort Pair 2)\n");
			sb.append("(declare-fun (par (s t) (pair s t (Pair s t))))");
			sb.append("\n");
		}
		if (printFstAndSndFunctions) {
			sb.append("(declare-fun (par (X Y) (fst (Pair X Y) X)))");
			sb.append("\n");
			sb.append("(declare-fun (par (X Y) (snd (Pair X Y) Y)))");
			sb.append("\n");
		}
		sortDeclarations(sb);
		funDeclarations(sb, preds);
		funDeclarations(sb, funs);
		extramacrosSection(sb);
	}

	/**
	 * Adds the fst and snd functions, as well as their defining assumptions.
	 * They are added only once.
	 */
	public void addFstAndSndAuxiliarFunctions() {
		if (!printFstAndSndFunctions) {
			funs.add(SMTFactoryVeriT.FST_SYMBOLV_2_0);
			funs.add(SMTFactoryVeriT.SND_SYMBOL_V2_0);
			printFstAndSndFunctions = true;
		}
	}

	@Override
	public void removeUnusedSymbols(final Set<SMTSymbol> symbols) {
		final Set<SMTFunctionSymbol> funSymbols = new HashSet<SMTFunctionSymbol>();
		final Set<SMTPredicateSymbol> predSymbols = new HashSet<SMTPredicateSymbol>();
		final Set<SMTSortSymbol> sortSymbols = new HashSet<SMTSortSymbol>();
		final Set<String> macroSymbols = new HashSet<String>();

		for (final SMTSymbol symbol : symbols) {
			if (symbol instanceof SMTFunctionSymbol) {
				funSymbols.add((SMTFunctionSymbol) symbol);
			} else if (symbol instanceof SMTPredicateSymbol) {
				predSymbols.add((SMTPredicateSymbol) symbol);
			} else if (symbol instanceof SMTSortSymbol) {
				sortSymbols.add((SMTSortSymbol) symbol);
			} else if (symbol instanceof SMTMacroSymbol) {
				macroSymbols.add(((SMTMacroSymbol) symbol).getName());
			}
		}

		for (final SMTMacro macro : macros) {
			if (macro instanceof SMTPredefinedMacro) {
				final SMTPredefinedMacro pmacro = (SMTPredefinedMacro) macro;
				for (final SMTMacro macroS : pmacro.getRequiredMacros()) {
					macroSymbols.add(macroS.getMacroName());
				}
			}
		}

		removeUnusedSymbols(funSymbols, predSymbols, sortSymbols, macroSymbols);
	}

	/**
	 * Remove unused symbols from signature
	 * 
	 * @param usedFuns
	 *            unused function symbols
	 * @param usedPreds
	 *            unused predicate symbols
	 * @param usedSorts
	 *            unused sort symbols
	 * @param usedMacros
	 *            unused macro symbols
	 */
	private void removeUnusedSymbols(final Set<SMTFunctionSymbol> usedFuns,
			final Set<SMTPredicateSymbol> usedPreds,
			final Set<SMTSortSymbol> usedSorts, final Set<String> usedMacros) {

		final Set<SMTFunctionSymbol> unusedFunctionSymbols = removeUnusedFunctions(usedFuns);
		final Set<SMTPredicateSymbol> unusedPredicateSymbols = removeUnusedPreds(usedPreds);
		final Set<SMTSortSymbol> unusedSortSymbols = removeUnusedSorts(usedSorts);
		final Set<SMTMacro> unusedMacroSymbols = removeUnusedMacros(usedMacros);

		if (unusedFunctionSymbols.isEmpty() && unusedPredicateSymbols.isEmpty()
				&& unusedSortSymbols.isEmpty() && unusedMacroSymbols.isEmpty()) {
			return;
		}
		removeUnusedSymbols(usedFuns, usedPreds, usedSorts, usedMacros);

	}

	/**
	 * remove unused macro symbols from signature
	 * 
	 * @param usedMacros
	 *            used macro symbols
	 * @return unused macro symbols
	 */
	private Set<SMTMacro> removeUnusedMacros(final Set<String> usedMacros) {
		final Set<SMTMacro> unusedMacros = new HashSet<SMTMacro>();

		for (final SMTMacro macro : macros) {
			if (!usedMacros.contains(macro.getMacroName())) {
				unusedMacros.add(macro);
			}
		}

		macros.removeAll(unusedMacros);

		return unusedMacros;
	}
}
