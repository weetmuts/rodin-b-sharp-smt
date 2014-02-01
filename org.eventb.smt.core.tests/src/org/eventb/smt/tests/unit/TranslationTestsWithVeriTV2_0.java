/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests.unit;

import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.seqprover.transformer.SimpleSequents.make;
import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.SolverKind.VERIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.ast.SMTFormula;
import org.eventb.smt.core.internal.ast.SMTSignatureV2_0;
import org.eventb.smt.core.internal.ast.SMTSignatureV2_0Verit;
import org.eventb.smt.core.internal.ast.macros.SMTMacro;
import org.eventb.smt.core.internal.ast.macros.SMTMacroFactoryV2_0.SMTVeriTOperatorV2_0;
import org.eventb.smt.core.internal.ast.macros.SMTPredefinedMacro;
import org.eventb.smt.core.internal.ast.theories.Logic;
import org.eventb.smt.core.internal.ast.theories.Logic.QF_AUFLIAv2_0;
import org.eventb.smt.core.internal.ast.theories.Theory;
import org.eventb.smt.core.internal.ast.theories.TheoryV2_0;
import org.eventb.smt.core.internal.ast.theories.VeriTBooleansV2_0;
import org.eventb.smt.core.internal.translation.SMTThroughVeriT;
import org.eventb.smt.tests.AbstractTests;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Ensure that translation to veriT extended version of SMT-LIB is correct
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class TranslationTestsWithVeriTV2_0 extends AbstractTests {
	protected static final ITypeEnvironment defaultTe, simpleTe, cdisTe,
			powpowTe;
	protected static final Logic defaultLogic, veriTLogicWithBool;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";

	static {
		simpleTe = mTypeEnvironment(//
				"e", "ℙ(S)", "f", "ℙ(S)", "g", "S", "AB", "ℤ ↔ ℤ");

		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ", "b",
				"ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");

		cdisTe = mTypeEnvironment(//
				"S", "ℙ(S)", "R", "ℙ(R)", "f", "S ↔ R", "x", "S", "y", "R");

		powpowTe = mTypeEnvironment(//
				"S", "ℙ(S)", "R", "ℙ(R)", "e", "ℙ(ℙ(S) ↔ ℤ) ↔ ℙ(R)");

		defaultLogic = Logic.QF_AUFLIAv2_0VeriT.getInstance();
		veriTLogicWithBool = new Logic.SMTLogicVeriT(Logic.UNKNOWN,
				TheoryV2_0.Ints.getInstance(),
				VeriTBooleansV2_0.getInstance());
	}

	private static void testTranslationV2_0Default(final String predStr,
			final String expectedSMTNode) {
		testTranslationV2_0(defaultTe, predStr, expectedSMTNode,
				defaultFailMessage, VERIT.toString(), defaultLogic);
	}

	private static void testTranslationV2_0ChooseLogic(
			final ITypeEnvironment typeEnvironment, final String predStr,
			final String expectedSMTNode, final Logic logic) {
		testTranslationV2_0(typeEnvironment, predStr, expectedSMTNode,
				defaultFailMessage, VERIT.toString(), logic);
	}

	private static void testTranslationV2_0VerDefaultSolver(
			final ITypeEnvironment typeEnvironment, final String ppPredStr,
			final String expectedSMTNode) {
		testTranslationV2_0(typeEnvironment, ppPredStr, expectedSMTNode,
				defaultFailMessage, VERIT.toString());
	}

	/**
	 * Parses a Predicate Calculus formula, (builds hypotheses and goal) and
	 * tests its SMT-LIB translation
	 * 
	 * @param iTypeEnv
	 *            Input type environment
	 * @param predStr
	 *            String representation of the input predicate
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV2_0(final ITypeEnvironment iTypeEnv,
			final String predStr, final String expectedSMTNode,
			final String failMessage, final String solver)
			throws AssertionError {
		testTranslationV2_0(iTypeEnv, predStr, expectedSMTNode, failMessage,
				solver, defaultLogic);
	}

	private static void testTranslationV2_0(final ITypeEnvironment iTypeEnv,
			final String predStr, final String expectedSMTNode,
			final String failMessage, final String solver, final Logic logic)
			throws AssertionError {
		final Predicate pred = parse(predStr, iTypeEnv.makeBuilder());

		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(pred);

		testTranslationV2_0Verit(pred, expectedSMTNode, failMessage, solver,
				logic);
	}

	private void testContainsNotPredefinedMacros(final ITypeEnvironment te,
			final String inputGoal, final Map<String, String> expectedMacros) {
		final Predicate goal = parse(inputGoal, te.makeBuilder());

		// Type check goal and hypotheses
		assertTypeChecked(goal);

		final ISimpleSequent sequent = make((List<Predicate>) null, goal,
				goal.getFactory());

		final SMTThroughVeriT translator = new SMTThroughVeriT(V2_0);
		final SMTBenchmark benchmark = translate(translator, "lemma", sequent);

		final SMTSignatureV2_0 signature = (SMTSignatureV2_0) benchmark
				.getSignature();
		final SMTSignatureV2_0Verit sigverit = (SMTSignatureV2_0Verit) signature;
		final Set<SMTMacro> sets = sigverit.getMacros();
		for (final SMTMacro macro : sets) {
			if (!(macro instanceof SMTPredefinedMacro)) {
				final String macroName = macro.getMacroName();
				assertTrue(macroName.toString(), expectedMacros.keySet()
						.contains(macroName));
				final String macroBody = expectedMacros.get(macroName);
				assertEquals(macroBody, macro.toString());
			}
		}
	}

	/**
	 * Tests the SMT-LIB translation with the given Predicate Calculus formula
	 * 
	 * @param ppred
	 *            Input Predicate Calculus formula
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV2_0Verit(final Predicate ppred,
			final String expectedSMTNode, final String failMessage,
			final String solver) {
		testTranslationV2_0Verit(ppred, expectedSMTNode, failMessage, solver,
				defaultLogic);
	}

	private static void testTranslationV2_0Verit(final Predicate ppred,
			final String expectedSMTNode, final String failMessage,
			final String solver, final Logic logic) {

		final StringBuilder actualSMTNode = new StringBuilder();

		SMTThroughVeriT.translate(logic, ppred, V2_0, ppred.getFactory())
				.toString(actualSMTNode, -1, false);
		assertEquals(failMessage, expectedSMTNode, actualSMTNode.toString());
	}

	public static void testTypeEnvironmentFuns(final Logic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignatureV2_0 signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentFuns(signature, expectedFunctions, predString);
	}

	private void testContainsAssumptionsVeriT(final ITypeEnvironment te,
			final String inputGoal, final List<String> expectedAssumptions) {

		final Predicate goal = parse(inputGoal, te.makeBuilder());

		assertTypeChecked(goal);

		final ISimpleSequent sequent = make((List<Predicate>) null, goal,
				goal.getFactory());

		final SMTThroughVeriT translator = new SMTThroughVeriT(V2_0);
		final SMTBenchmark benchmark = translate(translator, "lemma", sequent);

		final List<SMTFormula> assumptions = benchmark.getAssumptions();
		assertEquals(assumptionsString(assumptions),
				expectedAssumptions.size(), assumptions.size());
		for (final SMTFormula assumption : assumptions) {
			assertTrue(assumption.toString(),
					expectedAssumptions.contains(assumption.toString()));
		}
	}

	public static void testTypeEnvironmentSorts(final Logic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignatureV2_0 signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentSorts(signature, expectedFunctions, predString);
	}

	public static void testTypeEnvironmentPreds(final Logic logic,
			final ITypeEnvironment te, final Set<String> expectedFunctions,
			final String predString) {
		final SMTSignatureV2_0 signature = translateTypeEnvironment(logic, te,
				predString);
		testTypeEnvironmentPreds(signature, expectedFunctions, predString);
	}

	protected static SMTSignatureV2_0 translateTypeEnvironment(
			final Logic logic, final ITypeEnvironment iTypeEnv,
			final String ppPredStr) throws AssertionError {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv.makeBuilder());
		return (SMTSignatureV2_0) SMTThroughVeriT.translateTE(logic, ppPred,
				V2_0, ppPred.getFactory());
	}

	@Test
	public void testWellFormedParenthesis() {
		SMTVeriTOperatorV2_0[] values = SMTVeriTOperatorV2_0.values();
		for (SMTVeriTOperatorV2_0 op : values) {
			SMTPredefinedMacro macro = op.getSymbol();
			String macroString = macro.toString();
			int count = 0;
			for (int i = 0; i < macroString.length(); i++) {
				char c = macroString.charAt(i);
				if (c == '(')
					count++;
				else if (c == ')')
					count--;
				assertTrue(count >= 0);
			}
			assertTrue(count == 0);
		}
	}

	@Test
	public void testTypeEnvironmentFunctionSimpleTe() {
		final Set<String> expectedFunctions = new HashSet<String>(
				Arrays.asList( //
						"INTS () PZ", //
						"g () S", //
						"BOOLS () PB"));

		testTypeEnvironmentFuns(defaultLogic, simpleTe, expectedFunctions,
				"g = g");
	}

	// "∀x·x∈s"
	@Test
	public void testTypeEnvironmentFunctionWithKBool() {
		final Set<String> expectedFunctions = new HashSet<String>(
				Arrays.asList( //
						"INTS () PZ", //
						"BOOLS () PB" //
				));

		testTypeEnvironmentFuns(defaultLogic, mTypeEnvironment("s", "ℙ(ℤ)"),
				expectedFunctions, "∀x·x∈s ∧ TRUE ∈ BOOL");
	}

	@Test
	public void testTypeEnvironmentFunctionWithBool() {
		final Set<String> expectedFunctions = new HashSet<String>(
				Arrays.asList( //
						"INTS () PZ", //
						"BOOLS () PB" //
				));

		testTypeEnvironmentFuns(defaultLogic, mTypeEnvironment(),
				expectedFunctions, "BOOL = {TRUE,FALSE}");
	}

	@Test
	// TODO remove the exception expectation when veriT translation can handle
	// this kind of expression
	public void testpowpowTe() {
		final Set<String> expectedFunctions = new HashSet<String>(
				Arrays.asList(""));
		try {
			testTypeEnvironmentFuns(defaultLogic, powpowTe, expectedFunctions,
					"e = e");
		} catch (Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			return;
		}
		fail("Expected " + IllegalArgumentException.class
				+ " but got no error.");
	}

	/**
	 * Testing rule 7
	 */
	@Test
	@Ignore("Pair is not generated")
	public void testTypeEnvironmenSortSimpleTe() {
		final Set<String> expectedSorts = new HashSet<String>(Arrays.asList( //
				"Bool", //
				"PB", //
				"S", //
				"Pair",//
				"PZ",//
				"Int"));

		testTypeEnvironmentSorts(defaultLogic, simpleTe, expectedSorts, "g = g");
	}

	/**
	 * Testing rules 4, 5 and 6
	 */
	@Test
	public void testTypeEnvironmentPredicateSimpleTePreds() {
		final Set<String> expectedPredicates = new HashSet<String>(
				Arrays.asList("S0 (S) Bool"));

		testTypeEnvironmentPreds(defaultLogic, simpleTe, expectedPredicates,
				"g = g");
	}

	@Test
	@Ignore("Pair is not generated")
	public void testTypeEnvironmentPredicateSimpleTeSorts() {
		final Set<String> expectedSorts = new HashSet<String>(Arrays.asList(
				"Bool", "PB", "S", "Pair", "PZ", "Int"));

		testTypeEnvironmentSorts(defaultLogic, simpleTe, expectedSorts, "g = g");
	}

	@Test
	public void testTypeEnvironmentPredicateSimpleTeFuns() {
		final Set<String> expectedPredicates = new HashSet<String>(
				Arrays.asList(//
						"g () S", //
						"INTS () PZ",//
						"BOOLS () PB"));

		testTypeEnvironmentFuns(defaultLogic, simpleTe, expectedPredicates,
				"g = g");
	}

	@Test
	public void testTypeEnvironmentPredicateDefaultTe() {
		final Set<String> expectedPredicates = new HashSet<String>(
				Arrays.asList("AB ((Pair Int Int)) Bool"));

		testTypeEnvironmentPreds(defaultLogic, defaultTe, expectedPredicates,
				"AB = AB");
	}

	@Test
	// TODO: Check if it is not necessary to create fresh names for macros.
	public void testReservedQNames() {
		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te,
				"∀UNION_0⦂UNION_1·UNION_0 ∈ {UNION_0}",
				"(forall ((UNION_0 UNION_1)) (in UNION_0 enum))");
	}

	/**
	 * "pred-ass"
	 */
	@Test
	public void testPredAssop() {

		testTranslationV2_0Default("(u = v)", "(= u v)");

		/**
		 * land
		 */
		testTranslationV2_0Default("(a = b) ∧ (u = v)", "(and (= a b) (= u v))");
		/**
		 * land (multiple predicates)
		 */
		testTranslationV2_0Default("(a = b) ∧ (u = v) ∧ (r = s)",
				"(and (= a b) (= u v) (= r s))");
		/**
		 * lor
		 */
		testTranslationV2_0Default("(a = b) ∨ (u = v)", "(or (= a b) (= u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV2_0Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (= u v) (= r s))");
	}

	@Test
	public void testReservedMacroName() {
		final ITypeEnvironment te = mTypeEnvironment(//
				"in", "emptyset", "range", "emptyset");

		testTranslationV2_0VerDefaultSolver(te, "in = range", "(= in0 range0)");
	}

	/**
	 * "pred-boolequ with constants only"
	 */
	@Test
	public void testPredBoolEquCnst() {
		testTranslationV2_0Default("u = v", "(= u v)");
	}

	/**
	 * "pred-boolequ"
	 */
	@Test
	public void testPredBoolEqu() {
		final Logic defaultPlusBooleanLogic = new Logic.SMTLogicVeriT(
				QF_AUFLIAv2_0.getInstance().getName(), new Theory[] {
						TheoryV2_0.Ints.getInstance(),
						VeriTBooleansV2_0.getInstance() });

		testTranslationV2_0ChooseLogic(defaultTe, "u = TRUE", "(= u TRUE)",
				defaultPlusBooleanLogic);
		testTranslationV2_0ChooseLogic(defaultTe, "TRUE = u", "(= TRUE u)",
				defaultPlusBooleanLogic);
	}

	/**
	 * "pred-bin" in ppTrans abstract syntax
	 */
	@Test
	public void testPredBinop() {
		/**
		 * limp
		 */
		testTranslationV2_0Default("(a < b ∧ b < c) ⇒ a < c",
				"(=> (and (< a b) (< b c)) (< a c))");
		/**
		 * leqv
		 */
		testTranslationV2_0Default("(a ≤ b ∧ b ≤ a) ⇔ a = b",
				"(= (and (<= a b) (<= b a)) (= a b))");
	}

	/**
	 * "pred-una" Testing rule 14:
	 */
	@Test
	public void testPredUna() {
		testTranslationV2_0Default("¬ ((a ≤ b ∧ b ≤ c) ⇒ a < c)",
				"(not (=> (and (<= a b) (<= b c)) (< a c)))");
	}

	/**
	 * "pred-lit"
	 */
	@Test
	public void testPredLit() {
		/**
		 * btrue
		 */
		testTranslationV2_0Default("⊤", "true");
		/**
		 * bfalse
		 */
		testTranslationV2_0Default("⊥", "false");
	}

	/**
	 * "pred-rel"
	 */
	@Test
	public void testPredRelop() {
		/**
		 * equal (identifiers of type ℤ)
		 */
		testTranslationV2_0Default("a = b", "(= a b)");
		/**
		 * equal (integer numbers)
		 */
		testTranslationV2_0Default("42 = 42", "(= 42 42)");
		/**
		 * notequal
		 */
		testTranslationV2_0Default("a ≠ b", "(not (= a b))");
		/**
		 * lt
		 */
		testTranslationV2_0Default("a < b", "(< a b)");
		/**
		 * le
		 */
		testTranslationV2_0Default("a ≤ b", "(<= a b)");
		/**
		 * gt
		 */
		testTranslationV2_0Default("a > b", "(> a b)");
		/**
		 * ge
		 */
		testTranslationV2_0Default("a ≥ b", "(>= a b)");
	}

	/**
	 * Arithmetic expressions binary operations: cf. "a-expr-bin"
	 */
	@Test
	public void testArithExprBinop() {
		/**
		 * minus
		 */
		testTranslationV2_0Default("a − b = c", "(= (- a b) c)");
		/**
		 * equal (a-expr-bin)
		 */
		testTranslationV2_0Default("a − b = a − c", "(= (- a b) (- a c))");
	}

	@Test
	@Ignore("Unsupported operators")
	public void testArithExprBinopExponentialUnsupported() {
		/**
		 * expn
		 */
		testTranslationV2_0Default("a ^ b = c", "(= (expn a b) c)");
		/**
		 * div
		 */
		testTranslationV2_0Default("a ÷ b = c", "(= (divi a b) c)");

		/**
		 * mod
		 */
		testTranslationV2_0Default("a mod b = c", "(= (mod a b) c)");
	}

	/**
	 * Arithmetic expressions associative operations: cf. "a-expr-ass"
	 */
	@Test
	public void testArithExprAssnop() {
		/**
		 * plus
		 */
		testTranslationV2_0Default("a + c + b = a + b + c",
				"(= (+ a c b) (+ a b c))");
		/**
		 * mul
		 */
		testTranslationV2_0Default("a ∗ b ∗ c = a ∗ c ∗ b",
				"(= (* a b c) (* a c b))");
	}

	@Test
	public void testAssociativeExpression() {
		testTranslationV2_0Default("s = {}", "(= s emptyset)");
	}

	/**
	 * Arithmetic expressions unary operations: cf. "a-expr-una"
	 */
	@Test
	public void testArithExprUnop() {
		/**
		 * uminus (right child)
		 */
		testTranslationV2_0Default("a = −b", "(= a (- b))");
		/**
		 * uminus (left child)
		 */
		testTranslationV2_0Default("−a = b", "(= (- a) b)");
	}

	/**
	 * "pred-in" This test should not happen with ppTrans; The
	 */

	@Test
	public void testPredIn() {
		testTranslationV2_0Default("a ∈ A", "(in a A)");
		testTranslationV2_0Default("a↦b ∈ AB", "(in (pair a b) AB)");
	}

	@Test
	@Ignore("Type ℤ×ℙ(BOOL)×ℙ(BOOL): sets of sets are not supported yet")
	public void testPredIn2() {
		testTranslationV2_0Default("a↦BOOL↦BOOL ∈ X", "(X a BOOLS BOOLS)");
		testTranslationV2_0Default("a↦BOOL↦a ∈ Y", "(Y a BOOLS a)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV2_0Default("r = s", "(= r s)");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		testTranslationV2_0Default("p = q", "(= p q)");
	}

	@Test
	public void testDynamicStableLSR_081014_20_hyp1() {

		final ITypeEnvironment te = mTypeEnvironment("S", "ℙ(S)", "P",
				"ℙ(S × S)", "Q", "ℙ(S × S)", "k", "S × S", "m", "S", "n", "S");

		testTranslationV2_0VerDefaultSolver(te, "¬ k = m ↦ n",
				"(not (= k (pair m n)))");
	}

	@Test
	public void testRule14() {

		testTranslationV2_0Default("(AB)∼ = AB", "(= (inv AB) AB)");
		/**
		 * inverse(SMT)/converse(EventB)
		 */
		testTranslationV2_0Default("AB = (AB)∼", "(= AB (inv AB))");
		/**
		 * not
		 */
		testTranslationV2_0Default("\u00ac(p = q)", "(not (= p q))");

		/**
		 * uminus
		 */
		testTranslationV2_0Default("a = (−b)", "(= a (- b))");

		/**
		 * id
		 */
		testTranslationV2_0Default("AB = id", "(= AB id)");

		/**
		 * dom
		 */
		testTranslationV2_0Default("a ∈ dom(AB)", "(in a (dom AB))");

		/**
		 * ran
		 */
		testTranslationV2_0Default("b ∈ ran(AB)", "(in b (ran AB))");

	}

	@Test
	public void testExistsRule17() {
		/**
		 * exists
		 */
		testTranslationV2_0Default("∃x·x∈s", "(exists ((x R)) (in x s))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV2_0Default("∃x,y·x∈s∧y∈s",
				"(exists ((x R) (y R)) (and (in x s) (in y s)))");
	}

	@Test
	public void testForallRule17() {
		/**
		 * forall
		 */
		testTranslationV2_0Default("∀x·x∈s", "(forall ((x R)) (in x s))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV2_0Default("∀x,y·x∈s∧y∈s",
				"(forall ((x R) (y R)) (and (in x s) (in y s)))");

	}

	@Test
	public void testForallRule17Part2() {
		/**
		 * forall (multiple identifiers)
		 */
		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", defaultTe.makeBuilder());
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = base.getFactory().makeQuantifiedPredicate(FORALL,
				bids, base.getPredicate(), null);
		testTranslationV2_0Verit(p,
				"(forall ((x R) (x0 R)) (and (in x s) (in x0 s)))",
				"twice same decl", VERIT.toString());
	}

	@Test
	public void testRule16() {
		testTranslationV2_0Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∧ (a + b + c = b) ∧  (a ∗ b ∗ c = 0)",
				"(and (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");

		testTranslationV2_0Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∨ (a + b + c = b) ∨  (a ∗ b ∗ c = 0)",
				"(or (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testAssociativeExpressionsUnionAndInter() {
		final ITypeEnvironment tpe = mTypeEnvironment("A", "ℙ(ℤ)", "B", "ℙ(ℤ)",
				"C", "ℙ(ℤ)", "D", "ℙ(ℤ)", "E", "ℙ(ℤ)");
		testTranslationV2_0VerDefaultSolver(tpe, "A ∪ B ∪ C ∪ D = E",
				"(= (union (union (union A B) C) D) E)");

		testTranslationV2_0VerDefaultSolver(tpe, "A ∩ B ∩ C ∩ D = E",
				"(= (inter (inter (inter A B) C) D) E)");
	}

	@Test
	public void testAssociativeFcompAndOvr() {
		final ITypeEnvironment tpe = mTypeEnvironment("A", "ℤ ↔ ℤ", "B",
				"ℤ ↔ ℤ", "C", "ℤ ↔ ℤ", "D", "ℤ ↔ ℤ", "E", "ℤ ↔ ℤ");

		testTranslationV2_0VerDefaultSolver(tpe,
				"A \u003b B \u003b C \u003b D = E",
				"(= (fcomp (fcomp (fcomp A B) C) D) E)");

		testTranslationV2_0VerDefaultSolver(tpe,
				"A \ue103 B \ue103 C \ue103 D = E",
				"(= (ovr (ovr (ovr A B) C) D) E)");
	}

	@Test
	public void testRule15SetMinusUnionInter() {

		testTranslationV2_0Default("(A ∖ A) ⊂ (A ∪ A)",
				"(subset (setminus A A) (union A A))");

		testTranslationV2_0Default("(A ∩ A) ⊂ (A ∪ A)",
				"(subset (inter A A) (union A A))");

	}

	@Test
	// FIXME: This test must be changed after recoding POW rule
	/**
	 * (subseteq A A) should be expected, but while POW can not be translated, false is
	 * returned instead
	 */
	public void testPow() {
		testTranslationV2_0Default("A ∈ ℙ(A)", "false");
	}

	@Test
	public void testRule15A() {
		/**
		 * ∈ , ⊆ , ⊂
		 */
		testTranslationV2_0Default("(a ∈ A) ∧ (A ⊆ A)",
				"(and (in a A) (subseteq A A))");
	}

	@Test
	public void testRule15B() {
		/**
		 * < , > , ⇒ , =
		 */
		testTranslationV2_0Default("(a < b ∧ b > c) ⇒ a = c",
				"(=> (and (< a b) (> b c)) (= a c))");
	}

	@Test
	public void testRule15Functions() {

		testTranslationV2_0Default("AB ∈ (A↔A)", "(in AB (rel A A))");

		testTranslationV2_0Default("AB ∈ (A→A)", "(in AB (tfun A A))");

		testTranslationV2_0Default("AB ∈ (A↣A)", "(in AB (tinj A A))");

		testTranslationV2_0Default("AB ∈ (A \u2916 A)", "(in AB (bij A A))");

		testTranslationV2_0Default("AB ∈ (A⤔A)", "(in AB (pinj A A))");

		testTranslationV2_0Default("AB ∈ (A↠A)", "(in AB (tsur A A))");

		testTranslationV2_0Default("AB ∈ (A⤀A)", "(in AB (psur A A))");

		testTranslationV2_0Default("AB ∈ (A⇸A)", "(in AB (pfun A A))");
	}

	@Test
	public void testRule15RelationOverridingCompANdCP() {
		/**
		 * relation overriding
		 */
		testTranslationV2_0Default("(AB \ue103 AB) = (AB \ue103 AB)",
				"(= (ovr AB AB) (ovr AB AB))");

		testTranslationV2_0Default("(AB \u003b AB) = (AB \u003b AB)",
				"(= (fcomp AB AB) (fcomp AB AB))");

		testTranslationV2_0Default("(AB \u2218 AB) = (AB \u2218 AB)",
				"(= (bcomp AB AB) (bcomp AB AB))");
	}

	@Test
	public void testRule15CartesianProductAndIntegerRange() {

		testTranslationV2_0Default("(A × A) = (A × A)",
				"(= (prod A A) (prod A A))");

		testTranslationV2_0Default("(a ‥ a) = (a ‥ a)",
				"(= (range a a) (range a a))");
	}

	@Test
	public void testRule15RestrictionsAndSubstractions() {

		testTranslationV2_0Default("(A ◁ AB) = (A ◁ AB)",
				"(= (domr A AB) (domr A AB))");

		testTranslationV2_0Default("(A ⩤ AB) = (A ⩤ AB)",
				"(= (doms A AB) (doms A AB))");

		testTranslationV2_0Default("(AB ▷ A) = (AB ▷ A)",
				"(= (ranr AB A) (ranr AB A))");

		testTranslationV2_0Default("(AB ⩥ A) = (AB ⩥ A)",
				"(= (rans AB A) (rans AB A))");
	}

	@Test
	public void testRule18() {

		testTranslationV2_0Default("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}",
				"(= cset cset0)");

		testTranslationV2_0Default("{a∣a ≥ 0} = A", "(= cset A)");
	}

	@Test
	public void testRule19() {
		testTranslationV2_0Default("{0 ↦ 1,1 ↦ 2} = {0 ↦ 1,2 ↦ 3}",
				"(= enum enum0)");

		testTranslationV2_0Default("{0,1,2,3,4} = A", "(= enum A)");
	}

	@Test
	public void testRule20() {

		testTranslationV2_0Default("(λx·x>0 ∣ x+x) = ∅", "(= cset emptyset)");
	}

	@Test
	public void testRule21() {
		final Logic defaultPlusBooleanLogic = new Logic.SMTLogicVeriT(
				QF_AUFLIAv2_0.getInstance().getName(), new Theory[] {
						TheoryV2_0.Ints.getInstance(),
						VeriTBooleansV2_0.getInstance() });

		testTranslationV2_0ChooseLogic(defaultTe, "bool(⊤) ∈ BOOL",
				"(in (ite true TRUE FALSE) BOOLS)", defaultPlusBooleanLogic);
	}

	@Test
	public void testRule22and23() {

		testTranslationV2_0Default("min({2,3}) = min({2,3})",
				"(= ismin_var ismin_var0)");

		testTranslationV2_0Default("max({2,3}) = max({2,3})",
				"(= ismax_var ismax_var0)");
	}

	@Test
	public void testRule24() {
		testTranslationV2_0Default("finite({1,2,3})", "finite_p");
	}

	@Test
	public void testRule25() {
		testTranslationV2_0Default("card({1,2,3}) = card({1,2,3})",
				"(= card_k card_k0)");
	}

	@Test
	public void testKSuccAndKPred() {
		testTranslationV2_0Default("x = pred", "(= x pred)");

		testTranslationV2_0Default("x = succ", "(= x succ)");
	}

	@Test
	public void testDistinct() {
		testTranslationV2_0Default("partition(A,{1},{2},{3})",
				"(= A (union (union set set0) set1))");
	}

	@Test
	public void testIntSet() {
		testTranslationV2_0Default("A = ℤ", "(= A Int)");
	}

	@Test
	public void testNatSet() {
		testTranslationV2_0Default("ℕ ⊂ ℤ", "(subset Nat Int)");
	}

	@Test
	public void testNat1Set() {
		testTranslationV2_0Default("ℕ1 ⊂ ℤ", "(subset Nat1 Int)");
	}

	@Test
	public void testTREL() {
		// Total Relation
		testTranslationV2_0Default("r  s = r  s", "(= (totp r s) (totp r s))");
	}

	@Test
	public void testSREL() {
		// Surjective Relation
		testTranslationV2_0Default("r  s = r  s", "(= (surp r s) (surp r s))");
	}

	@Test
	public void testTSREL() {
		// Total Surjective Relation
		testTranslationV2_0Default("r  s = r  s",
				"(= (totsurp r s) (totsurp r s))");
	}

	@Test
	public void testRelimage() {
		// Relimage
		testTranslationV2_0Default("AB[A] = AB[A]", "(= (img AB A) (img AB A))");

		testTranslationV2_0Default("AB[{1}] =  AB[{1}]",
				"(= (img AB enum) (img AB enum0))");
	}

	@Test
	public void testNotSubset() {
		testTranslationV2_0Default("ℕ ⊄ ℤ", "(not (subset Nat Int))");
	}

	@Test
	public void testNotSubseteq() {
		testTranslationV2_0Default("ℕ ⊈ ℤ", "(not (subseteq Nat Int))");
	}

	@Test
	public void testPartition() {
		testTranslationV2_0Default("partition(A,{1,2},{3,4})",
				"(and (= A enum) (= (inter enum0 enum1) emptyset))");
	}

	@Test
	public void testPartition1Set() {
		testTranslationV2_0Default("partition(A,{1})", "(= A set)");
	}

	@Test
	public void testFunAppl_ID() {
		testTranslationV2_0Default("id(ℤ) = id(ℤ)", "(= Int Int)");
	}

	@Test
	public void testFunAppl_PRJ1() {
		testTranslationV2_0Default("prj1(ℤ↦BOOL) = prj1(ℤ↦BOOL)",
				"(= (fst (pair Int BOOLS)) (fst (pair Int BOOLS)))");
	}

	@Test
	public void testFunAppl_PRJ2() {
		testTranslationV2_0Default("prj2(ℤ↦BOOL) = prj2(ℤ↦BOOL)",
				"(= (snd (pair Int BOOLS)) (snd (pair Int BOOLS)))");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionSetOfSet() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ", "ℤ ↔ ℙ(ℤ)");

		testTranslationV2_0VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionParsePairTypes() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ",
				"ℙ((ℤ ↔ ℤ) ↔ ℤ)");

		testTranslationV2_0VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionParsePairTypes2() {

		final ITypeEnvironment errorTe = mTypeEnvironment("AZ", "ℤ ↔ (ℤ ↔ ℤ)");

		testTranslationV2_0VerDefaultSolver(errorTe, "AZ = AZ", "(= AZ AZ)");

	}

	public void testExceptionBoolSort() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(errorTe, "BOOL = BOOL",
				"(= BOOL BOOL)");

	}

	@Test
	/**
	 * (= prj1 prj1) should be expected, but while PRJ1 can not be translated yet,
	 * false is returned instead
	 */
	public void testPRJ1() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(errorTe, "1 ↦ 2 ↦ 1 ∈ prj1",
				"false");
	}

	@Test
	/**
	 * (= prj2s prj2) should be expected, but while PRJ2 can not be translated yet,
	 * false is returned instead
	 */
	public void testPRJ2() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(errorTe, "1 ↦ 2 ↦ 1 ∈ prj2",
				"false");
	}

	@Test
	public void testTRUE() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV2_0ChooseLogic(errorTe, "TRUE ∈ BOOL",
				"(in TRUE BOOLS)", veriTLogicWithBool);
	}

	@Test
	public void testFALSE() {

		final ITypeEnvironment errorTe = mTypeEnvironment();

		testTranslationV2_0ChooseLogic(errorTe, "FALSE ∈ BOOL",
				"(in FALSE BOOLS)", veriTLogicWithBool);
	}

	@Test
	/**
	 * (= (dprod A A) (dprod A A)) should be expected, but while DPROD can not be translated yet,
	 * false is returned instead
	 */
	public void testDPROD() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "A ↔ A");

		testTranslationV2_0VerDefaultSolver(te, "B ⊗ B = B ⊗ B", "false");
	}

	@Test
	/**
	 * While PPROD can not be translated yet, false is returned instead.
	 */
	public void testPPROD() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "A ↔ A");

		testTranslationV2_0VerDefaultSolver(te, "B ∥ B = B ∥ B", "false");
	}

	@Test
	/**
	 * While FUNIMAGE can not be translated yet, false is returned instead.
	 */
	public void testFUNIMAGE() {

		final ITypeEnvironment te = mTypeEnvironment("A", "ℙ(A)", "B", "ℤ ↔ ℤ",
				"f", "ℤ");

		testTranslationV2_0VerDefaultSolver(te, "B(f) = B(f)", "false");
	}

	@Test
	/**
	 * While KUNION can not be translated yet, false is returned instead.
	 */
	public void testKUNION() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te, "union({{1}}) = union({{1}})",
				"false");
	}

	@Test
	/**
	 * While POW1 can not be translated yet, false is returned instead.
	 */
	public void testPOW1() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te, "ℙ1(ℤ) = ℙ1(ℤ)", "false");
	}

	@Test
	/**
	 * While QUNION can not be translated yet, false is returned instead.
	 */
	public void testQUNION() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te,
				"( ⋃ x ⦂ ℤ · x ∈ {0,1,2} ∧ x > 0 ∣ {x} ) = {1, 2}", "false");
	}

	@Test
	/**
	 * While QINTER can not be translated yet, false is returned instead.
	 */
	public void testQINTER() {

		final ITypeEnvironment te = mTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te,
				"( ⋂ x ⦂ ℤ · x ∈ {0,1,2} ∧ x > 0 ∣ {x} ) = {}", "false");
	}

	@Test
	/**
	 * While ExtendedPredicate can not be translated yet, false is returned instead.
	 */
	public void testExtendedPredicate() {

		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te, "bar(5)", "false");
	}

	@Test
	/**
	 * While ExtendedExpression can not be translated yet, false is returned instead.
	 */
	public void testExtendedExpression() {

		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();

		testTranslationV2_0VerDefaultSolver(te, "(foo) = (foo)", "false");
	}

	/**
	 * Benchmark tests
	 */
	@Test
	public void testComprehensionSet() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final Map<String, String> expectedNotPredefinedMacros = new HashMap<String, String>();
		expectedNotPredefinedMacros
				.put("cset",
						"cset ((elem (Pair Int Int))) (exists ((x Int)) (and (= elem (pair x (+ x x))) (forall ((y Int)) (and (in y Nat) (forall ((z Int)) (and (in z Nat) (= (+ z y) x)))))");
		testContainsNotPredefinedMacros(
				te,
				"((λx· ∀y· (y ∈ ℕ ∧ ∀z·(z ∈ ℕ ∧ (z + y = x	))) ∣ x+x) = ∅) ∧ (∀t·(t≥0∨t<0))",
				expectedNotPredefinedMacros);
	}

	@Test
	public void testEnumeration() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final Map<String, String> expectedEnumerations = new HashMap<String, String>();
		expectedEnumerations.put("enum", "enum ((elem Int)) (Int Bool)  (or\n"
				+ "		(= elem 1)\n"//
				+ "		(= elem 2)\n"//
				+ "		(= elem 3)\n"//
				+ " )");
		expectedEnumerations.put("enum0",
				"enum0 ((elem0 Int)) (Int Bool)  (= elem0 1)");
		testContainsNotPredefinedMacros(te, "card({1,2,3}) ≠ card({1})",
				expectedEnumerations);
	}

	@Test
	public void testFiniteAssumptions() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final List<String> expectedAssumptions = Arrays
				.asList("(finite finite_p enum finite_f finite_k)");
		testContainsAssumptionsVeriT(te, "finite({1,2,3})", expectedAssumptions);
	}

	@Test
	public void testPairEnumeration() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final Map<String, String> expectedEnumerations = new HashMap<String, String>();
		expectedEnumerations
				.put("enum",
						"(enum (lambda (?elem (Pair Int Int)) . (or\n\t\t(= ?elem (pair 2 1))\n\t\t(= ?elem (pair 3 2))\n)))");
		testContainsNotPredefinedMacros(te, "{2 ↦ 1,3 ↦ 2} ⊂ pred",
				expectedEnumerations);
	}

	@Test
	public void testDistinctAssumptions() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final List<String> expectedAssumptions = Arrays.asList(
				"(distinct set set0 set1)", //
				"(= set1 enum1)", //
				"(= set0 enum0)", //
				"(= set enum)");

		testContainsAssumptionsVeriT(te, "partition(A,{1},{2},{3})",
				expectedAssumptions);
	}

	@Test
	public void testCardAssumptions() {
		final ITypeEnvironment te = ExtendedFactory.eff.makeTypeEnvironment();
		final List<String> expectedAssumptions = Arrays.asList(
				"(card enum card_f card_k)", //
				"(card enum0 card_f0 card_k0)");
		testContainsAssumptionsVeriT(te, "card({1,2,3}) = card({1,2,3})",
				expectedAssumptions);
	}
}
