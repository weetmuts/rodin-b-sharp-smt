/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests.unit;

import static br.ufrn.smt.solver.translation.SMTSolver.VERIT;
import static org.eventb.core.ast.Formula.FORALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;

import br.ufrn.smt.solver.translation.SMTThroughPP;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.core.tests.AbstractTests;

/**
 * Ensure that translation from ppTrans produced predicates to SMT-LIB
 * predicates is correct.
 * 
 * @author Yoann Guyot
 * 
 */
public class TranslationTestsWithPP extends AbstractTests {
	protected static final ITypeEnvironment defaultTe;
	protected static final SMTLogic defaultLogic;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";
	static {
		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ",
				"AZ", "ℤ ↔ ℙ(ℤ)", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		defaultLogic = new SMTLogic(SMTLogic.UNKNOWN, Ints.getInstance(),
				Booleans.getInstance());
	}

	private void testTranslationV1_2(final ITypeEnvironment te,
			final String ppPredStr, final String expectedSMTNode,
			final boolean usesTrue) {
		testTranslationV1_2_Ver(te, ppPredStr, expectedSMTNode,
				defaultFailMessage, usesTrue);
	}

	private static void testTranslationV1_2Default(final String ppPredStr,
			final String expectedSMTNode) {
		testTranslationV1_2_Ver(defaultTe, ppPredStr, expectedSMTNode,
				defaultFailMessage, false);
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
	private static void testTranslationV1_2_Ver(
			final ITypeEnvironment iTypeEnv, final String ppPredStr,
			final String expectedSMTNode, final String failMessage,
			final boolean usesTrue) throws AssertionError {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv);
		// TODO adapter et serialiser le message d'erreur sur le predicat
		// d'entrée
		assertTrue("\'" + ppPredStr + "\' isn't a valid input.",
				Translator.isInGoal(ppPred));
		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(ppPred);

		testTranslationV1_2(defaultLogic, ppPred, expectedSMTNode, failMessage,
				VERIT.toString(), usesTrue);
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
			final String failMessage, final String solver,
			final boolean usesBool) {
		final StringBuilder actualSMTNode = new StringBuilder();
		SMTThroughPP.translate(logic, ppPred, solver, usesBool).toString(
				actualSMTNode, false);

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
				"(and (= a b) (iff u v) (= r s))");
		/**
		 * lor
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v)",
				"(or (= a b) (iff u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (iff u v) (= r s))");
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
	public void testForall() {
		/**
		 * forall
		 */
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R) (s ?x))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2Default("∀x,y·x∈s∧y∈s",
				"(forall (?x R) (?y R) (and (s ?x) (s ?y)))");
		/**
		 * forall (multiple identifiers)
		 */
		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", defaultTe);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(FORALL, bids,
				base.getPredicate(), null);
		testTranslationV1_2(defaultLogic, p,
				"(forall (?x R) (?x_0 R) (and (s ?x) (s ?x_0)))",
				"twice same decl", VERIT.toString(), false);
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
		testTranslationV1_2Default("42 = 42", "(= 42 42)");
		/**
		 * notequal
		 */
		testTranslationV1_2Default("a ≠ b", "(not (= a b))");
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
	// @Ignore("Not yet implemented")
	public void testArithExprBinopUnsupported() { // TODO Add exponential binop
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
		testTranslationV1_2Default("a ∈ A", "(A a)");
		testTranslationV1_2Default("a↦b ∈ AB", "(MS a b AB)");
		testTranslationV1_2Default("a↦BOOL↦BOOL ∈ X", "(MS a BOOL BOOL X)");
	}

	@Test
	public void testPredInInt() {
		testTranslationV1_2Default("a↦ℤ ∈ AZ", "(MS_0 a int AZ)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV1_2Default("r = s", "(= r s)");
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
		testTranslationV1_2Default("p = q", "(= p q)");
	}

	@Test
	public void testTRUELit() {
		final ITypeEnvironment te = mTypeEnvironment("f", "ℙ(BOOL)", "x",
				"BOOL");

		testTranslationV1_2(te, "x ∈ f", "(f x)", true);
	}
}
