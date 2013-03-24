/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UFRN - additional methods
 *******************************************************************************/
package org.eventb.smt.core.internal.ast;

import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.BENCHMARK;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.EQUAL;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.LOGIC;
import static org.eventb.smt.core.internal.ast.symbols.SMTSymbol.THEORY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eventb.smt.core.internal.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.core.internal.ast.symbols.SMTSymbol;
import org.eventb.smt.core.internal.ast.theories.Logic;

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
public abstract class SMTSignatureV1_2 extends SMTSignature {
	/**
	 * reserved symbols and keywords
	 */
	private final static Set<String> reservedSymbols = getReservedSymbolsAndKeywordsV1_2();

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
	 * Construts a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignatureV1_2(final Logic logic) {
		super(logic, V1_2);
	}

	/**
	 * This method returns a set with the reserved symbols and keywords in
	 * SMT-LIB 1.2
	 * 
	 * @return the reserved symbols and keyboards.
	 */
	public static Set<String> getReservedSymbolsAndKeywordsV1_2() {
		final List<String> reservedSymbolsAndKeywords = new ArrayList<String>(
				Arrays.asList(EQUAL, "and", BENCHMARK, "distinct", "false",
						"flet", "if_then_else", "iff", "implies", "ite", "let",
						LOGIC, "not", "or", "sat", THEORY, "true", "unknown",
						"unsat", "xor"));
		final boolean successfullyAddedReservedSymbolsAndKeywords = reservedSymbolsAndKeywords
				.addAll(SMTConnective.getConnectiveSymbols(V1_2))
				&& reservedSymbolsAndKeywords.addAll(SMTQuantifierSymbol
						.getQuantifierSymbols());
		assert successfullyAddedReservedSymbolsAndKeywords;
		return new HashSet<String>(reservedSymbolsAndKeywords);
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
	 * Appends to the StringBuilder the string representation of the logic
	 * section
	 * 
	 * @param builder
	 *            the StringBuilder
	 */
	private void logicSection(final StringBuilder builder) {
		builder.append(" :logic ");
		builder.append(logic.getName());
		builder.append("\n");
	}

	/**
	 * One sort per line. May add a comment beside.
	 */
	private void extrasortsSection(final StringBuilder builder) {
		if (!sorts.isEmpty()) {
			extraSection(builder, sorts, "extrasorts");
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
			extraSection(builder, preds, "extrapreds");
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
			extraSection(builder, funs, "extrafuns");
		}
	}

	@Override
	void loadReservedAndPredefinedSymbols() {
		names.addAll(reservedSymbols);
		names.addAll(Arrays.asList(predefinedAttributesSymbols));
		names.add("U"); // predefined sort in Empty theory
	}

	@Override
	String freshSymbolName(final Set<String> symbolNames, final String name,
			final String newSymbolName) {
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
	 * Appends to the StringBuilder the string representation of the signature
	 * 
	 * @param sb
	 *            the StringBuilder
	 */
	@Override
	public void toString(final StringBuilder sb) {
		logicSection(sb);
		extrasortsSection(sb);
		extrapredsSection(sb);
		extrafunsSection(sb);
	}
}
