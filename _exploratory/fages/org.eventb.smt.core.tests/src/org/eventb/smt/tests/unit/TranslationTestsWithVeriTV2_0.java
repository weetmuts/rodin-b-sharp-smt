package org.eventb.smt.tests.unit;

import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.tests.unit.Messages.SMTLIB_Translation_Failed;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.ast.theories.SMTTheoryV2_0;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.SMTThroughVeriT;
import org.junit.Test;

public class TranslationTestsWithVeriTV2_0 extends AbstractTests {

	protected static final ITypeEnvironment defaultTe;
	public static final SMTLogic defaultLogic;

	static {
		defaultTe = mTypeEnvironment("S", "ℙ(S)", "r", "ℙ(R)", "s", "ℙ(R)",
				"a", "ℤ", "b", "ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");
		defaultLogic = new SMTLogic.SMTLogicVeriT(SMTLogic.UNKNOWN,
				SMTTheoryV2_0.Ints.getInstance(),
				SMTTheoryV2_0.Core.getInstance());
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
	private static void testTranslationV2_0(final Predicate ppPred,
			final String expectedSMTNode, final String failMessage,
			final String solver) {
		final StringBuilder actualSMTNode = new StringBuilder();
		SMTThroughVeriT.translate(ppPred, V2_0).toString(actualSMTNode, -1,
				false);

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
			final String failMessage) throws AssertionError {
		final Predicate pred = parse(predStr, iTypeEnv);
		testTranslationV2_0(pred, expectedSMTNode, failMessage,
				VERIT.toString());
	}

	private static void testTranslationV2_0Default(final String predStr,
			final String expectedSMTNode) {
		testTranslationV2_0(defaultTe, predStr, expectedSMTNode,
				SMTLIB_Translation_Failed);
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

}
