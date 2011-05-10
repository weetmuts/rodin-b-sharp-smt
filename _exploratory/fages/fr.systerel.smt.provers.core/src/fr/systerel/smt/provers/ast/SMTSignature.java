/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFunctionSymbol.ASSOCIATIVE;
import static fr.systerel.smt.provers.ast.SMTSymbol.LOGIC;
import static fr.systerel.smt.provers.ast.SMTSymbol.PREDEFINED;
import static fr.systerel.smt.provers.ast.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Here are the rules in SMT-LIB V1.2 that we need to implement in this class:
 * <ul>
 * 
 * <li><strong>(The actual solvers does not support overloading)</strong>
 * Explicit (ad-hoc) overloading of function or predicate symbols — by which a
 * symbol could have more than one rank — is allowed.</li>
 * <li><strong>TODO: Implement this test: </strong>Every variable has a unique
 * sort and no function symbol has distinct ranks of the form
 * <code>s1 ··· sn s</code> and <code>s1 ··· sn s</code>.</li>
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

	/**
	 * The logic of the signature
	 */
	protected final SMTLogic logic;

	protected final static String NEW_SYMBOL_NAME = "NSYMB";
	private final static String NEW_SORT_NAME = "NSORT";

	/**
	 * reserved symbols of the logic
	 */
	protected final static Set<String> reservedSymbols = getReservedSymbolsAndKeywords();

	/**
	 * Predefined attribute symbols
	 */
	protected final static String predefinedAttributesSymbols[] = {
			"assumption", "formula", "status", "logic", "extrasorts",
			"extrafuns", "extrapreds", "funs", "preds", "axioms", "sorts",
			"definition", THEORY, "language", "extensions", "notes" };

	/**
	 * attribute symbols of the signature
	 */
	protected final Set<String> attributeSymbols = new HashSet<String>(
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
	 * Construts a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignature(final SMTLogic logic) {
		this.logic = logic;
		loadLogicSymbols();
	}

	// TODO Refactor this method
	public void verifyPredicateSignature(
			final SMTPredicateSymbol predicateSymbol) {
		if (predicateSymbol.getName().equals(SMTFactory.PTRUE.getName())
				|| predicateSymbol.getName()
						.equals(SMTFactory.PFALSE.getName())) {
			return;
		}
		for (final SMTPredicateSymbol predSymbol : preds) {

			// Verify if the predicates have the same name
			if (predicateSymbol.getName().equals(predSymbol.getName())) {

				final SMTSortSymbol[] argSorts = predicateSymbol.getArgSorts();
				final SMTSortSymbol[] expectedArgSorts = predSymbol
						.getArgSorts();

				// Verify if the number of arguments are the same
				if (expectedArgSorts.length == argSorts.length) {

					// Verify each argument sort
					for (int i = 0; i < expectedArgSorts.length; i++) {
						if (expectedArgSorts[i] instanceof SMTPolymorphicSortSymbol) {
							continue;
						}
						if (!expectedArgSorts[i].equals(argSorts[i])) {
							throw new IllegalArgumentException(
									makeIncompatiblePredicatesExceptionMessage(
											predicateSymbol, predSymbol));
						}
					}
					return;
				}
			}
		}
		throw new IllegalArgumentException("Predicate " + predicateSymbol
				+ " is not declared in the signature.");
	}

	// TODO: Refactor this method
	public void verifyFunctionSignature(final SMTFunctionSymbol functionSymbol) {
		for (final SMTFunctionSymbol symbol : funs) {

			// Verify if the predicates have the same name
			if (functionSymbol.getName().equals(symbol.getName())) {

				final SMTSortSymbol[] expectedArgSorts = symbol.getArgSorts();
				final SMTSortSymbol[] argSorts = functionSymbol.getArgSorts();

				// Verify if the function is associative. If yes, all the
				// arguments of the sort of functionSymbol shall be the same.
				if (symbol.isAssociative()) {
					for (final SMTSortSymbol argSort : argSorts) {
						if (!argSort.equals(expectedArgSorts[0])) {
							throw makeIncompatibleFunctionsException(
									functionSymbol, symbol);
						}
					}
					return;
				}

				// If it's not associative, verify if the number of arguments
				// are the same
				if (expectedArgSorts.length == argSorts.length) {

					// Verify each argument sort
					for (int i = 0; i < expectedArgSorts.length; i++) {
						if (expectedArgSorts[i] instanceof SMTPolymorphicSortSymbol) {
							continue;
						}
						if (!expectedArgSorts[i].equals(argSorts[i])) {
							throw makeIncompatibleFunctionsException(
									functionSymbol, symbol);
						}
					}
					return;
				}
			}
		}
		throw new IllegalArgumentException("Function " + functionSymbol
				+ " is not declared in the signature.");
	}

	// TODO: Refactor this method
	private static IllegalArgumentException makeIncompatibleFunctionsException(
			final SMTFunctionSymbol actualFunctionSymbol,
			final SMTFunctionSymbol expectedSymbol) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Arguments of function symbol: ");
		sb.append(expectedSymbol);
		sb.append(": ");
		String sep = "";
		for (final SMTSortSymbol expectedArg : expectedSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			expectedArg.toString(sb);
		}
		sb.append(" does not match the arguments: ");
		for (final SMTSortSymbol arg : actualFunctionSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			arg.toString(sb);
		}
		sb.append(" in the declaration of function in the signature.");
		return new IllegalArgumentException(sb.toString());
	}

	// TODO: Refactor this method
	private static String makeIncompatiblePredicatesExceptionMessage(
			final SMTPredicateSymbol actualPredicateSymbol,
			final SMTPredicateSymbol expectedPredSymbol) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Terms of function symbol: ");
		sb.append(expectedPredSymbol);
		sb.append(": ");
		String sep = "";
		for (final SMTSortSymbol expectedArg : expectedPredSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			expectedArg.toString(sb);
		}
		sb.append(" does not match: ");
		for (final SMTSortSymbol arg : actualPredicateSymbol.getArgSorts()) {
			sb.append(sep);
			sep = " ";
			arg.toString(sb);
		}
		sb.append(" in the declaration of predicate in the signature.");
		return sb.toString();
	}

	/**
	 * This method returns a set with the reserved symbols and keywords
	 * 
	 * @return the reserved symbols and keyboards.
	 */
	private static Set<String> getReservedSymbolsAndKeywords() {
		final List<String> reservedSymbolsAndKeywords = new ArrayList<String>(
				Arrays.asList(SMTSymbol.EQUAL, "and", SMTSymbol.BENCHMARK,
						"distinct", "false", "flet", "if_then_else", "iff",
						"implies", "ite", "let", LOGIC, "not", "or", "sat",
						THEORY, "true", "unknown", "unsat", "xor"));
		if (!reservedSymbolsAndKeywords.addAll(SMTConnective
				.getConnectiveSymbols())
				|| !reservedSymbolsAndKeywords.addAll(SMTQuantifierSymbol
						.getQuantifierSymbols())) {
			// TODO throw new exception
		}
		return new HashSet<String>(reservedSymbolsAndKeywords);
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
	 * Adds in the signature the predicates, functions and sort symbols from the
	 * loaded logic
	 */
	private void loadLogicSymbols() {
		sorts.addAll(logic.getSorts());
		preds.addAll(logic.getPredicates());
		funs.addAll(logic.getFunctions());
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
	protected static <T extends SMTSymbol> void extraSection(
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
	 * Gives a fresh symbol name. Implements SMT-LIB rules. If the symbol name
	 * contains "\'", it is replaced with "_" + i + "_", where i is an arbitrary
	 * number , incremented as much as needed. If the symbol name already exists
	 * in the symbols set, a new name is created, that is: original_name + "_" +
	 * i, where i is incremented as much as needed.
	 */
	// TODO check which prover needs the "\'" simplification, and document it
	// here
	protected static String freshName(final Set<String> symbols,
			final String name) {
		int i = 0;
		final StringBuilder freshName = new StringBuilder(name);

		// To avoid the sort U problem
		symbols.add("U");

		if (name.contains("\'")) {
			final StringBuilder patch = new StringBuilder();
			/**
			 * Arbitrary chosen initial number
			 */
			int discrNumber = name.length() - name.indexOf('\'');

			patch.append("_").append(discrNumber).append("_");

			freshName.setLength(0);
			freshName.append(name.replaceAll("'", patch.toString()));

			while (symbols.contains(freshName.toString())) {
				discrNumber = discrNumber + 1;

				patch.setLength(1);
				patch.append(discrNumber).append("_");

				freshName.setLength(0);
				freshName.append(name.replaceAll("'", patch.toString()));
			}
		}

		final String intermediateName = freshName.toString();
		/**
		 * If the set already contains this symbol
		 */
		while (symbols.contains(freshName.toString())) {
			/**
			 * Sets the buffer content to: name + "_" + i.
			 */
			freshName.setLength(intermediateName.length());
			freshName.append("_").append(i);

			i = i + 1;
		}

		return freshName.toString();
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
	 * gets a function symbol according to the parameters. Otherwise, it returns
	 * null.
	 * 
	 * @param name
	 *            the name of the function symbol.
	 * @param argSorts
	 *            the sorts of the arguments.
	 * @param resultSort
	 *            the result sort of the function symbol.
	 * @return the function symbol.
	 */
	public SMTFunctionSymbol getFunctionSymbol(final String name,
			final SMTSortSymbol[] argSorts, final SMTSortSymbol resultSort) {
		for (final SMTFunctionSymbol fun : funs) {
			if (fun.name.equals(name) && fun.hasRank(argSorts, resultSort)) {
				return fun;
			}
		}
		return null;
	}

	/**
	 * gets a predicate symbol according to the parameters. Otherwise, it
	 * returns null.
	 * 
	 * @param name
	 *            the name of the predicate symbol.
	 * @param argSorts
	 *            the argument sorts.
	 * @return the predicate symbol.
	 */
	public SMTPredicateSymbol getPredicateSymbol(final String name,
			final SMTSortSymbol[] argSorts) {
		for (final SMTPredicateSymbol pred : preds) {
			if (pred.name.equals(name) && pred.hasRank(argSorts)) {
				return pred;
			}
		}
		return null;
	}

	/**
	 * This constant is used to name membership predicates.
	 */
	protected final static String MS_PREDICATE_NAME = "MS";

	/**
	 * returns a fresh predicate name.
	 * 
	 * @return a fresh predicate name.
	 */
	public String freshPredName() {
		final Set<String> names = new HashSet<String>();
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(preds));
		names.addAll(getSymbolNames(sorts));
		return freshName(names, MS_PREDICATE_NAME);
	}

	/**
	 * returns a fresh constante name.
	 * 
	 * @param the
	 *            base name for the new constant name.
	 * @return {@code name} if there is no element defined with the same name,
	 *         {@code name} + a numeral otherwise.
	 */
	public String freshCstName(final String name) {
		final Set<String> names = new HashSet<String>();
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(sorts));
		names.addAll(getSymbolNames(preds));
		if (reservedSymbols.contains(name) || attributeSymbols.contains(name)) {
			return freshName(names, NEW_SYMBOL_NAME);
		} else {
			return freshName(names, name);
		}
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
	public String freshSymbolName(final Set<String> symbolNames,
			final String name) {
		if (reservedSymbols.contains(name) || attributeSymbols.contains(name)) {
			return freshName(symbolNames, NEW_SYMBOL_NAME);
		} else {
			return freshName(symbolNames, name);
		}
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
		final String freshName = freshCstName(name);
		return new SMTFunctionSymbol(freshName, sort, !ASSOCIATIVE, !PREDEFINED);
	}

	/**
	 * returns a fresh sort.
	 * 
	 * @return a fresh sort.
	 */
	public SMTSortSymbol freshSort() {
		return freshSort(NEW_SORT_NAME);
	}

	/**
	 * Gives a fresh sort
	 * 
	 * @param name
	 */
	public SMTSortSymbol freshSort(final String name) {
		final Set<String> names = new HashSet<String>();
		names.addAll(getSymbolNames(funs));
		names.addAll(getSymbolNames(sorts));
		names.addAll(getSymbolNames(preds));
		final String freshName = freshName(names, name);
		final SMTSortSymbol freshSort = new SMTSortSymbol(freshName,
				!SMTSymbol.PREDEFINED);
		/**
		 * Tries to put the sort in sorts set.
		 */
		sorts.add(freshSort);
		return freshSort;
	}

	/**
	 * Appends to the StringBuilder the string representation of the logic
	 * section
	 * 
	 * @param sb
	 *            the StringBuilder
	 */
	private void logicSection(final StringBuilder sb) {
		sb.append(" :logic ");
		sb.append(logic.getName());
		sb.append("\n");
	}

	/**
	 * One sort per line. May add a comment beside.
	 */
	private void extrasortsSection(final StringBuilder sb) {
		if (!sorts.isEmpty()) {
			extraSection(sb, sorts, "extrasorts");
		}
	}

	/**
	 * Appends to the StringBuilder the string representation of the extrapreds
	 * section
	 * 
	 * @param sb
	 */
	private void extrapredsSection(final StringBuilder sb) {
		if (!preds.isEmpty()) {
			extraSection(sb, preds, "extrapreds");
		}
	}

	/**
	 * Appends to the StringBuilder the string representation of the extrafuns
	 * section
	 * 
	 * @param sb
	 */
	private void extrafunsSection(final StringBuilder sb) {
		if (!funs.isEmpty()) {
			extraSection(sb, funs, "extrafuns");
		}
	}

	/**
	 * Add a constant to the signature.
	 * 
	 * @param constant
	 *            the constant.
	 */
	public void addConstant(final SMTFunctionSymbol constant) {
		funs.add(constant);
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
	public SMTPredicateSymbol addPredicateSymbol(final String name,
			final SMTSortSymbol... argSorts) {
		SMTPredicateSymbol symbol = new SMTPredicateSymbol(name,
				!SMTSymbol.PREDEFINED, argSorts);
		boolean successfullyAdded = preds.add(symbol);
		if (successfullyAdded) {
			return symbol;
		} else {
			symbol = new SMTPredicateSymbol(freshName(getSymbolNames(preds),
					name), !SMTSymbol.PREDEFINED, argSorts);
			successfullyAdded = preds.add(symbol);
			if (successfullyAdded) {
				return symbol;
			} else {
				return null;
			}
		}
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
