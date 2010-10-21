/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package fr.systerel.smt.provers.tests;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

public abstract class AbstractTests {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Builds a type environment with given combined symbols
	 */
	protected static ITypeEnvironment mTypeEnvironment(String... strs) {
		assert (strs.length & 1) == 0;
		ITypeEnvironment te = ff.makeTypeEnvironment();
		for (int i = 0; i < strs.length; i += 2) {
			final String name = strs[i];
			final Type type = parseType(strs[i + 1]);
			te.addName(name, type);
		}
		return te;
	}

	/**
	 * Parses an Event-B predicate string representation to build a Predicate instance
	 */
	public static Predicate parse(String string, ITypeEnvironment te) {
		IParseResult parseResult = ff.parsePredicate(string, V2, null);
		assertFalse("Parse error for: " + string +
				"\nProblems: " + parseResult.getProblems(),
				parseResult.hasProblem());
		Predicate pred = parseResult.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(te);
		assertTrue(string + " is not typed. Problems: " + tcResult.getProblems(),
				pred.isTypeChecked());
		te.addAll(tcResult.getInferredEnvironment());
		return pred;
	}

	public static Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}

	public static void assertTypeChecked(Formula<?> formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

}
