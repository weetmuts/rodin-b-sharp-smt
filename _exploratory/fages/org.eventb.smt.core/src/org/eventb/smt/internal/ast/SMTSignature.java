/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - additional methods
 *******************************************************************************/

package org.eventb.smt.internal.ast;

import static org.eventb.smt.internal.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.internal.ast.symbols.SMTSymbol.PREDEFINED;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.internal.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.internal.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.internal.ast.symbols.SMTSortSymbol;
import org.eventb.smt.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.internal.ast.theories.SMTLogic;

public abstract class SMTSignature {
	protected final SMTLIBVersion smtlibVersion;

	/**
	 * The logic of the signature
	 */
	protected final SMTLogic logic;

	private final static String NEW_SORT_NAME = "NS";
	protected final static String NEW_FUNCTION_NAME = "nf";
	private final static String NEW_PREDICATE_NAME = "np";
	private final static String NEW_SYMBOL_NAME = "ns";

	/**
	 * Sorts of the signature
	 */
	protected final Set<SMTSortSymbol> sorts = new HashSet<SMTSortSymbol>();

	/**
	 * predicates of the signature
	 */
	protected final Set<SMTPredicateSymbol> preds = new HashSet<SMTPredicateSymbol>();

	/**
	 * functions of the signature
	 */
	protected final Set<SMTFunctionSymbol> funs = new HashSet<SMTFunctionSymbol>();

	/**
	 * names of all symbols of the signature
	 */
	protected Set<String> names = new HashSet<String>();

