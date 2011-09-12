/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - additional methods
 *******************************************************************************/

package org.eventb.smt.ast;

import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.ast.symbols.SMTSymbol.BENCHMARK;
import static org.eventb.smt.ast.symbols.SMTSymbol.EQUAL;
import static org.eventb.smt.ast.symbols.SMTSymbol.LOGIC;
import static org.eventb.smt.ast.symbols.SMTSymbol.PREDEFINED;
import static org.eventb.smt.ast.symbols.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eventb.smt.ast.commands.SMTDeclareFunCommand;
import org.eventb.smt.ast.commands.SMTDeclareSortCommand;
import org.eventb.smt.ast.commands.SMTSetLogicCommand;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.translation.SMTLIBVersion;

/**
 * Here are the rules in SMT-LIB V1.2 that we need to implement in this class:
 * <ul>
 * 
 * <li><strong>(The actual solvers does not support overloading)</strong>
 * Explicit (ad-hoc) overloading of function or predicateSymbol symbols — by
 * which a symbol could have more than one rank — is allowed.</li>
 * <li><strong>(DONE) </strong>Every variable has a unique sort and no function
 * symbol has distinct ranks of the form <code>s1 ··· sn s</code> and
 * <code>s1 ··· sn s</code>.</li>
 * <li><strong>(The actual solvers does not support this too)</strong> The sets
 * <code>ΣS</code>, <code>ΣF</code> and <code>ΣP</code> of an SMT-LIB signature
 * are not required to be disjoint.</li>
 * <li>It is required for the set of attribute symbols to be disjoint from all
 * the other sets of the language.</li>
 * <li>The symbols <code>assumption</code>, <code>formula</code>,
 * <code>status</code>, <code>logic</code>, <code>extrasorts</code>,
 * <code>extrafuns</code>, <code>extrapreds</code>, <code>funs</code>,
 * <code>preds</code>, <code>axioms</code>, <code>sorts</code>,
 * <code>definition</code>, <code>theory</code>, <code>language</code>,
 * <code>extensions</code> and <code>notes</code> are reserved attribute
 * symbols.</li>
 * <li>Reserved symbols and keywords are: <code>=</code>, <code>and</code>,
 * <code>benchmark</code>, <code>distinct</code>, <code>exists</code>,
 * <code>false</code>, <code>flet</code>, <code>forall</code>,
 * <code>if_then_else</code>, <code>iff</code>, <code>implies</code>,
 * <code>ite</code>, <code>let</code>, <code>logic</code>, <code>not</code>,
 * <code>or</code>, <code>sat</code>, <code>theory</code>, <code>true</code>,
 * <code>unknown</code>, <code>unsat</code>, <code>xor</code>.</li>
 * </ul>
 */
// TODO Create two subclasses when hanging SMT-LIB 2.0: SMTSignatureV1_2 and
// SMTSignature2_0. This might be necessary if naming rules are not the same in
// the two versions of the language.
public abstract class SMTSignature {
	protected final SMTLIBVersion smtlibVersion;

	/**
	 * The logic of the signature
	 */
	protected final SMTLogic logic;

	private final static String NEW_SORT_NAME = "NS";
	private final static String NEW_FUNCTION_NAME = "nf";
	private final static String NEW_PREDICATE_NAME = "np";
	private final static String NEW_SYMBOL_NAME = "ns";

	/**
	 * reserved symbols and keywords
	 */
	private final static Set<String> reservedSymbols = getReservedSymbolsAndKeywords();

	/**
	 * Predefined attribute symbols
	 */
	private final static String predefinedAttributesSymbols[] = { "assumption",
			"formula", "status", "logic", "extrasorts", "extrafuns",
			"extrapreds", "funs", "preds", "axioms", "sorts", "definition",
			THEORY, "language", "extensions", "notes" };

	/**
	 * attribute symbols of the signature
	 */
	private final Set<String> attributeSymbols = new HashSet<String>(
			Arrays.asList(predefinedAttributesSymbols));

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

