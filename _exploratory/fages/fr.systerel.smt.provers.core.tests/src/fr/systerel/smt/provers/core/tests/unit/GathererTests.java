/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.pptrans.Translator;
import org.junit.Test;

import br.ufrn.smt.solver.translation.Gatherer;
import fr.systerel.smt.provers.core.tests.AbstractTests;

/**
 * Ensures that the gatherer reports correct information about the sequent
 * passed to it for analysis.
 * 
 * @see Gatherer
 * @author Laurent Voisin
 * @author Vitor Almeida
 */
public class GathererTests extends AbstractTests {

	private static final String[] NO_MS_SPECIAL_PREDS = new String[0];

	private static class Option {
		private final boolean isTrue;

		public Option(final boolean isTrue) {
			this.isTrue = isTrue;
		}

		public void check(final boolean actual) {
			assertEquals(isTrue, actual);
		}

	}

	static class AtomicBoolExp extends Option {
		static final AtomicBoolExp FOUND = new AtomicBoolExp(true);
		static final AtomicBoolExp NOT_FOUND = new AtomicBoolExp(false);

		private AtomicBoolExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class AtomicIntegerExp extends Option {
		static final AtomicIntegerExp FOUND = new AtomicIntegerExp(true);
		static final AtomicIntegerExp NOT_FOUND = new AtomicIntegerExp(false);

		private AtomicIntegerExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class BoolTheory extends Option {
		static final BoolTheory FOUND = new BoolTheory(true);
		static final BoolTheory NOT_FOUND = new BoolTheory(false);

		private BoolTheory(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class TruePredicate extends Option {
		static final TruePredicate FOUND = new TruePredicate(true);
		static final TruePredicate NOT_FOUND = new TruePredicate(false);

		private TruePredicate(final boolean isTrue) {
			super(isTrue);
		}
	}

	private static void doTest(final ITypeEnvironment typenv,
			final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedSpecialMSPredImages, final String goal) {
		doTest(typenv, atomicBoolExp, atomicIntegerExp, boolTheory,
				truePredicate, expectedSpecialMSPredImages, new String[0], goal);
	}

	private static void doTest(final ITypeEnvironment typenv,
			final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedSpecialMSPredImages,
			final String[] hypotheses, final String goal) {

		final List<Predicate> preds = new ArrayList<Predicate>();
		for (final String hypothesis : hypotheses) {
			final Predicate h = parse(hypothesis, typenv);
			assertTrue("Predicate: " + h.toString()
					+ " is not in the PP sub-language.", Translator.isInGoal(h));
			preds.add(h);
		}
		final Predicate goalP = parse(goal, typenv);
		assertTrue("Predicate: " + goalP.toString()
				+ " is not in the PP sub-language.", Translator.isInGoal(goalP));
		final Set<FreeIdentifier> expectedSpecialMSPreds = getExpectedIdents(
				typenv, expectedSpecialMSPredImages);

		final Gatherer actual = Gatherer.gatherFrom(preds, goalP);
		checkResult(atomicBoolExp, atomicIntegerExp, boolTheory, truePredicate,
				expectedSpecialMSPreds, actual);
	}

	private static Set<FreeIdentifier> getExpectedIdents(
			final ITypeEnvironment typenv,
			final String[] expectedSpecialMSPredImages) {
		final FormulaFactory factory = typenv.getFormulaFactory();
		final Set<FreeIdentifier> result = new HashSet<FreeIdentifier>();
		for (final String name : expectedSpecialMSPredImages) {
			final Type type = typenv.getType(name);
			assertNotNull("Invalid identifier name " + name, type);
			assertNotNull("identifier " + name + " should be a set",
					type.getBaseType());
			result.add(factory.makeFreeIdentifier(name, null, type));
		}
		return result;
	}

	private static void checkResult(final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final Set<FreeIdentifier> expectedSpecialMSPreds,
			final Gatherer actual) {
		atomicBoolExp.check(actual.foundAtomicBoolExp());
		atomicIntegerExp.check(actual.foundAtomicIntegerExp());
		boolTheory.check(actual.usesBoolTheory());
		truePredicate.check(actual.usesTruePredicate());
		assertEquals(expectedSpecialMSPreds, actual.getSetsForSpecialMSPreds());
	}

	private static final String[] L(String... strings) {
		return strings;
	}

	/**
	 * Ensures that occurrence of the set of integers alone is correctly
	 * reported.
	 */
	@Test
	public void testIntegerExpr() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"(a↦ℤ ∈ X) ∨ (∃ T · (a↦ℤ ∈ T))");
	}

	@Test
	public void testIntegerSpecialMSExpr() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				L("X"), //
				"(a↦ℤ ∈ X) ∨ (∃ V · (a ∈ V))");
	}

	@Test
	public void testBoolTheory() {
		doTest(mTypeEnvironment("a", "BOOL"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"(a = b)");
	}

	@Test
	public void testSpecialMSPreds() {
		doTest(mTypeEnvironment("X", "ℙ(ℤ)"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				L("X"), //
				"∃t · (t ∈ X)");
	}

	@Test
	public void testSpecialMSPredsFull() {
		doTest(mTypeEnvironment("a", "A", "b", "B", "c", "A", "d", "B"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"∀H·((a↦b ∈ X) ∨ (c↦d ∈ H))");
	}

	@Test
	public void testSpecialMSPredsAbsence() {
		doTest(mTypeEnvironment(),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"∃t⦂ℤ · (∀ X · (t ∈ X))");
	}

	@Test
	public void testMix1() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"(a = b)");
	}

	@Test
	public void testMix2() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"a↦BOOL↦BOOL ∈ X ∧ (∃T · a↦BOOL↦BOOL ∈ T)");
	}

	@Test
	public void testMix2_1() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				L("X"), //
				"a↦BOOL↦BOOL ∈ X");
	}

	@Test
	public void testNotBoolSetNotIntgSet() {
		doTest(mTypeEnvironment("a", "BOOL", "b", "BOOL", "c", "BOOL", "d",
				"BOOL"),//
		AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"∀H·((a↦b↦c ∈ X) ∨ (c↦d↦a ∈ H))");
	}

	@Test
	public void testNotBoolSetOnly() {
		doTest(mTypeEnvironment("a", "BOOL", "g", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				L("G", "X"), //
				"(a↦ℤ ∈ X) ∧ (g ∈ G)");
	}

	@Test
	public void testAll() {
		doTest(mTypeEnvironment(),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				L("X"), //
				"(a↦BOOL↦ℤ ∈ X) ∧ (a = TRUE)");
	}
}
