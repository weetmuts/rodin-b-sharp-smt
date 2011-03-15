/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.tests;

import static org.eventb.core.ast.Formula.FORALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.pptrans.Translator;

import org.junit.Test;

import br.ufrn.smt.solver.translation.SMTThroughVeriT;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSortSymbol;

/**
 * Ensure that translation to veriT extended version of SMT-LIB is correct
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class TranslationTestsWithVeriT extends AbstractTests {
	protected static final ITypeEnvironment defaultTe, simpleTe;
	protected static final SMTLogic defaultLogic;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";
	private SMTSignature signature;

	static {
		simpleTe = mTypeEnvironment("e", "ℙ(S)", "f", "ℙ(S)", "g", "S");

		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ",
				"AZ", "ℤ ↔ ℙ(ℤ)", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		defaultLogic = SMTLogic.SMTLIBUnderlyingLogic.getInstance();
	}

	private static void testTranslationV1_2Default(final String predStr,
			final String expectedSMTNode) {
		testTranslationV1_2(defaultTe, predStr, expectedSMTNode,
				defaultFailMessage);
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
	private static void testTranslationV1_2(final ITypeEnvironment iTypeEnv,
			final String predStr, final String expectedSMTNode,
			final String failMessage) throws AssertionError {
		final Predicate pred = parse(predStr, iTypeEnv);
		// TODO adapter et serialiser le message d'erreur sur le predicat
		// d'entrée
		assertTrue("\'" + predStr + "\' isn't a valid input.",
				Translator.isInGoal(pred));
		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(pred);

		testTranslationV1_2(pred, expectedSMTNode, failMessage);
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
	private static void testTranslationV1_2(final Predicate ppred,
			final String expectedSMTNode, final String failMessage) {
		final String actualSMTNode = SMTThroughVeriT.translate(defaultLogic,
				ppred).toString();

		System.out.println(translationMessage(ppred, actualSMTNode));
		assertEquals(failMessage, expectedSMTNode, actualSMTNode);
	}

	public void setSignatureForTests(ITypeEnvironment typeEnvironment) {
		this.signature = SMTThroughVeriT.translateSMTSignature(typeEnvironment);
	}

	private StringBuilder typeEnvironmentFunctionsFail(
			Set<String> expectedFunctions, Set<SMTFunctionSymbol> functions) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated functions wasn't the expected ones. The expected functions are:");
		for (String expectedFunction : expectedFunctions) {
			sb.append("\n");
			sb.append(expectedFunction);
		}
		sb.append("\nBut the translated functions were:");
		for (SMTFunctionSymbol fSymbol : functions) {
			sb.append("\n");
			sb.append(fSymbol.toString());
		}
		return sb;
	}

	private StringBuilder typeEnvironmentPredicatesFail(
			Set<String> expectedPredicates, Set<SMTPredicateSymbol> predicates) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated functions wasn't the expected ones. The expected functions are:");
		for (String expectedPredicate : expectedPredicates) {
			sb.append("\n");
			sb.append(expectedPredicate);
		}
		sb.append("\nBut the translated functions were:");
		for (SMTPredicateSymbol predicateSymbol : predicates) {
			sb.append("\n");
			sb.append(predicateSymbol.toString());
		}
		return sb;
	}

	private StringBuilder typeEnvironmentSortsFail(Set<String> expectedSorts,
			Set<SMTSortSymbol> sorts) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated functions wasn't the expected ones. The expected functions are:");
		for (String expectedSort : expectedSorts) {
			sb.append("\n");
			sb.append(expectedSort);
		}
		sb.append("\nBut the translated functions were:");
		for (SMTSortSymbol sortSymbol : sorts) {
			sb.append("\n");
			sb.append(sortSymbol.toString());
		}
		return sb;
	}

	public void testTypeEnvironmentFunctions(Set<String> expectedFunctions) {
		expectedFunctions.add("(~ Int Int)");
		expectedFunctions.add("(- Int Int Int)");
		expectedFunctions.add("(* Int Int)");
		expectedFunctions.add("(+ Int Int)");
		Set<SMTFunctionSymbol> functionSymbols = signature.getFuns();

		Iterator<SMTFunctionSymbol> iterator = functionSymbols.iterator();

		StringBuilder sb = typeEnvironmentFunctionsFail(expectedFunctions,
				functionSymbols);
		assertEquals(sb.toString(), expectedFunctions.size(),
				functionSymbols.size());

		while (iterator.hasNext()) {
			assertTrue(sb.toString(),
					expectedFunctions.contains(iterator.next().toString()));
		}

		// Iterator<String> expecIterator = expectedFunctions.iterator();
		// Iterator<SMTFunctionSymbol> functionIterator = functionSymbols
		// .iterator();
		//
		// while (expecIterator.hasNext() && functionIterator.hasNext()) {
		// assertEquals(sb.toString(), expecIterator.next(), functionIterator
		// .next().toString());
		// }

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

	@Test
	public void testTypeEnvironmentFunction() {
		setSignatureForTests(simpleTe);
		Set<String> expectedFunctions = new HashSet<String>();

		expectedFunctions.add("(g S)");

		testTypeEnvironmentFunctions(expectedFunctions);
	}

	/**
	 * "pred-ass"
	 */
	@Test
	public void testPredAssop() {

		testTranslationV1_2Default("(u = v)", "(= u v)");

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
	 * "pred-boolequ"
	 */
	@Test
	public void testPredBoolEqu() {
		testTranslationV1_2Default("u = v", "(= u v)");
		testTranslationV1_2Default("u = TRUE", "(= u TRUE)");
		testTranslationV1_2Default("TRUE = u", "(= TRUE u)");
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
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R) (in ?x s))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2Default("∀x,y·x∈s∧y∈s",
				"(forall (?x R) (?y R) (and (in ?x s) (in ?y s)))");
		/**
		 * forall (multiple identifiers)
		 */
		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", defaultTe);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(FORALL, bids,
				base.getPredicate(), null);
		// System.out.println("Predicate " + p);
		testTranslationV1_2(p,
				"(forall (?x R) (?x_0 R) (and (in ?x s) (in ?x_0 s)))",
				"twice same decl");
	}

	@Test
	public void testExists() {
		/**
		 * exists
		 */
		testTranslationV1_2Default("∃x·x∈s", "(exists (?x R) (in ?x s))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV1_2Default("∃x,y·x∈s∧y∈s",
				"(exists (?x R) (?y R) (and (in ?x s) (in ?y s)))");
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
	// (expected = IllegalArgumentException.class)
	public void testArithExprBinopUnsupported() { // TODO Add exponential binop
		/**
		 * expn
		 */
		testTranslationV1_2Default("a ^ b = c", "(= (^ a b) c)");
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (/ a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (% a b) c)");
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
		testTranslationV1_2Default("a ∈ A", "(MS a A)");
		testTranslationV1_2Default("a↦b ∈ AB", "(in (pair a b) AB)");
		testTranslationV1_2Default("a↦ℤ ∈ AZ", "(in (pair a Int) AZ)");
		testTranslationV1_2Default("a↦ℤ↦BOOL ∈ X",
				"(in (pair (pair a Int) Bool) X)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV1_2Default("r = s", "(= r s)");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		testTranslationV1_2Default("p = q", "(= p q)");
	}
}