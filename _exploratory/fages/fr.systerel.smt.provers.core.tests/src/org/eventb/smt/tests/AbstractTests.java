/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTSignature;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;


public abstract class AbstractTests {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Builds an Event-B type environment with given combined symbols
	 */
	protected static ITypeEnvironment mTypeEnvironment(final String... strs) {
		assert (strs.length & 1) == 0;
		final ITypeEnvironment te = ff.makeTypeEnvironment();
		for (int i = 0; i < strs.length; i += 2) {
			final String name = strs[i];
			final Type type = parseType(strs[i + 1]);
			te.addName(name, type);
		}
		return te;
	}

	/**
	 * Parses an Event-B predicate string representation to build a 'Predicate'
	 * instance
	 */
	public static Predicate parse(final String predicate,
			final ITypeEnvironment te) {
		final IParseResult parseResult = te.getFormulaFactory().parsePredicate(
				predicate, V2, null);
		assertFalse("Parse error for: " + predicate + "\nProblems: "
				+ parseResult.getProblems(), parseResult.hasProblem());
		final Predicate parsedPredicate = parseResult.getParsedPredicate();
		final ITypeCheckResult tcResult = parsedPredicate.typeCheck(te);
		assertTrue(
				predicate + " is not typed. Problems: "
						+ tcResult.getProblems(),
				parsedPredicate.isTypeChecked());
		te.addAll(tcResult.getInferredEnvironment());
		return parsedPredicate;
	}

	/**
	 * Asserts that the given Event-B formula is typed.
	 */
	public static void assertTypeChecked(final Formula<?> formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

	private static String typeEnvironmentSortsFail(
			final Set<String> expectedSorts, final Set<SMTSortSymbol> sorts) {
		final StringBuilder sb = new StringBuilder();
		sb.append("The translated sorts wasn't the expected ones. The expected sorts are:");
		for (final String expectedSort : expectedSorts) {
			sb.append("\n");
			sb.append(expectedSort);
		}
		sb.append("\nBut the translated sorts were:");
		for (final SMTSortSymbol sortSymbol : sorts) {
			sb.append("\n");
			sb.append(sortSymbol.toString());
		}
		sb.append("\n");
		return sb.toString();
	}

	private static String typeEnvironmentPredicatesFail(
			final Set<String> expectedPredicates,
			final Set<SMTPredicateSymbol> predicates) {
		final StringBuilder sb = new StringBuilder();
		sb.append("The translated predicates wasn't the expected ones. The expected predicates are:");
		for (final String expectedPredicate : expectedPredicates) {
			sb.append("\n");
			sb.append(expectedPredicate);
		}
		sb.append("\nBut the translated predicates were:");
		for (final SMTPredicateSymbol predicateSymbol : predicates) {
			if (!predicateSymbol.isPredefined()) {
				sb.append("\n");
				predicateSymbol.toString(sb);
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	public static void testTypeEnvironmentPreds(
			final Set<String> expectedPredicates, final SMTSignature signature) {
		final Set<SMTPredicateSymbol> predicateSymbols = signature.getPreds();
		final Iterator<SMTPredicateSymbol> iterator = predicateSymbols
				.iterator();
		final String sb = typeEnvironmentPredicatesFail(expectedPredicates,
				predicateSymbols);
		assertEquals(sb.toString(), expectedPredicates.size(),
				numberOfNonPredefinedPredSymbols(predicateSymbols));

		while (iterator.hasNext()) {
			final SMTPredicateSymbol pS = iterator.next();
			if (!pS.isPredefined()) {
				final StringBuilder builder = new StringBuilder();
				pS.toString(builder);
				assertTrue(sb, expectedPredicates.contains(builder.toString()));
			}
		}
	}

	private static String typeEnvironmentFunctionsFail(
			final Set<String> expectedFunctions,
			final Set<SMTFunctionSymbol> functions) {
		final StringBuilder sb = new StringBuilder();
		sb.append("The translated functions wasn't the expected ones. The expected functions are:");
		for (final String expectedFunction : expectedFunctions) {
			sb.append("\n");
			sb.append(expectedFunction);
		}
		sb.append("\nBut the translated functions were:");
		for (final SMTFunctionSymbol fSymbol : functions) {
			if (!fSymbol.isPredefined()) {
				sb.append("\n");
				fSymbol.toString(sb);
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	public static int numberOfNonPredefinedFunSymbols(
			final Set<SMTFunctionSymbol> funs) {
		int count = 0;
		for (final SMTFunctionSymbol symbol : funs) {
			if (!symbol.isPredefined()) {
				++count;
			}
		}
		return count;
	}

	public static int numberOfNonPredefinedPredSymbols(
			final Set<SMTPredicateSymbol> preds) {
		int count = 0;
		for (final SMTPredicateSymbol symbol : preds) {
			if (!symbol.isPredefined()) {
				++count;
			}
		}
		return count;
	}

	public static void testTypeEnvironmentSorts(final SMTSignature signature,
			final Set<String> expectedSorts, final String predString) {
		final Set<SMTSortSymbol> sortSymbols = signature.getSorts();
		final Iterator<SMTSortSymbol> iterator = sortSymbols.iterator();
		final String sb = typeEnvironmentSortsFail(expectedSorts, sortSymbols);
		assertEquals(sb.toString(), expectedSorts.size(), sortSymbols.size());

		while (iterator.hasNext()) {
			assertTrue(sb, expectedSorts.contains(iterator.next().toString()));
		}
	}

	public static void testTypeEnvironmentFuns(final SMTSignature signature,
			final Set<String> expectedFunctions, final String predString) {
		final Set<SMTFunctionSymbol> functionSymbols = signature.getFuns();
		final Iterator<SMTFunctionSymbol> iterator = functionSymbols.iterator();

		final String sb = typeEnvironmentFunctionsFail(expectedFunctions,
				functionSymbols);
		assertEquals(sb.toString(), expectedFunctions.size(),
				numberOfNonPredefinedFunSymbols(functionSymbols));

		while (iterator.hasNext()) {
			final SMTFunctionSymbol fS = iterator.next();
			if (!fS.isPredefined()) {
				final StringBuilder builder = new StringBuilder();
				fS.toString(builder);
				assertTrue(sb, expectedFunctions.contains(builder.toString()));
			}
		}
	}

	public static void testTypeEnvironmentPreds(final SMTSignature signature,
			final Set<String> expectedPreds, final String predString) {
		final Set<SMTPredicateSymbol> predSymbols = signature.getPreds();
		final Iterator<SMTPredicateSymbol> iterator = predSymbols.iterator();

		final String sb = typeEnvironmentPredicatesFail(expectedPreds,
				predSymbols);
		assertEquals(sb.toString(), expectedPreds.size(),
				numberOfNonPredefinedPredSymbols(predSymbols));

		while (iterator.hasNext()) {
			final SMTPredicateSymbol fS = iterator.next();
			if (!fS.isPredefined()) {
				final StringBuilder builder = new StringBuilder();
				fS.toString(builder);
				assertTrue(sb, expectedPreds.contains(builder.toString()));
			}
		}
	}

	protected String assumptionsString(final List<SMTFormula> assumptions) {
		final StringBuilder assumptionsStringBuilder = new StringBuilder();
		for (final SMTFormula assumption : assumptions) {
			assumptionsStringBuilder.append(assumption);
			assumptionsStringBuilder.append("\n");
		}
		return assumptionsStringBuilder.toString();
	}
}