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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTSignature;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.ast.theories.SMTTheory.Booleans;
import org.eventb.smt.ast.theories.SMTTheory.Ints;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.SMTThroughPP;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Ensure that translation from ppTrans produced predicates to SMT-LIB
 * predicates is correct.
 * 
 * @author Yoann Guyot
 * 
 */
public class TranslationTestsWithPP extends AbstractTests {
	protected static final ITypeEnvironment defaultTe;
	public static final SMTLogic defaultLogic;
	static {
		defaultTe = mTypeEnvironment("S", "ℙ(S)", "r", "ℙ(R)", "s", "ℙ(R)",
				"a", "ℤ", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		defaultLogic = new SMTLogic.SMTLogicPP(SMTLogic.UNKNOWN,
				Ints.getInstance(), Booleans.getInstance());
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
				"lemma", new ArrayList<Predicate>(), goalPredicate, "Z3");

		final SMTFormula formula = benchmark.getFormula();
		assertEquals(expectedFormula, formula.toString());
	}

	private void testContainsAssumptionsPP(final ITypeEnvironment te,
			final String inputGoal, final List<String> expectedAssumptions) {

		final Predicate goal = parse(inputGoal, te);

		assertTypeChecked(goal);

		final SMTBenchmark benchmark = SMTThroughPP.translateToSmtLibBenchmark(
				"lemma", new ArrayList<Predicate>(), goal, "Z3");

		final List<SMTFormula> assumptions = benchmark.getAssumptions();
		assertEquals(assumptionsString(assumptions),
				expectedAssumptions.size(), assumptions.size());
		for (final SMTFormula assumption : assumptions) {
			assertTrue(
					expectedAssumptionMessage(expectedAssumptions,
							assumption.toString()),
					expectedAssumptions.remove(assumption.toString()));
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
		final SMTSignature signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentFuns(signature, expectedFunctions, predString);
	}

	public static void testTypeEnvironmentSorts(final SMTLogic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignature signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentSorts(signature, expectedFunctions, predString);
	}

	protected static SMTSignature translateTypeEnvironment(
			final SMTLogic logic, final ITypeEnvironment iTypeEnv,
			final String ppPredStr) throws AssertionError {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv);

		assertTrue(
				TranslationTestsWithPP.producePPTargetSubLanguageError(ppPred),
				Translator.isInGoal(ppPred));

		return SMTThroughPP.translateTE(logic, ppPred, null);
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
				TranslationTestsWithPP.producePPTargetSubLanguageError(ppPred),
				Translator.isInGoal(ppPred));

