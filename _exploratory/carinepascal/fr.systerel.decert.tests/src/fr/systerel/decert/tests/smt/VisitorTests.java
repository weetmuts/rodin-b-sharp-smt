/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.tests.smt;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

import fr.systerel.decert.smt.Visitor;

public class VisitorTests extends AbstractSMTTests {

	private final FreeIdentifier x = ff.makeFreeIdentifier("x", null);
	private final FreeIdentifier y = ff.makeFreeIdentifier("y", null);
	private final Predicate p = ff.makeLiteralPredicate(Formula.BTRUE, null);
	private final Predicate q = ff.makeLiteralPredicate(Formula.BFALSE, null);
	private final Expression zero = ff
			.makeIntegerLiteral(BigInteger.ZERO, null);
	private final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);
	private final Expression minusone = ff.makeIntegerLiteral(new BigInteger("-1"), null);

	private <T extends Formula<?>> void assertVisit(T formula, String expected) {
		final Visitor visitor = new Visitor();
		formula.accept(visitor);
		assertEquals(expected, visitor.getSMTNode());
	}

	private static <T> T[] list(T... objs) {
		return objs;
	}

	@Test
	public void testAssociativeExpression() {
		assertVisit(ff
				.makeAssociativeExpression(Formula.PLUS, list(x, y), null),
				"(+ x y)");
		assertVisit(
				ff.makeAssociativeExpression(Formula.MUL, list(x, y), null),
				"(* x y)");
	}

	@Test
	public void testAssociativePredicate() {
		assertVisit(
				ff.makeAssociativePredicate(Formula.LAND, list(p, q), null),
				"(and true false)");
		assertVisit(ff.makeAssociativePredicate(Formula.LOR, list(p, q), null),
				"(or true false)");
	}

	@Test
	public void testAtomicExpression() {
		assertVisit(ff.makeAtomicExpression(Formula.INTEGER, null), "");
		assertVisit(ff.makeAtomicExpression(Formula.NATURAL, null), "");
		assertVisit(ff.makeAtomicExpression(Formula.NATURAL1, null), "");
		assertVisit(ff.makeAtomicExpression(Formula.BOOL, null), "");
		assertVisit(ff.makeAtomicExpression(Formula.EMPTYSET, null), "");
		assertVisit(ff.makeAtomicExpression(Formula.TRUE, null), "TRUE");
		assertVisit(ff.makeAtomicExpression(Formula.FALSE, null), "FALSE");
	}

	@Test
	public void testBinaryExpression() {
		assertVisit(ff.makeBinaryExpression(Formula.MINUS, x, y, null),
				"(- x y)");
		assertVisit(ff.makeBinaryExpression(Formula.DIV, x, y, null), "(/ x y)");
		assertVisit(ff.makeBinaryExpression(Formula.MOD, x, y, null), "(% x y)");
		assertVisit(ff.makeBinaryExpression(Formula.UPTO, x, y, null), "");
	}

	@Test
	public void testBinaryPredicate() {
		assertVisit(ff.makeBinaryPredicate(Formula.LIMP, p, q, null),
				"(implies true false)");
		assertVisit(ff.makeBinaryPredicate(Formula.LEQV, p, q, null),
				"(iff true false)");
	}

	@Test
	public void testBoolExpression() {
		assertVisit(ff.makeBoolExpression(p, null),
				"(ite true TRUE FALSE)");
	}

	@Test
	public void testFreeIdentifier() {
		assertVisit(x, "x");
	}

	@Test
	public void testIntegerLiteral() {
		assertVisit(zero, "0");
		assertVisit(minusone, "(~ 1)");
	}

	@Test
	public void testLiteralPredicate() {
		assertVisit(ff.makeLiteralPredicate(Formula.BTRUE, null), "true");
		assertVisit(ff.makeLiteralPredicate(Formula.BFALSE, null), "false");
	}

	@Test
	public void testRelationalPredicate() {
		assertVisit(ff.makeRelationalPredicate(Formula.EQUAL, x, y, null),
				"(= x y)");
		assertVisit(ff.makeRelationalPredicate(Formula.NOTEQUAL, x, y, null),
				"(not (= x y))");
		assertVisit(ff.makeRelationalPredicate(Formula.LT, x, y, null),
				"(< x y)");
		assertVisit(ff.makeRelationalPredicate(Formula.LE, x, y, null),
				"(<= x y)");
		assertVisit(ff.makeRelationalPredicate(Formula.GT, x, y, null),
				"(> x y)");
		assertVisit(ff.makeRelationalPredicate(Formula.GE, x, y, null),
				"(>= x y)");
		assertVisit(ff.makeRelationalPredicate(Formula.IN, x, ff
				.makeAtomicExpression(Formula.NATURAL, null), null), "(>= x 0)");
		assertVisit(ff.makeRelationalPredicate(Formula.IN, x, ff
				.makeAtomicExpression(Formula.NATURAL1, null), null), "(> x 0)");
		assertVisit(ff.makeRelationalPredicate(Formula.IN, x, ff
				.makeAtomicExpression(Formula.EMPTYSET, null), null), "false");
		assertVisit(ff.makeRelationalPredicate(Formula.IN, x, ff
				.makeBinaryExpression(Formula.UPTO, zero, y, null), null),
				"(and (>= x 0) (<= x y))");
		assertVisit(ff.makeRelationalPredicate(Formula.IN, x, ff
				.makeSetExtension(list(zero, one, y), null), null),
				"(or (= x 0) (= x 1) (= x y))");
	}

	@Test
	public void testSetExtension() {
		assertVisit(ff.makeSetExtension(list(x, y, zero), null), "");
	}

	@Test
	public void testUnaryExpression() {
		assertVisit(ff.makeUnaryExpression(Formula.UNMINUS, x, null), "(~ x)");
		assertVisit(ff.parsePredicate("¬ a = −1",
				LanguageVersion.V2, null).getParsedPredicate(), "(not (= a (~ 1)))");
		
	}

	@Test
	public void testUnaryPredicate() {
		assertVisit(ff.makeUnaryPredicate(Formula.NOT, p, null), "(not true)");
	}
}
