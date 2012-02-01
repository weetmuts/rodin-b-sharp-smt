/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.unit;

import static org.eventb.core.seqprover.transformer.SimpleSequents.make;
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
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.pptrans.Translator;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.Gatherer;
import org.junit.Test;

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

	/**
	 * Class created to handle each expected field from the Gatherer
	 * 
	 */
	private static class Option {
		private final boolean isTrue;

		public Option(final boolean isTrue) {
			this.isTrue = isTrue;
		}

		public void check(final boolean actual) {
			assertEquals(isTrue, actual);
		}

	}

	/**
	 * Class created to handle the expected field Bool Set from the Gatherer.
	 **/
	static class AtomicBoolExp extends Option {
		static final AtomicBoolExp FOUND = new AtomicBoolExp(true);
		static final AtomicBoolExp NOT_FOUND = new AtomicBoolExp(false);

		private AtomicBoolExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	/**
	 * Class created to handle the expected field Integer set from the Gatherer.
	 **/
	static class AtomicIntegerExp extends Option {
		static final AtomicIntegerExp FOUND = new AtomicIntegerExp(true);
		static final AtomicIntegerExp NOT_FOUND = new AtomicIntegerExp(false);

		private AtomicIntegerExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	/**
	 * Class created to handle the expected field BoolTheoryFound from the
	 * Gatherer.
	 **/
	static class BoolTheory extends Option {
		static final BoolTheory FOUND = new BoolTheory(true);
		static final BoolTheory NOT_FOUND = new BoolTheory(false);

		private BoolTheory(final boolean isTrue) {
			super(isTrue);
		}
	}

	/**
	 * Class created to handle the expected element True Predicate from the
	 * Gatherer.
	 **/
	static class TruePredicate extends Option {
		static final TruePredicate FOUND = new TruePredicate(true);
		static final TruePredicate NOT_FOUND = new TruePredicate(false);

		private TruePredicate(final boolean isTrue) {
			super(isTrue);
		}
	}

	/**
	 * Executes tests in the Gatherer class. This method is similar to the other
	 * doTest in this class, but it accepts no hypotheses.
	 * 
	 * @param typenv
	 *            The type environment for the test
	 * @param atomicBoolExp
	 *            the expected field value
	 * @param atomicIntegerExp
	 *            the expected field value
	 * @param boolTheory
	 *            the expected field value
	 * @param truePredicate
	 *            the expected field value
	 * @param expectedSpecialMSPredImages
	 *            the expected monadic predicates
	 * @param goal
	 *            the goal for the test
	 */
	private static void doTest(final ITypeEnvironment typenv,
			final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedSpecialMSPredImages, final String goal) {
		doTest(typenv, atomicBoolExp, atomicIntegerExp, boolTheory,
				truePredicate, expectedSpecialMSPredImages, new String[0], goal);
	}

	/**
	 * Execute tests in the Gatherer class
	 * 
	 * @param typenv
	 *            The type environment for the test
	 * @param atomicBoolExp
	 *            the expected field value
	 * @param atomicIntegerExp
	 *            the expected field value
	 * @param boolTheory
	 *            the expected field value
	 * @param truePredicate
	 *            the expected field value
	 * @param expectedSpecialMSPredImages
	 *            the expected monadic predicates
	 * @param hypotheses
	 *            the hypotheses for the test
	 * @param goal
	 *            the goal for the test
	 */
	private static void doTest(final ITypeEnvironment typenv,
			final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedSpecialMSPredImages,
			final String[] hypotheses, final String goal) {

		final List<Predicate> preds = new ArrayList<Predicate>();
		for (final String hypothesis : hypotheses) {
			final Predicate h = parse(hypothesis, typenv);
			preds.add(h);
		}
		final Predicate goalP = parse(goal, typenv);

		final ISimpleSequent sequent = make(preds, goalP, ff);

		assertTrue("Sequent is not in the PP sub-language.",
				Translator.isInGoal(sequent));

		final Set<FreeIdentifier> expectedSpecialMSPreds = getExpectedIdents(
				typenv, expectedSpecialMSPredImages);

		final Gatherer actual = Gatherer.gatherFrom(sequent);
		checkResult(atomicBoolExp, atomicIntegerExp, boolTheory, truePredicate,
				expectedSpecialMSPreds, actual);
	}

	/**
	 * Given the type environment and the expected predicate strings, thi method
	 * builds and returns the set of Free Identifiers that are expected to be
	 * translated to monadic predicates.
	 * 
	 * @param typenv
	 *            the type environment
	 * @param expectedSpecialMSPredImages
	 *            the list of expected monadic predicate strings
	 * @return the set of Free Identifiers that are expected to be translated to
	 *         monadic predicates.
	 */
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

	/**
	 * Returns an array of String
	 * 
	 * @param strings
	 *            the list of strings
	 * @return the array of the strings
	 */
	private static final String[] L(final String... strings) {
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

	/**
	 * Ensures that occurrence of the set of integers and at monadic predicates
	 * are correctly reported.
	 */
	@Test
	public void testIntegerAndSpecialMSExpr() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				L("X"), //
				"(a↦ℤ ∈ X) ∨ (∃ V · (a ∈ V))");
	}

	/**
	 * Ensures that the occurrence of Bool Theory in the predicate, looking at
	 * the type of the identifiers, is correctly reported.
	 */
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

	/**
	 * Ensures that the occurrence of monadic predicates, where the left side of
	 * the membership that contains the monadic predicate is a bound identifier,
	 * is correctly reported.
	 */
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

	/**
	 * Ensures that, if a membership contains in the right side a bound
	 * identifier, no other predicate from similar membership is translated to
	 * monadic predicate.
	 */
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

	/**
	 * Ensures that if the elements of the membership are bound, then the right
	 * identifier cannot be translated to monadic predicate.
	 * 
	 */
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

	/**
	 * Ensures that no field of the Gatherer is changed, that is, no symbol is
	 * found.
	 */
	@Test
	public void testNoSymbol() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"(a = b)");
	}

	/**
	 * Ensures that the set Bool, the Bool Theory and the Bool Predicate are
	 * defined as true in the Gatherer.
	 */
	@Test
	public void testMix() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				NO_MS_SPECIAL_PREDS, //
				"a↦BOOL↦BOOL ∈ X ∧ (∃T · a↦BOOL↦BOOL ∈ T)");
	}

	/**
	 * Ensures that the set Bool, the Bool Theory, the Bool Predicate and
	 * Monadic Preds are correctly reported.
	 */
	@Test
	public void testMix_1() {
		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				L("X"), //
				"a↦BOOL↦BOOL ∈ X");
	}

	/**
	 * Ensures that Bool Theory and True Predicate are correctly reported, as
	 * well as identifiers are not translated to monadic predicates if there is
	 * a similar membership where the identifier in the right is bound.
	 */
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

	/**
	 * Ensures that the Gatherer reports correctly the symbols in a predicate
	 * where only the set Bool is not present.
	 */
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

	/**
	 * Ensures that the Gatherer reports correctly in a predicate that contains
	 * all the symbols.
	 */
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