	/**
	 * Construts a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignature(final SMTLogic logic, final SMTLIBVersion smtlibVersion) {
		this.smtlibVersion = smtlibVersion;
		this.logic = logic;
		loadReservedAndPredefinedSymbols();
		loadLogicSymbols();
	}

	// TODO add an abstract Set<String> getReservedSymbolsAndKeywords(); method
	// ?

	/**
	 * verify rank of a symbol(function or predicate) , assuming that the symbol
	 * is associative.
	 * 
	 * @param expectedSort
	 *            the expected sort of all arguments
	 * @param sorts
	 *            the actual sorts
	 * @return true if all the sorts are compatible with the expected sort,
	 *         false otherwise.
	 */
	private static boolean verifyAssociativeRank(
			final SMTSortSymbol expectedSort, final SMTSortSymbol[] sorts) {
		for (final SMTSortSymbol sort : sorts) {
			if (!sort.isCompatibleWith(expectedSort)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * verify rank of symbol (function or predicate), assuming that the symbol
	 * is not associative.
	 * 
	 * @param expectedSorts
	 *            the expected sorts
	 * @param sorts
	 *            the actual sorts
	 * @return if each actual sort is compatible with its respective expected
	 *         sort.
	 */
	private static boolean verifyRank(final SMTSortSymbol[] expectedSorts,
			final SMTSortSymbol[] sorts) {
		if (expectedSorts.length != sorts.length) {
			return false;
		}
		for (int i = 0; i < sorts.length; i++) {
			if (!expectedSorts[i].isCompatibleWith(sorts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gives a fresh symbol name. Implements SMT-LIB rules. If the symbol name
	 * contains "\'", it is replaced with "_" + i + "_", where i is an arbitrary
	 * number , incremented as much as needed. If the symbol name already exists
	 * in the symbols set, a new name is created, that is: original_name + "_" +
	 * i, where i is incremented as much as needed.
	 * 
	 * Remark about the \' symbol in the solvers:
	 * 
	 * The solvers cvc3, veriT and alt-ergo and z3 accepts the symbol, but it
	 * cannot be the first character of the word
	 * 
	 * z3 accepts the symbol and it can be used anywhere in the word (or just
	 * itself).
	 * 
	 */
	protected String freshName(final Set<String> additionalReservedNames,
			final String name) {
		int i = 0;
		final StringBuilder freshName = new StringBuilder(name);

		final int basenameLength = freshName.length();
		/**
		 * If the set already contains this symbol
		 */
		while (names.contains(freshName.toString())
				|| additionalReservedNames.contains(freshName.toString())) {
			/**
			 * Sets the buffer content to: name + i.
			 */
			freshName.setLength(basenameLength);
			freshName.append(i);

			i = i + 1;
		}

		return freshName.toString();
	}

	abstract void loadReservedAndPredefinedSymbols();

	/**
	 * Adds in the signature the predicates, functions and sort symbols from the
	 * loaded logic
	 */
	private void loadLogicSymbols() {
		sorts.addAll(logic.getSorts());
		names.addAll(getSymbolNames(sorts));
		preds.addAll(logic.getPredicates());
		names.addAll(getSymbolNames(preds));
		funs.addAll(logic.getFunctions());
		names.addAll(getSymbolNames(funs));
	}

	/**
	 * Remove unused symbols from signature
	 * 
	 * @param usedFuns
	 *            used functions
	 * @param usedPreds
	 *            used predicates
	 * @param usedSorts
	 *            used sorts
	 */
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

	/**
	 * Remove unused sorts from signature.
	 * 
	 * @param usedSorts
	 *            used sorts
	 * @return a list with unused sorts
	 */
	protected Set<SMTSortSymbol> removeUnusedSorts(
			final Set<SMTSortSymbol> usedSorts) {
		final Set<SMTSortSymbol> unusedSortSymbols = new HashSet<SMTSortSymbol>();
		final Set<SMTSortSymbol> declUsedSorts = new HashSet<SMTSortSymbol>();

		for (final SMTFunctionSymbol fun : funs) {
			declUsedSorts.add(fun.getResultSort());
			declUsedSorts.addAll(Arrays.asList(fun.getArgSorts()));
		}

		for (final SMTPredicateSymbol pred : preds) {
			declUsedSorts.addAll(Arrays.asList(pred.getArgSorts()));
		}

		for (final SMTSortSymbol symbol : sorts) {
			if (!usedSorts.contains(symbol) && !declUsedSorts.contains(symbol)) {
				unusedSortSymbols.add(symbol);
			}
		}

		sorts.removeAll(unusedSortSymbols);
		return unusedSortSymbols;
	}

	/**
	 * Remove unused predicates from signature
	 * 
	 * @param usedPreds
	 *            used predicates
	 * @return unused predicates
	 */
	protected Set<SMTPredicateSymbol> removeUnusedPreds(
			final Set<SMTPredicateSymbol> usedPreds) {
		final Set<SMTPredicateSymbol> unusedPredicateSymbols = new HashSet<SMTPredicateSymbol>();
		for (final SMTPredicateSymbol symbol : preds) {
			if (!usedPreds.contains(symbol)) {
				unusedPredicateSymbols.add(symbol);
			}
		}
		preds.removeAll(unusedPredicateSymbols);
		return unusedPredicateSymbols;
	}

	/**
	 * Remove unused functions from signature
	 * 
	 * @param usedFuns
	 *            used functions
	 * @return unused functions
	 */
	protected Set<SMTFunctionSymbol> removeUnusedFunctions(
			final Set<SMTFunctionSymbol> usedFuns) {
		final Set<SMTFunctionSymbol> unusedFunctionSymbols = new HashSet<SMTFunctionSymbol>();
		for (final SMTFunctionSymbol symbol : funs) {
			if (!usedFuns.contains(symbol)) {
				unusedFunctionSymbols.add(symbol);
			}
		}
		funs.removeAll(unusedFunctionSymbols);
		return unusedFunctionSymbols;
	}

	/**
	 * This method is used to get the symbol names already in use from a set of
	 * SMT-LIB symbols
	 */
	protected static Set<String> getSymbolNames(
			final Set<? extends SMTSymbol> symbols) {
		final Set<String> symbolNames = new HashSet<String>();
		for (final SMTSymbol symbol : symbols) {
			symbolNames.add(symbol.getName());
		}
		return symbolNames;
	}

	abstract String freshSymbolName(final Set<String> symbolNames,
			final String name, final String newSymbolName);

	/**
	 * Returns a fresh symbol name.
	 * 
	 * @param symbolNames
	 *            the reserved symbol names.
	 * @param name
	 *            the base name for the fresh name. That is, the fresh name can
	 *            be the same string or the same string + a numeral.
	 * @return a fresh name.
	 */
	protected String freshSymbolName(final Set<String> symbolNames,
			final String name) {
		return freshSymbolName(symbolNames, name, NEW_SYMBOL_NAME);
	}

	protected String freshSortName(final Set<String> symbolNames,
			final String name) {
		return freshSymbolName(symbolNames, name, NEW_SORT_NAME);
	}

	protected String freshFunctionName(final Set<String> symbolNames,
			final String name) {
		return freshSymbolName(symbolNames, name, NEW_FUNCTION_NAME);
	}

	protected String freshPredicateName(final Set<String> symbolNames,
			final String name) {
		return freshSymbolName(symbolNames, name, NEW_PREDICATE_NAME);
	}

	/**
	 * returns the sorts in the signature
	 * 
	 * @return the sorts in the signature
	 */
	public Set<SMTSortSymbol> getSorts() {
		return sorts;
	}

	/**
	 * returns the preds in the signature
	 * 
	 * @return the preds in the signature
	 */
	public Set<SMTPredicateSymbol> getPreds() {
		return preds;
	}

	/**
	 * returns the functions in the signature
	 * 
	 * @return the functions in the signature
	 */
	public Set<SMTFunctionSymbol> getFuns() {
		return funs;
	}

	/**
	 * Returns the version of SMTLIB used by the signature
	 * 
	 * @return the version of SMTLIB
	 */
	public SMTLIBVersion getSMTLIBVersion() {
		return smtlibVersion;
	}

	/**
	 * Returns the logic used by the signature
	 * 
	 * @return the logic of the signature
	 */
	public SMTLogic getLogic() {
		return logic;
	}

	/**
	 * /** checks if the actual predicate symbol has the same rank of the
	 * function stored in the signature.
	 * 
	 * @param symbol
	 *            the predicate symbol that will be checked.
	 */
	public void verifyPredicateSignature(final SMTPredicateSymbol symbol) {
		for (final SMTPredicateSymbol predSymbol : preds) {

			// Verify if the predicates have the same name
			if (symbol.getName().equals(predSymbol.getName())) {

				if (verifyRank(symbol.getArgSorts(), predSymbol.getArgSorts())) {
					return;
				}
			}
		}
		throw new IllegalArgumentException("Predicate " + symbol
				+ " is not declared in the signature.");
	}

	/**
	 * checks if the actual function symbol has the same rank of the function
	 * stored in the signature.
	 * 
	 * @param functionSymbol
	 *            the function symbol that will be checked.
	 */
	public void verifyFunctionSignature(final SMTFunctionSymbol functionSymbol) {
		for (final SMTFunctionSymbol symbol : funs) {

			// Verify if the predicates have the same name
			if (functionSymbol.getName().equals(symbol.getName())) {

				final SMTSortSymbol[] expectedArgSorts = symbol.getArgSorts();
				final SMTSortSymbol[] argSorts = functionSymbol.getArgSorts();

				if (symbol.isAssociative()) {
					if (verifyAssociativeRank(expectedArgSorts[0], argSorts))
						return;
				} else {
					if (verifyRank(expectedArgSorts, argSorts))
						return;
				}
			}
		}
		throw new IllegalArgumentException("Function " + functionSymbol
				+ " is not declared in the signature.");
	}

	/**
	 * Returns a fresh symbol name. The checked symbols for adding the new
	 * symbol are funs, sorts and preds.
	 * 
	 * @param name
	 *            a base name for a fresh symbol name
	 * @return a fresh symbol name
	 */
	public String freshSymbolName(final String name) {
		return freshSymbolName(new HashSet<String>(), name);
	}

	public String freshSortName(final String name) {
		return freshSortName(new HashSet<String>(), name);
	}

	public String freshPredicateName(final String name) {
		return freshPredicateName(new HashSet<String>(), name);
	}

	public String freshFunctionName(final String name) {
		return freshFunctionName(new HashSet<String>(), name);
	}

	/**
	 * Returns a fresh name for the benchmark
	 */
	public String freshBenchmarkName(final String basename) {
		final String freshName = freshName(new HashSet<String>(), basename);
		names.add(freshName);
		return freshName;
	}

	/**
	 * Returns a fresh function symbol.
	 * 
	 * @param name
	 *            a name for the fresh symbol. It creates a fresh name for the
	 *            function.
	 * @param argSorts
	 *            the argument sorts of the function
	 * @param returnSort
	 *            the return sort of the function
	 * @return a fresh function symbol.
	 */
	public SMTFunctionSymbol freshFunctionSymbol(final String name,
			final SMTSortSymbol[] argSorts, final SMTSortSymbol returnSort,
			final boolean associative) {
		final String freshName = freshFunctionName(name);
		final SMTFunctionSymbol freshSymbol = new SMTFunctionSymbol(freshName,
				argSorts, returnSort, associative, !PREDEFINED, smtlibVersion);
		final boolean successfullyAdded = funs.add(freshSymbol);
		if (!successfullyAdded) {
			throw new IllegalArgumentException(
					Messages.FreshSymbolCreationFailed + freshSymbol.toString());
		}
		return freshSymbol;
	}

	/**
	 * Creates and returns a fresh constant
	 * 
	 * @param name
	 *            the name of the fresh constant.
	 * @param sort
	 *            the sort of the fresh constant.
	 * @return a fresh function symbol.
	 */
	public SMTFunctionSymbol freshConstant(final String name,
			final SMTSortSymbol sort) {
		return freshFunctionSymbol(name, new SMTSortSymbol[0], sort,
				!ASSOCIATIVE);
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	public SMTSortSymbol freshSort(final String name) {
		final String freshName = freshSortName(name);
		final SMTSortSymbol freshSort = new SMTSortSymbol(freshName,
				!SMTSymbol.PREDEFINED, smtlibVersion);
		final boolean successfullyAdded = sorts.add(freshSort);
		if (!successfullyAdded) {
			throw new IllegalArgumentException(
					Messages.FreshSymbolCreationFailed + freshSort.toString());
		}
		return freshSort;
	}

	/**
	 * adds a predicate symbol to the signature
	 * 
	 * @param name
	 *            the name of the predicate symbol
	 * @param argSorts
	 *            the sort arguments.
	 * @return the just added predicate symbol to the signature
	 */
	public SMTPredicateSymbol freshPredicateSymbol(final String name,
			final SMTSortSymbol... argSorts) {
		final String freshName = freshPredicateName(name);
		final SMTPredicateSymbol freshPredicate = new SMTPredicateSymbol(
				freshName, argSorts, !SMTSymbol.PREDEFINED, smtlibVersion);
		final boolean successfullyAdded = preds.add(freshPredicate);
		if (!successfullyAdded) {
			throw new IllegalArgumentException(
					Messages.FreshSymbolCreationFailed
							+ freshPredicate.toString());
		}
		return freshPredicate;
	}

	/**
	 * Remove unused symbols
	 * 
	 * @param symbols
	 *            the found symbols in the benchmark
	 */
	public void removeUnusedSymbols(final Set<SMTSymbol> symbols) {
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
		}
		removeUnusedSymbols(funSymbols, predSymbols, sortSymbols);
	}

	/**
	 * Appends to the StringBuilder the string representation of the signature
	 * 
	 * @param sb
	 *            the StringBuilder
	 */
	abstract public void toString(final StringBuilder sb);
}
