/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.tests;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.junit.Assert;
import org.junit.Test;

import br.ufrn.smt.solver.translation.TranslatorV1_2;

/**
 * Ensure that translation from ppTrans produced predicates to SMT-LIB
 * predicates is correct.
 * 
 * @author Yoann Guyot
 * 
 */
public class TranslationTests extends AbstractTests {
	protected static final ITypeEnvironment defaultTe;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";
	static {
		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "b", "ℤ", "c", "ℤ", "u", "BOOL",
				"v", "BOOL");
	}

	private static void testTranslationV1_2Default(final String ppPredStr,
			final String expectedSMTNode) {
		testTranslationV1_2(defaultTe, ppPredStr, expectedSMTNode,
				defaultFailMessage);
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
		// TODO adapter et serialiser le message d'erreur sur le predicat
		// d'entrée
		Assert.assertTrue("\'" + ppPredStr + "\' n'est pas une entrée valide.",
				Translator.isInGoal(ppPred));
		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(ppPred);

		testTranslationV1_2(ppPred, expectedSMTNode, failMessage);
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
			final String expectedSMTNode, final String failMessage) {

		final String actualSMTNode = TranslatorV1_2.translate(ppPred)
				.toString();

		System.out.println(translationMessage(ppPred, actualSMTNode));
		Assert.assertEquals(failMessage, expectedSMTNode, actualSMTNode);
	}

	private static final String translationMessage(final Predicate ppPred,
			final String smtNode) {
		return "\'" + ppPred.toString() + "\' was translated in \'" + smtNode
				+ "\'";
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
		testTranslationV1_2Default("(a = b) ∧ (u = v)", "(and (= a b) (= u v))");
		/**
		 * land (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v) ∧ (r = s)",
				"(and (= a b) (= u v) (= r s))");
		/**
		 * lor
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v)", "(or (= a b) (= u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (= u v) (= r s))");
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
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R)(s x))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2Default("∀x,y·x∈s∧y∈s",
				"(forall (?x R) (?y R)(and (s x) (s y)))");
	}

	@Test
	public void testExists() {
		/**
		 * exists
		 */
		testTranslationV1_2Default("∃x·x∈s", "(exists (?x R)(s x))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV1_2Default("∃x,y·x∈s∧y∈s",
				"(exists (?x R) (?y R)(and (s x) (s y)))");
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
		testTranslationV1_2Default("a ≠ b", "(not(= a b))");
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
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (/ a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (% a b) c)");
	}

	@Test
	// (expected = IllegalArgumentException.class)
	public void testArithExprBinopUnsupported() { // TODO Add exponential binop
		/**
		 * expn
		 */
		testTranslationV1_2Default("a ^ b = c", "(= (^ a b) c)");
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
	 * "pred-in"
	 */
	@Test
	public void testPredIn() {
		testTranslationV1_2Default("a↦ℤ↦BOOL ∈ X", "");
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
		testTranslationV1_2Default("u = TRUE", "(= u TRUE)");
		testTranslationV1_2Default("TRUE = u", "(= TRUE u)");
		testTranslationV1_2Default("u = v", "(= u v)");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		testTranslationV1_2Default("p = q", "(= p q)");
	}
}