		testTranslationV1_2(defaultLogic, ppPred, expectedSMTNode, failMessage,
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
	private static void testTranslationV1_2(final SMTLogic logic,
			final Predicate ppPred, final String expectedSMTNode,
			final String failMessage, final String solver) {
		final StringBuilder actualSMTNode = new StringBuilder();
		SMTThroughPP.translate(logic, ppPred, solver).toString(actualSMTNode,
				-1, false);

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
	 * "pred-quant"
	 */
	@Test
	public void testQuantifiers() {
		final ITypeEnvironment te = mTypeEnvironment("RR", "r ↔ s");
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
	@Ignore("")
	public void testQuantifiers2() {
		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV1_2(te, "∃  X ⦂ ℙ(A×A)· (∃ x · x↦x ∈ X)",
				"(exists (?X PAA) (?x A) (MS ?x ?x ?X))");

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
	 * notequal
	 */
	@Test
	@Ignore("Waiting for ppTrans to be updated")
	// TODO Re-add when ppTrans is updated
	public void testPredRelopNotEqual() {
		testTranslationV1_2Default("a ≠ b", "(not (= a b))");
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
		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ");
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
		final ITypeEnvironment te = mTypeEnvironment("int", "S", "SPZ",
				"S ↔ ℙ(ℤ)", "AZ", "ℤ ↔ ℙ(ℤ)");
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
		final ITypeEnvironment te = mTypeEnvironment("p", "S", "q", "S");

		testTranslationV1_2(te, "p = q", "(= p q)");
	}

	@Test
	public void testTRUELit() {
		final ITypeEnvironment te = mTypeEnvironment("f", "ℙ(BOOL)", "x",
				"BOOL");

		testTranslationV1_2(te, "x ∈ f", "(f x)");
	}

	@Test
	public void testTRUEPred() {
		final ITypeEnvironment te = mTypeEnvironment("B", "ℙ(BOOL)", "b",
				"BOOL", "c", "BOOL");

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
	@Ignore("Waiting for ppTrans to be updated")
	// TODO Readd when ppTrans is updated
	public void testSimplifyAnd() {
		testTranslationV1_2Default(
				"((a = b) ∧ (u = v) ∧ (a = b)) ∧ ((u = v) ∧ (a = b))",
				"(and (= a b) (iff u v))");
	}

	@Test
	@Ignore("Waiting for ppTrans to be updated")
	// TODO Re-add when ppTrans is updated
	public void testSimplifyImplies() {
		testTranslationV1_2Default("∀ x · x + 1 > 0 ⇒ ∀ y · y + 1 > 0", "true");
	}

	@Test
	public void testPredefinedAttributesSymbols() {
		final ITypeEnvironment te = mTypeEnvironment("assumption", "funs",
				"formula", "funs");

		testTranslationV1_2(te, "assumption = formula", "(= nf nf1)");
	}

	@Test
	public void testPredefinedAttributesSymbolsSorts() {
		final ITypeEnvironment te = mTypeEnvironment("if_then_else", "ℙ(NS)",
				"implies", "NS", "ite", "NS");

		final Set<String> expectedSorts = new HashSet<String>();

		expectedSorts.add("PN");
		expectedSorts.add("Int");
		expectedSorts.add("PZ");
		expectedSorts.add("BOOL");
		expectedSorts.add("PB");
		expectedSorts.add("NS");

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"implies = ite");

	}

	@Test
	public void testReservedSymbolsAndKeywords() {
		final ITypeEnvironment te = mTypeEnvironment("distinct", "false", "nf",
				"false");

		testTranslationV1_2(te, "distinct = flet", "(= nf1 nf)");
	}

	@Test
	public void testpredefinedAttributesSymbolsSorts() {
		final ITypeEnvironment te = mTypeEnvironment("status", "ℙ(logic)",
				"extrasorts", "logic", "extrafuns", "logic");

		final Set<String> expectedSorts = new HashSet<String>();

		expectedSorts.add("NS"); // logic
		expectedSorts.add("Int");
		expectedSorts.add("PZ");
		expectedSorts.add("BOOL");
		expectedSorts.add("PB");
		expectedSorts.add("PL"); // ℙ(logic)

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"extrasorts = extrafuns");
	}

	@Test
	public void testpredefinedAttributesSymbolsFuns() {
		final ITypeEnvironment te = mTypeEnvironment("status", "ℙ(logic)",
				"extrasorts", "logic", "extrafuns", "logic");

		final Set<String> expectedFuns = new HashSet<String>();

		expectedFuns.add("(BOOLS PB)");
		expectedFuns.add("(mod Int Int Int)");
		expectedFuns.add("(nf0 NS)");
		expectedFuns.add("(INTS PZ)");
		expectedFuns.add("(expn Int Int Int)");
		expectedFuns.add("(divi Int Int Int)");
		expectedFuns.add("(nf1 PL)");
		expectedFuns.add("(nf NS)");

		testTypeEnvironmentFuns(defaultLogic, te, expectedFuns,
				"extrasorts = extrafuns");
	}

	@Test
	public void testReservedSymbolsAndKeywordsSorts() {
		final ITypeEnvironment te = mTypeEnvironment("if_then_else", "ℙ(NS)",
				"implies", "NS", "ite", "NS");

		final Set<String> expectedSorts = new HashSet<String>();

		expectedSorts.add("PN");
		expectedSorts.add("Int");
		expectedSorts.add("PZ");
		expectedSorts.add("BOOL");
		expectedSorts.add("PB");
		expectedSorts.add("NS");

		testTypeEnvironmentSorts(defaultLogic, te, expectedSorts,
				"implies = ite");
	}

	@Test
	public void testReservedSymbolsAndKeywordsFuns() {
		final ITypeEnvironment te = mTypeEnvironment("if_then_else", "ℙ(NS)",
				"implies", "NS", "ite", "NS");

		final Set<String> expectedFuns = new HashSet<String>();

		expectedFuns.add("(BOOLS PB)");
		expectedFuns.add("(nf0 NS)");
		expectedFuns.add("(mod Int Int Int)");
		expectedFuns.add("(INTS PZ)");
		expectedFuns.add("(expn Int Int Int)");
		expectedFuns.add("(NS0 PN)");
		expectedFuns.add("(divi Int Int Int)");
		expectedFuns.add("(nf NS)");

		testTypeEnvironmentFuns(defaultLogic, te, expectedFuns, "implies = ite");
	}

	@Test
	public void testNumeral() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		testTranslateGoalPP(te, "n ≥ 1", "(<= 1 n)");
	}

	@Test
	public void testQuantifier() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		testTranslateGoalPP(te, "∀ x · x + 1 ∈ S",
				"(forall (?x Int) (exists (?x0 Int) (and (= ?x0 (+ ?x 1)) (S ?x0))))");
	}

	@Test
	public void testIntAxiom() {
		final ITypeEnvironment te = defaultTe;
		final List<String> expectedAssumptions = new ArrayList<String>();
		expectedAssumptions.add("(forall (?x Int) (MS ?x INTS))");
		expectedAssumptions
				.add("(forall (?A PZ) (?B PZ) (implies (forall (?x0 Int) (iff (MS ?x0 ?A) (MS ?x0 ?B))) (= ?A ?B)))");
		expectedAssumptions
				.add("(forall (?x1 Int) (exists (?X PZ) (and (MS ?x1 ?X) (forall (?y Int) (implies (MS ?y ?X) (= ?y ?x1))))))");
		// expectedAssumptions
		// .add("(forall (?A0 PZZ) (?B0 PZZ) (implies (forall (?x2 Int) (?x3 PZ) (iff (MS0 ?x2 ?x3 ?A0) (MS0 ?x2 ?x3 ?B0))) (= ?A0 ?B0)))");
		// expectedAssumptions
		// .add("(forall (?x4 Int) (?x5 PZ) (exists (?X0 PZZ) (and (MS0 ?x4 ?x5 ?X0) (forall (?y0 Int) (?y1 PZ) (implies (MS0 ?y0 ?y1 ?X0) (and (= ?y0 ?x4) (= ?y1 ?x5)))))))");

		testContainsAssumptionsPP(te, "a↦ℤ ∈ AZ", expectedAssumptions);
	}

	@Test
	public void testTrueAxiom() {
		final ITypeEnvironment te = mTypeEnvironment("Y", "ℙ(BOOL×BOOL)");
		final List<String> expectedAssumptions = new ArrayList<String>();
		expectedAssumptions
				.add("(forall (?x BOOL) (?y BOOL) (iff (iff (TRUE ?x) (TRUE ?y)) (= ?x ?y)))");
		expectedAssumptions
				.add("(exists (?x0 BOOL) (?y0 BOOL) (and (TRUE ?x0) (not (TRUE ?y0))))");
		// expectedAssumptions
		// .add("(forall (?A PBB) (?B PBB) (implies (forall (?x3 BOOL) (?x4 BOOL) (iff (MS ?x3 ?x4 ?A) (MS ?x3 ?x4 ?B))) (= ?A ?B)))");
		// expectedAssumptions
		// .add("(forall (?x5 BOOL) (?x6 BOOL) (exists (?X PBB) (and (MS ?x5 ?x6 ?X) (forall (?y1 BOOL) (?y2 BOOL) (implies (MS ?y1 ?y2 ?X) (and (= ?y1 ?x5) (= ?y2 ?x6)))))))");

		testContainsAssumptionsPP(te, "FALSE↦TRUE ∈ Y", expectedAssumptions);
	}

	@Test
	public void testBoolAxiom() {
		final ITypeEnvironment te = defaultTe;
		final List<String> expectedAssumptions = new ArrayList<String>();
		expectedAssumptions.add("(forall (?x BOOL) (MS ?x BOOLS))");
		expectedAssumptions
				.add("(forall (?x0 BOOL) (?y BOOL) (iff (iff (TRUE ?x0) (TRUE ?y)) (= ?x0 ?y)))");
		expectedAssumptions
				.add("(exists (?x1 BOOL) (?y0 BOOL) (and (TRUE ?x1) (not (TRUE ?y0))))");
		expectedAssumptions
				.add("(forall (?A PB) (?B PB) (implies (forall (?x2 BOOL) (iff (MS ?x2 ?A) (MS ?x2 ?B))) (= ?A ?B)))");
		expectedAssumptions
				.add("(forall (?x3 BOOL) (exists (?X PB) (and (MS ?x3 ?X) (forall (?y1 BOOL) (implies (MS ?y1 ?X) (= ?y1 ?x3))))))");
		testContainsAssumptionsPP(te, "a↦BOOL↦a ∈ Y", expectedAssumptions);
	}

	@Test
	public void testBoundBaseType() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te, "∀z⦂ℙ(A×B),c⦂ℙ(A×B)·z=c",
				"(forall (?z PAB) (?c PAB) (forall (?x AB) (iff (MS ?x ?z) (MS ?x ?c))))");
	}

	@Test
	public void testBoundBaseType2() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te, "∀z⦂A×B,c⦂A×B·z=c",
				"(forall (?z A) (?z0 B) (?c A) (?c0 B) (and (= ?z ?c) (= ?z0 ?c0)))");
	}

	@Test
	public void testBoundBaseType3() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te, "∀z⦂A,c⦂A·z↦c=c↦z",
				"(forall (?z A) (?c A) (and (= ?z ?c) (= ?c ?z)))");
	}

	@Test
	public void testBoundBaseType4() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te, "∃ x ⦂ ℤ×ℤ×ℤ, X ⦂ ℙ(ℤ×ℤ×ℤ) · x ∈ X",
				"(exists (?x Int) (?x0 Int) (?x1 Int) (?X PZZZ) (MS ?x ?x0 ?x1 ?X))");
	}

	@Test
	public void testBoundBaseType5() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te,
				"∃ x ⦂ ℙ(ℙ(ℤ)×ℙ(ℤ)), X ⦂ ℙ(ℙ(ℙ(ℤ)×ℙ(ℤ))) · x ∈ X",
				"(exists (?x PZZ) (?X PZZ0) (MS ?x ?X))");
	}

	@Test
	public void testBoundBaseType6() {
		final ITypeEnvironment te = mTypeEnvironment();
		testTranslateGoalPP(te,
				"∃ x ⦂ ℙ(ℙ(ℤ)×ℙ(ℤ)), X ⦂ ℙ(ℙ(ℙ(ℤ)×ℙ(ℤ))) · x ∈ X",
				"(exists (?x PZZ) (?X PZZ0) (MS ?x ?X))");
	}

	@Test
	public void testBoundRightHandSide() {
		final ITypeEnvironment te = mTypeEnvironment("a", "ℙ(A)");
		testTranslateGoalPP(te, "∀z⦂ℙ(A),c⦂A·(c ∈ a)∧(c ∈ z)",
				"(forall (?z PA) (?c A) (and (MS ?c a) (MS ?c ?z)))");
	}
}