	/**
	 * This method returns a set with the reserved symbols and keywords
	 * 
	 * @return the reserved symbols and keyboards.
	 */
	public static Set<String> getReservedSymbolsAndKeywords() {
		final List<String> reservedSymbolsAndKeywords = new ArrayList<String>(
				Arrays.asList(EQUAL, "and", BENCHMARK, "distinct", "false",
						"flet", "if_then_else", "iff", "implies", "ite", "let",
						LOGIC, "not", "or", "sat", THEORY, "true", "unknown",
						"unsat", "xor"));
		final boolean successfullyAddedReservedSymbolsAndKeywords = reservedSymbolsAndKeywords
				.addAll(SMTConnective.getConnectiveSymbols())
				&& reservedSymbolsAndKeywords.addAll(SMTQuantifierSymbol
						.getQuantifierSymbols());
		assert successfullyAddedReservedSymbolsAndKeywords;
		return new HashSet<String>(reservedSymbolsAndKeywords);
	}

	/**
	 * Creates and returns a messge describing the exception message. It is used
	 * when a rank check fails.
	 * 
	 * @param actualSymbol
	 *            the actual symbol
	 * @param expectedSymbol
	 *            the expected symbol
	 * @return the exception message
	 */
	private static String makeIncompatibleSymbolExceptionMessage(
			final SMTSymbol actualSymbol, final SMTSymbol expectedSymbol) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Sorts of the actual symbol: ");
		sb.append(actualSymbol);
		sb.append(" does not match the expected symbol:");
		sb.append(expectedSymbol);
		return sb.toString();
	}

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
	 * Returns a string containing as much space characters as needed to indent
	 * the next line according to the length of the name of the section
	 */
	private static String sectionIndentation(final String sectionName) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < sectionName.length(); i++) {
			sb.append(" ");
		}
		sb.append("    ");
		return sb.toString();
	}

	/**
	 * This method appends the string representation of the given SMT-LIB
	 * section by appending the string representation of each SMT-LIB symbols
	 * that is not predefined in the current logic. It doesn't modify the buffer
	 * if the set contains only predefined symbols.
	 * 
	 * @param <T>
	 *            The type of SMT-LIB symbols to append to the buffer.
	 * @param sb
	 *            the buffer to complete.
	 * @param elements
	 *            the symbols to append to the buffer.
	 * @param sectionName
	 *            the name of the SMT-LIB section this buffer represents.
	 */
	private static <T extends SMTSymbol> void extraSection(
			final StringBuilder sb, final Set<T> elements,
			final String sectionName) {
		final String eltSep = sectionIndentation(sectionName);
		boolean emptySection = true;
		final StringBuilder sectionStarting = new StringBuilder();
		sectionStarting.append(" :");
		sectionStarting.append(sectionName);
		sectionStarting.append(" (");
		String separator = sectionStarting.toString();
		final TreeSet<T> sortedElements = new TreeSet<T>(elements);
		for (final T element : sortedElements) {
			if (!element.isPredefined()) {
				sb.append(separator);
				element.toString(sb);
				separator = eltSep;
				emptySection = false;
			}
		}
		if (!emptySection) {
			sb.append(")\n");
		}
	}

	/**
	 * Appends the string representation of this signature sorts (when they are
	 * not predefined) in SMT-LIB v2.0 version syntax. It doesn't modify the
	 * buffer if the set contains only predefined symbols.
	 * 
	 * @param builder
	 */
	private void sortDeclarations(final StringBuilder builder) {
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

	/**
	 * Appends the string representation of this signature funs (when they are
	 * not predefined) in SMT-LIB v2.0 version syntax. It doesn't modify the
	 * buffer if the set contains only predefined symbols.
	 * 
	 * @param builder
	 */
	private static <SMTFunOrPredSymbol extends SMTSymbol> void funDeclarations(
			final StringBuilder builder, final Set<SMTFunOrPredSymbol> elements) {
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
	private String freshName(final Set<String> additionalReservedNames,
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

	/**
	 * Appends to the StringBuilder the string representation of the logic
	 * section
	 * 
	 * @param builder
	 *            the StringBuilder
	 */
	private void logicSection(final StringBuilder builder) {
		switch (smtlibVersion) {
		case V1_2:
			builder.append(" :logic ");
			builder.append(logic.getName());
			builder.append("\n");
			break;

		default:
			final SMTSetLogicCommand setLogicCommand = new SMTSetLogicCommand(
					logic.getName());
			setLogicCommand.toString(builder);
			builder.append("\n");
			break;
		}
	}

	/**
	 * One sort per line. May add a comment beside.
	 */
	private void extrasortsSection(final StringBuilder builder) {
		if (!sorts.isEmpty()) {
			switch (smtlibVersion) {
			case V1_2:
				extraSection(builder, sorts, "extrasorts");
				break;

			default:
				sortDeclarations(builder);
				break;
			}
		}
	}

	/**
	 * Appends to the StringBuilder the string representation of the extrapreds
	 * section
	 * 
	 * @param builder
	 */
	private void extrapredsSection(final StringBuilder builder) {
		if (!preds.isEmpty()) {
			switch (smtlibVersion) {
			case V1_2:
				extraSection(builder, preds, "extrapreds");
				break;

			default:
				funDeclarations(builder, preds);
				break;
			}
		}
	}

	/**
	 * Appends to the StringBuilder the string representation of the extrafuns
	 * section
	 * 
	 * @param builder
	 */
	private void extrafunsSection(final StringBuilder builder) {
		if (!funs.isEmpty()) {
			switch (smtlibVersion) {
			case V1_2:
				extraSection(builder, funs, "extrafuns");
				break;

			default:
				funDeclarations(builder, funs);
				break;
			}
		}
	}

	private void loadReservedAndPredefinedSymbols() {
		names.addAll(reservedSymbols);
		names.addAll(Arrays.asList(predefinedAttributesSymbols));
		names.add("U"); // predefined sort in Empty theory
	}

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

	private String freshSymbolName(final Set<String> symbolNames,
			final String name, final String newSymbolName) {
		final String freshName;
		/**
		 * Avoids creating names similar to reserved symbols, predefined symbols
		 * or keywords
		 */
		if (reservedSymbols.contains(name) || attributeSymbols.contains(name)) {
			freshName = freshName(symbolNames, newSymbolName);
		} else {
			freshName = freshName(symbolNames, name);
		}

		names.add(freshName);

		return freshName;
	}

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

				if (!verifyRank(symbol.getArgSorts(), predSymbol.getArgSorts())) {
					throw new IllegalArgumentException(
							makeIncompatibleSymbolExceptionMessage(symbol,
									predSymbol));
				}
				return;
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

				final boolean wellSorted;
				if (symbol.isAssociative()) {
					wellSorted = verifyAssociativeRank(expectedArgSorts[0],
							argSorts);
				} else {
					wellSorted = verifyRank(expectedArgSorts, argSorts);
				}
				if (!wellSorted) {
					throw new IllegalArgumentException(
							makeIncompatibleSymbolExceptionMessage(
									functionSymbol, symbol));
				}
				return;
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
			final SMTSortSymbol[] argSorts, final SMTSortSymbol returnSort) {
		final String freshName = freshFunctionName(name);
		final SMTFunctionSymbol freshConstant = new SMTFunctionSymbol(
				freshName, argSorts, returnSort, !ASSOCIATIVE, !PREDEFINED,
				smtlibVersion);
		final boolean successfullyAdded = funs.add(freshConstant);
		if (!successfullyAdded) {
			throw new IllegalArgumentException(
					Messages.FreshSymbolCreationFailed
							+ freshConstant.toString());
		}
		return freshConstant;
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
		return freshFunctionSymbol(name, new SMTSortSymbol[0], sort);
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
	public void toString(final StringBuilder sb) {
		logicSection(sb);
		extrasortsSection(sb);
		extrapredsSection(sb);
		extrafunsSection(sb);
	}
}
