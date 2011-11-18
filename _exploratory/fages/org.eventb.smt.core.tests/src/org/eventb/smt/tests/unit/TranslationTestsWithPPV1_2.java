/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - tests fixing
 *******************************************************************************/

package org.eventb.smt.tests.unit;

import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.tests.unit.Messages.SMTLIB_Translation_Failed;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTSignatureV1_2;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.ast.theories.SMTTheoryV1_2;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.SMTThroughPP;
import org.junit.Test;

/**
 * Ensure that translation from ppTrans produced predicates to SMT-LIB
 * predicates is correct.
 * 
 * @author Yoann Guyot
 * 
 */
public class TranslationTestsWithPPV1_2 extends AbstractTests {
	protected static final ITypeEnvironment defaultTe;
	public static final SMTLogic defaultLogic;
	static {
		defaultTe = mTypeEnvironment( //
				"S", "ℙ(S)", "r", "ℙ(R)", "s", "ℙ(R)", //
				"a", "ℤ", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		defaultLogic = new SMTLogic.SMTLogicPP(SMTLogic.UNKNOWN,
				SMTTheoryV1_2.Ints.getInstance(),
				SMTTheoryV1_2.Booleans.getInstance());
	}

	private void testTranslationV1_2(final ITypeEnvironment te,
			final String ppPredStr, final String expectedSMTNode) {
		testTranslationV1_2(te, ppPredStr, expectedSMTNode,
				SMTLIB_Translation_Failed);
	}

	private void testTranslateGoalPP(final ITypeEnvironment te,
			final String inputGoal, final String expectedFormula) {

		final Predicate goalPredicate = parse(inputGoal, te);

		assertTypeChecked(goalPredicate);

		final SMTBenchmark benchmark = SMTThroughPP.translateToSmtLibBenchmark(
				"lemma", new ArrayList<Predicate>(), goalPredicate, V1_2);

		final SMTFormula formula = benchmark.getFormula();
		assertEquals(expectedFormula, formula.toString());
	}

	private void testContainsAssumptionsPP(final ITypeEnvironment te,
			final String inputGoal, final List<String> expectedAssumptions) {

		final Predicate goal = parse(inputGoal, te);

		assertTypeChecked(goal);

		final SMTBenchmark benchmark = SMTThroughPP.translateToSmtLibBenchmark(
				"lemma", new ArrayList<Predicate>(), goal, V1_2);

		final List<SMTFormula> assumptions = benchmark.getAssumptions();
		assertEquals(assumptionsString(assumptions),
				expectedAssumptions.size(), assumptions.size());
		for (final SMTFormula assumption : assumptions) {
			assertTrue(
					expectedAssumptionMessage(expectedAssumptions,
							assumption.toString()),
					expectedAssumptions.contains(assumption.toString()));
		}
	}

	private String expectedAssumptionMessage(
			final List<String> expectedAssumptions, final String assumption) {
		return "Expected these assumptions: " + expectedAssumptions.toString()
				+ ". But found this assumption: " + assumption.toString();
	}

	private static void testTranslationV1_2Default(final String ppPredStr,
			final String expectedSMTNode) {
		testTranslationV1_2(defaultTe, ppPredStr, expectedSMTNode,
				SMTLIB_Translation_Failed);
	}

	public static void testTypeEnvironmentFuns(final SMTLogic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignatureV1_2 signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentFuns(signature, expectedFunctions, predString);
	}

	public static void testTypeEnvironmentSorts(final SMTLogic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignatureV1_2 signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentSorts(signature, expectedFunctions, predString);
	}

	protected static SMTSignatureV1_2 translateTypeEnvironment(
			final SMTLogic logic, final ITypeEnvironment iTypeEnv,
			final String ppPredStr) throws AssertionError {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv);

		assertTrue(
				TranslationTestsWithPPV1_2
						.producePPTargetSubLanguageError(ppPred),
				Translator.isInGoal(ppPred));

		return (SMTSignatureV1_2) SMTThroughPP.translateTE(logic, ppPred, V1_2);
	}

	private static String producePPTargetSubLanguageError(
			final Predicate predicate) {
		return "\'" + predicate
				+ "\' is not in the target sub-language of the PP translator.";
	}

