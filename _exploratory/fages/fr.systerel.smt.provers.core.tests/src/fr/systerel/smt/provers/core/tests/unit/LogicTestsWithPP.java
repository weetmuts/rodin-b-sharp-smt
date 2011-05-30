package fr.systerel.smt.provers.core.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;

import br.ufrn.smt.solver.translation.SMTThroughPP;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTTheory.Booleans;
import fr.systerel.smt.provers.ast.SMTTheory.Ints;
import fr.systerel.smt.provers.core.tests.AbstractTests;

public class LogicTestsWithPP extends AbstractTests {
	private static final ITypeEnvironment defaultTe;
	private static final SMTLogic defaultBoolLogic;
	static {
		defaultTe = mTypeEnvironment("a", "ℤ", "p", "BOOL", "P", "ℙ(BOOL)");
		defaultBoolLogic = new SMTLogic(SMTLogic.UNKNOWN, Ints.getInstance(),
				Booleans.getInstance());
	}

	private static void testLogic(final ITypeEnvironment iTypeEnv,
			final String ppPredStr) {
		final Predicate goalPredicate = parse(ppPredStr, iTypeEnv);
		assertTrue("\'" + ppPredStr + "\' isn't a valid input.",
				Translator.isInGoal(goalPredicate));

		final SMTLogic logic = SMTThroughPP.determineLogic(goalPredicate);

		assertEquals("", defaultBoolLogic.toString(), logic.toString());
	}

	@Test
	public void testBool() {
		/**
		 * Reaches
		 * br.ufrn.smt.solver.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitTRUE(AtomicExpression)
		 */
		testLogic(defaultTe, "TRUE = p");
		/**
		 * Reaches
		 * br.ufrn.smt.solver.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOOL(AtomicExpression)
		 */
		testLogic(defaultTe, "a↦BOOL↦BOOL ∈ X");
		/**
		 * Reaches
		 * br.ufrn.smt.solver.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOUND_IDENT_DECL(BoundIdentDecl)
		 */
		testLogic(defaultTe, "∀ x ⦂ ℤ, X ⦂ ℙ(ℤ), P ⦂ BOOL · (x ∈ X ⇒ P = TRUE)");
		/**
		 * Reaches
		 * br.ufrn.smt.solver.translation.SMTThroughPP.BoolTheoryVisitor.
		 * enterIN(RelationalPredicate)
		 */
		testLogic(defaultTe, "p ∈ P");
	}
}
