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

import static org.eventb.smt.translation.SMTLIBVersion.V2_0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eventb.smt.ast.commands.SMTCommand.SMTCommandName;
import org.eventb.smt.ast.commands.SMTDeclareFunCommand;
import org.eventb.smt.ast.commands.SMTDeclareSortCommand;
import org.eventb.smt.ast.commands.SMTSetLogicCommand;
import org.eventb.smt.ast.symbols.SMTQuantifierSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTLogic;

public abstract class SMTSignatureV2_0 extends SMTSignature {
	/**
	 * reserved symbols and keywords
	 */
	private final static Set<String> reservedSymbols = getReservedSymbolsAndKeywordsV2_0();

	/**
	 * Construts a new Signature given the SMT Logic
	 * 
	 * @param logic
	 *            the logic used in the SMTSignature
	 */
	public SMTSignatureV2_0(final SMTLogic logic) {
		super(logic, V2_0);
	}

	/**
	 * This method returns a set with the reserved symbols and keywords in
	 * SMT-LIB 2.0
	 * 
	 * @return the reserved symbols and keyboards.
	 */
	public static Set<String> getReservedSymbolsAndKeywordsV2_0() {
		final List<String> reservedSymbolsAndKeywords = new ArrayList<String>(
				Arrays.asList("par", "NUMERAL", "DECIMAL", "STRING", "_", "!",
						"as", "let"));
		final boolean successfullyAddedReservedSymbolsAndKeywords = reservedSymbolsAndKeywords
				.addAll(SMTQuantifierSymbol.getQuantifierSymbols())
				&& reservedSymbolsAndKeywords.addAll(SMTConnective
						.getConnectiveSymbols(V2_0))
				&& reservedSymbolsAndKeywords.addAll(SMTCommandName
						.getCommandNames());
		assert successfullyAddedReservedSymbolsAndKeywords;
		return new HashSet<String>(reservedSymbolsAndKeywords);
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

	@Override
	void loadReservedAndPredefinedSymbols() {
		names.addAll(reservedSymbols);
	}

	@Override
	String freshSymbolName(final Set<String> symbolNames, final String name,
			final String newSymbolName) {
		final String freshName;
		/**
		 * Avoids creating names similar to reserved symbols, predefined symbols
		 * or keywords
		 */
		if (reservedSymbols.contains(name)) {
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
	 * @param builder
	 *            the StringBuilder
	 */
	@Override
	public void toString(final StringBuilder builder) {
		logicSection(builder);
		sortDeclarations(builder);
		funDeclarations(builder, preds);
		funDeclarations(builder, funs);
	}
}