	/**
	 * Parses a Predicate Calculus formula, (builds hypotheses and goal) and
	 * tests its SMT-LIB translation
	 * 
	 * @param iTypeEnv
	 *            Input type environment
	 * @param ppPredStr
	 *            String representation of the input predicate
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2(final ITypeEnvironment iTypeEnv,
			final String ppPredStr, final String expectedSMTNode,
			final String failMessage) throws AssertionError {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv);
		assertTrue(
				TranslationTestsWithPPV1_2
						.producePPTargetSubLanguageError(ppPred),
				Translator.isInGoal(ppPred));

		testTranslationV1_2(ppPred, expectedSMTNode, failMessage,
				VERIT.toString());
	}

	/**
	 * Tests the SMT-LIB translation with the given Predicate Calculus formula
	 * 
	 * @param ppPred
	 *            Input Predicate Calculus formula
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2(final Predicate ppPred,
			final String expectedSMTNode, final String failMessage,
			final String solver) {
		final StringBuilder actualSMTNode = new StringBuilder();
		SMTThroughPP.translate(ppPred, V1_2).toString(actualSMTNode, -1, false);

		System.out
				.println(translationMessage(ppPred, actualSMTNode.toString()));
		assertEquals(failMessage, expectedSMTNode, actualSMTNode.toString());
	}

	private static final String translationMessage(final Predicate ppPred,
			final String smtNode) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\'");
		sb.append(ppPred.toString());
		sb.append("\' was translated in \'");
		sb.append(smtNode);
		sb.append("\'");
		return sb.toString();
	}

	/**
	 * "pred-bin" in ppTrans abstract syntax
	 */
	@Test
	public void testPredBinop() {
		/**
		 * limp
		 */
		testTranslationV1_2Default("(a < b ∧ b < c) ⇒ a < c",
				"(implies (and (< a b) (< b c)) (< a c))");
		/**
		 * leqv
		 */
		testTranslationV1_2Default("(a ≤ b ∧ b ≤ a) ⇔ a = b",
				"(iff (and (<= a b) (<= b a)) (= a b))");
	}

	/**
	 * "pred-ass"
	 */
	@Test
	public void testPredAssop() {
		/**
		 * land
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v)",
				"(and (= a b) (iff u v))");
		/**
		 * land (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v) ∧ (r = s)",
				"(and (= a b) (iff u v) (forall (?x R) (iff (MS ?x r) (MS ?x s))))");
		/**
		 * lor
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v)",
				"(or (= a b) (iff u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (iff u v) (forall (?x R) (iff (MS ?x r) (MS ?x s))))");
	}

	/**
	 * "pred-una"
	 */
	@Test
	public void testPredUna() {
		testTranslationV1_2Default("¬ ((a ≤ b ∧ b ≤ c) ⇒ a < c)",
				"(not (implies (and (<= a b) (<= b c)) (< a c)))");
	}

	/**
	 * When a set is used on the left hand side of a membership, the translator
	 * must not use this set as a monadic membership predicate.
	 * 
	 * In this example, we expect the membership of the set PS to be translated
	 * with a monadic predicate, whereas, the membership of the set S must be
	 * translated with the generalised membership predicate 'MS'.
	 */
	@Test
	public void testMonadicMembershipPredicate() {
		final ITypeEnvironment te = mTypeEnvironment("PS", "ℙ(ℙ(ℤ))", "S",
				"ℙ(ℤ)", "x", "ℤ", "PPS", "ℙ(ℙ(ℤ) × ℙ(ℤ))");

		testTranslationV1_2(te, "S ∈ PS ⇒ ¬ x ∈ S",
				"(implies (PS S) (not (MS x S)))");

		testTranslationV1_2(te, "S ↦ S ∈ PPS ⇒ ¬ x ∈ S",
				"(implies (PPS S S) (not (MS x S)))");
	}

	/**
	 * "pred-quant"
	 */
	@Test
	public void testQuantifiers() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"RR", "r ↔ s");
		te.addAll(defaultTe);

