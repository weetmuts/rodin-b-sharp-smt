/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.tests;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

import br.ufrn.smt.solver.translation.TypeEnvironment;
import br.ufrn.smt.solver.translation.VisitorV1_2;

/**
 * Ensure that translation from Event-B to SMT-LIB is correct.
 * 
 * @author Yoann Guyot
 * 
 */
public class TranslationTests extends AbstractTests {
	protected static final ITypeEnvironment defaultTe;
	protected static final String defaultFailMessage = " ≠ ";
	static {
		defaultTe = mTypeEnvironment("s", "ℙ(S)", "t", "ℙ(T)", "a", "ℤ", "b",
				"ℤ");
	}

	private static void testVisitorV1_2Default(final String ppPredStr,
			final String expectedSMTNode) {
		testVisitorV1_2(defaultTe, ppPredStr, expectedSMTNode,
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
	private static void testVisitorV1_2(final ITypeEnvironment iTypeEnv,
			final String ppPredStr, final String expectedSMTNode,
			final String failMessage) {
		final Predicate ppPred = parse(ppPredStr, iTypeEnv);
		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(ppPred);
		final TypeEnvironment typeEnv = new TypeEnvironment(hypothesis, ppPred);

		testVisitorV1_2(typeEnv, ppPred, expectedSMTNode, failMessage);
	}

	/**
	 * Tests the SMT-LIB translation with the given Predicate Calculus formula
	 * 
	 * @param typeEnv
	 *            Type environment, hypotheses and goal
	 * @param ppPred
	 *            Input Predicate Calculus formula
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testVisitorV1_2(final TypeEnvironment typeEnv,
			final Predicate ppPred, final String expectedSMTNode,
			final String failMessage) {

		final String actualSMTNode = VisitorV1_2.translateToSMTNode(typeEnv,
				ppPred).toString();

		System.out.println(translationMessage(ppPred, actualSMTNode));
		if (!actualSMTNode.equals(expectedSMTNode)) {
			throw new IllegalArgumentException("\n" + actualSMTNode + "\n"
					+ failMessage + expectedSMTNode);
		}
	}

	private static final String translationMessage(final Predicate ppPred,
			final String smtNode) {
		return "\'" + ppPred.toString() + "\' was translated in \'" + smtNode
				+ "\'";
	}

	/**
	 * Arithmetic symbols tests
	 */
	@Test
	public void testArithEqual() {
		testVisitorV1_2Default("s = s", "(= s s)");
	}

	@Test
	public void testArithSuperior() {
		testVisitorV1_2Default("a > b", "(> a b)");
	}

	@Test
	public void testArithAnd() {
		testVisitorV1_2Default("a & b", "(& a b)");
	}

	@Test
	public void testArith() {
		testVisitorV1_2Default("a # b", "(# a b)");
	}

	/**
	 * Quantifier symbols tests
	 */
	@Test(expected = AssertionError.class)
	public void testForallErr() {
		testVisitorV1_2Default("∀x·x", "");
	}

	@Test
	public void testForallIn() {
		testVisitorV1_2Default("∀x·x∈s", "(forall (?x S)(in x s))");
	}

	@Test
	public void testExistsIn() {
		testVisitorV1_2Default("∃x.x∈s", "(exists (?x S)(in x s))");
	}

	/**
	 * Connective tests
	 */
	@Test
	public void testImplies() {
		testVisitorV1_2Default("∀s,t.s⇒t", "(forall (?s S)(?t T)(implies s t)");
	}

	/**
	 * BoundIdentDeclaration
	 */
	@Test
	public void testBoundIdentDeclaration() {
		testVisitorV1_2Default(
				"∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ",
				"(forall (?x Nat)(?y Nat)(implies (and (in x Nat) (in y Nat))(in (+ x y) Nat)))");
	}
}