		/**
		 * forall
		 */
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R) (s ?x))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2(te, "∀x,y·x↦y∈RR",
				"(forall (?x r) (?y s) (RR ?x ?y))");

		/**
		 * bound set
		 */
		testTranslationV1_2Default("∃ x ⦂ ℤ, X ⦂ ℙ(ℤ) · x ∈ X",
				"(exists (?x Int) (?X PZ) (MS ?x ?X))");

	}

	@Test
	public void testExists() {
		/**
		 * exists
		 */
		testTranslationV1_2Default("∃x·x∈s", "(exists (?x R) (s ?x))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV1_2Default("∃x,y·x∈s∧y∈s",
				"(exists (?x R) (?y R) (and (s ?x) (s ?y)))");
	}

	/**
	 * "pred-lit"
	 */
	@Test
	public void testPredLit() {
		/**
		 * btrue
		 */
		testTranslationV1_2Default("⊤", "true");
		/**
		 * bfalse
		 */
		testTranslationV1_2Default("⊥", "false");
	}

	/**
	 * "pred-rel"
	 */
	@Test
	public void testPredRelop() {
		/**
		 * equal (identifiers of type ℤ)
		 */
		testTranslationV1_2Default("a = b", "(= a b)");
		/**
		 * equal (integer numbers)
		 */
		testTranslationV1_2Default("42 − 1 + 1 = 42", "(= (+ (- 42 1) 1) 42)");
		/**
		 * lt
		 */
		testTranslationV1_2Default("a < b", "(< a b)");
		/**
		 * le
		 */
		testTranslationV1_2Default("a ≤ b", "(<= a b)");
		/**
		 * gt
		 */
		testTranslationV1_2Default("a > b", "(> a b)");
		/**
		 * ge
		 */
		testTranslationV1_2Default("a ≥ b", "(>= a b)");
	}

	/**
	 * Arithmetic expressions binary operations: cf. "a-expr-bin"
	 */
	@Test
	public void testArithExprBinop() {
		/**
		 * minus
		 */
		testTranslationV1_2Default("a − b = c", "(= (- a b) c)");
		/**
		 * equal (a-expr-bin)
		 */
		testTranslationV1_2Default("a − b = a − c", "(= (- a b) (- a c))");
	}

	@Test
	public void testArithExprBinopUnsupported() {
		/**
		 * expn
		 */
		testTranslationV1_2Default("a ^ b = c", "(= (expn a b) c)");
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (divi a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (mod a b) c)");
	}

	/**
	 * Arithmetic expressions associative operations: cf. "a-expr-ass"
	 */
	@Test
	public void testArithExprAssnop() {
		/**
		 * plus
		 */
		testTranslationV1_2Default("a + c + b = a + b + c",
				"(= (+ a c b) (+ a b c))");
		/**
		 * mul
		 */
		testTranslationV1_2Default("a ∗ b ∗ c = a ∗ c ∗ b",
				"(= (* a b c) (* a c b))");
	}

	/**
	 * Arithmetic expressions unary operations: cf. "a-expr-una"
	 */
	@Test
	public void testArithExprUnop() {
		/**
		 * uminus (right child)
		 */
		testTranslationV1_2Default("a = −b", "(= a (~ b))");
		/**
		 * uminus (left child)
		 */
		testTranslationV1_2Default("−a = b", "(= (~ a) b)");
	}

	/**
	 * "pred-in" This test should not happen with ppTrans; The
	 */

	@Test
	public void testPredIn() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ");
		te.addAll(defaultTe);

		testTranslationV1_2Default("a ∈ A", "(A a)");
		testTranslationV1_2(te, "a↦b ∈ AB", "(AB a b)");
		testTranslationV1_2(te, "a↦BOOL↦BOOL ∈ X", "(X a BOOLS BOOLS)");
	}

	@Test
	public void testPredIn2() {
		testTranslationV1_2Default("a↦BOOL↦a ∈ Y", "(Y a BOOLS a)");
	}

	@Test
	public void testPredInInt() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"int", "S", "SPZ", "S ↔ ℙ(ℤ)", "AZ", "ℤ ↔ ℙ(ℤ)");
		te.addAll(defaultTe);

		/**
		 * Through these unit tests, the integer axiom is not generated. That's
		 * why the membership predicate symbol 'MS' is not already in use, and
		 * can be expected here.
		 */
		testTranslationV1_2(te, "INTS↦ℤ ∈ SPZ", "(SPZ INTS0 INTS)");
		testTranslationV1_2(te, "a↦ℤ ∈ AZ", "(AZ a INTS)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV1_2Default("r = s",
				"(forall (?x R) (iff (MS ?x r) (MS ?x s)))");
	}

	/**
	 * "pred-boolequ"
	 */
	@Test
	public void testPredBoolEqu() {
		testTranslationV1_2Default("u = v", "(iff u v)");
		testTranslationV1_2Default("u = TRUE", "u");
		testTranslationV1_2Default("TRUE = u", "u");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"p", "S", "q", "S");

		testTranslationV1_2(te, "p = q", "(= p q)");
	}

	@Test
	public void testTRUELit() {
		final ITypeEnvironment te = mTypeEnvironment( //
				//
				"f", "ℙ(BOOL)", "x", "BOOL");

		testTranslationV1_2(te, "x ∈ f", "(f x)");
	}

	@Test
	public void testTRUEPred() {
		final ITypeEnvironment te = mTypeEnvironment( //
				//
				"B", "ℙ(BOOL)", "b", "BOOL", "c", "BOOL");

		/**
		 * Formulas containing boolean equalities and memberships involving
		 * boolean values.
		 */
		testTranslationV1_2(te, "b = TRUE ∧ b ∈ B", "(and (TRUE b) (B b))");
		testTranslationV1_2(te, "TRUE = b ∧ b ∈ B", "(and (TRUE b) (B b))");
		testTranslationV1_2(te, "b = c ∧ b ∈ B",
				"(and (iff (TRUE b) (TRUE c)) (B b))");

		/**
		 * Formulas containing boolean equalities and quantified boolean
		 * variables.
		 */
		testTranslationV1_2(te, "b = TRUE ∧ (∀d·d = b)",
				"(and (TRUE b) (forall (?d BOOL) (iff (TRUE ?d) (TRUE b))))");
		testTranslationV1_2(te, "TRUE = b ∧ (∀d·d = b)",
				"(and (TRUE b) (forall (?d BOOL) (iff (TRUE ?d) (TRUE b))))");
		testTranslationV1_2(te, "b = c ∧ (∀d·d = b)",
				"(and (iff (TRUE b) (TRUE c)) (forall (?d BOOL) (iff (TRUE ?d) (TRUE b))))");

		/**
		 * Boolean equalities without any membership involving boolean values,
		 * neither quantified boolean variables.
		 */
		testTranslationV1_2(te, "b = TRUE", "b");
		testTranslationV1_2(te, "TRUE = b", "b");
		testTranslationV1_2(te, "b = c", "(iff b c)");
	}

	@Test
	public void testPredefinedAttributesSymbols() {
		final ITypeEnvironment te = mTypeEnvironment( //
				//
				"assumption", "funs", "formula", "funs");

		testTranslationV1_2(te, "assumption = formula", "(= nf nf1)");
	}

	@Test
	public void testPredefinedAttributesSymbolsSorts() {
		final ITypeEnvironment te = mTypeEnvironment( //
				//
				"if_then_else", "ℙ(NS)", "implies", "NS", "ite", "NS");

		final Set<String> expectedSorts = new HashSet<String>(Arrays.asList(
				"PN", "Int", "PZ", "BOOL", "PB", "NS"));

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"implies = ite");

	}

	@Test
	public void testReservedSymbolsAndKeywords() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"distinct", "false", "nf", "false");

		testTranslationV1_2(te, "distinct = flet", "(= nf1 nf)");
	}

	@Test
	public void testpredefinedAttributesSymbolsSorts() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"status", "ℙ(logic)", //
				"extrasorts", "logic", //
				"extrafuns", "logic");

		final Set<String> expectedSorts = new HashSet<String>(Arrays.asList( //
				"NS", "Int", "PZ", "BOOL", "PB", "PL"));

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"extrasorts = extrafuns");
	}

	@Test
	public void testpredefinedAttributesSymbolsFuns() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"status", "ℙ(logic)", //
				"extrasorts", "logic", //
				"extrafuns", "logic");

		final Set<String> expectedFuns = new HashSet<String>(Arrays.asList(
				"(BOOLS PB)", //
				"(mod Int Int Int)", //
				"(nf0 NS)", //
				"(INTS PZ)", //
				"(expn Int Int Int)", //
				"(divi Int Int Int)", //
				"(nf1 PL)", //
				"(nf NS)"));

		testTypeEnvironmentFuns(defaultLogic, te, expectedFuns,
				"extrasorts = extrafuns");
	}

	@Test
	public void testReservedSymbolsAndKeywordsSorts() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"if_then_else", "ℙ(NS)", "implies", "NS", "ite", "NS");

		final Set<String> expectedSorts = new HashSet<String>(Arrays.asList(
				"PN", "Int", "PZ", "BOOL", "PB", "NS"));

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"implies = ite");
	}

	@Test
	public void testReservedSymbolsAndKeywordsFuns() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"if_then_else", "ℙ(NS)", "implies", "NS", "ite", "NS");

		final Set<String> expectedFuns = new HashSet<String>(Arrays.asList(
				"(BOOLS PB)", //
				"(nf0 NS)", //
				"(mod Int Int Int)", //
				"(INTS PZ)", //
				"(expn Int Int Int)", //
				"(NS0 PN)", //
				"(divi Int Int Int)", //
				"(nf NS)"));

		testTypeEnvironmentFuns(defaultLogic, te, expectedFuns, "implies = ite");
	}

	@Test
	public void testNumeral() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		testTranslateGoalPP(te, "n ≥ 1", "(not (<= 1 n))");
	}

	@Test
	public void testQuantifier() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		testTranslateGoalPP(te, "∀ x · x + 1 ∈ S",
				"(not (forall (?x Int) (exists (?x0 Int) (and (= ?x0 (+ ?x 1)) (S ?x0)))))");
	}

	@Test
	public void testIntAxiom() {
		final ITypeEnvironment te = defaultTe;
		final List<String> expectedAssumptions = Arrays
				.asList("(forall (?x Int) (MS ?x INTS))", //
						"(forall (?x0 Int) (exists (?X PZ) (and (MS ?x0 ?X) (forall (?y Int) (implies (MS ?y ?X) (= ?y ?x0))))))");
		testContainsAssumptionsPP(te, "a↦ℤ ∈ AZ", expectedAssumptions);
	}

	@Test
	public void testTrueAxiom() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"Y", "ℙ(BOOL×BOOL)");
		final List<String> expectedAssumptions = Arrays
				.asList("(forall (?x BOOL) (?y BOOL) (iff (iff (TRUE ?x) (TRUE ?y)) (= ?x ?y)))", //
						"(exists (?x0 BOOL) (?y0 BOOL) (and (TRUE ?x0) (not (TRUE ?y0))))");

		testContainsAssumptionsPP(te, "FALSE↦TRUE ∈ Y", expectedAssumptions);
	}

	@Test
	public void testBoolAxiom() {
		final ITypeEnvironment te = defaultTe;
		final List<String> expectedAssumptions = Arrays
				.asList("(forall (?x BOOL) (MS ?x BOOLS))", //
						"(forall (?x0 BOOL) (?y BOOL) (iff (iff (TRUE ?x0) (TRUE ?y)) (= ?x0 ?y)))", //
						"(exists (?x1 BOOL) (?y0 BOOL) (and (TRUE ?x1) (not (TRUE ?y0))))", //
						"(forall (?x2 BOOL) (exists (?X PB) (and (MS ?x2 ?X) (forall (?y1 BOOL) (implies (MS ?y1 ?X) (= ?y1 ?x2))))))");
		testContainsAssumptionsPP(te, "a↦BOOL↦a ∈ Y", expectedAssumptions);
	}

	@Test
	public void testBoundBaseType() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(
				te,
				"∀z⦂ℙ(A×B),c⦂ℙ(A×B)·z=c",
				"(not (forall (?z PAB) (?c PAB) (?x A) (?x0 B) (iff (MS ?x ?x0 ?z) (MS ?x ?x0 ?c))))");
	}

	@Test
	public void testBoundBaseType2() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(
				te,
				"∀z⦂A×B,c⦂A×B·z=c",
				"(not (and (forall (?z A) (?z0 B) (?c A) (?c0 B) (= ?z ?c)) (forall (?z1 A) (?z2 B) (?c1 A) (?c2 B) (= ?z2 ?c2))))");
	}

	@Test
	public void testBoundBaseType3() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(
				te,
				"∀z⦂A,c⦂A·z↦c=c↦z",
				"(not (and (forall (?z A) (?c A) (= ?z ?c)) (forall (?z0 A) (?c0 A) (= ?c0 ?z0))))");
	}

	@Test
	public void testBoundBaseType4() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te, "∃ x ⦂ ℤ×ℤ×ℤ, X ⦂ ℙ(ℤ×ℤ×ℤ) · x ∈ X",
				"(not (exists (?x Int) (?x0 Int) (?x1 Int) (?X PZZZ) (MS ?x ?x0 ?x1 ?X)))");
	}

	@Test
	public void testBoundBaseType5() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te,
				"∃ x ⦂ ℙ(ℙ(ℤ)×ℙ(ℤ)), X ⦂ ℙ(ℙ(ℙ(ℤ)×ℙ(ℤ))) · x ∈ X",
				"(not (exists (?x PZZ) (?X PZZ0) (MS ?x ?X)))");
	}

	@Test
	public void testBoundBaseType6() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te,
				"∃ x ⦂ ℙ(ℙ(ℤ)×ℙ(ℤ)), X ⦂ ℙ(ℙ(ℙ(ℤ)×ℙ(ℤ))) · x ∈ X",
				"(not (exists (?x PZZ) (?X PZZ0) (MS ?x ?X)))");
	}

	@Test
	public void testBoundRightHandSide() {
		final ITypeEnvironment te = mTypeEnvironment( //
				"a", "ℙ(A)");
		testTranslateGoalPP(
				te,
				"∀z⦂ℙ(A),c⦂A·(c ∈ a)∧(c ∈ z)",
				"(not (and (forall (?z PA) (?c A) (MS ?c a)) (forall (?z0 PA) (?c0 A) (MS ?c0 ?z0))))");
	}
}
