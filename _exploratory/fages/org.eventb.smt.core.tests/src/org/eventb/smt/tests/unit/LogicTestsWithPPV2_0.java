/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.unit;

import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.SMTThroughPP;
import org.junit.Test;

public class LogicTestsWithPPV2_0 extends AbstractTests {
	private static final ITypeEnvironment defaultTe;
	// uncomment when the gatherer can detect Ints occurrences
	// private static final SMTLogic smtLibUnderlyingLogic;
	private static final SMTLogic aufliaLogic;
	static {
		defaultTe = mTypeEnvironment("a", "ℤ", "p", "BOOL", "P", "ℙ(BOOL)");
		// uncomment when the gathere can detect Ints occurrences
		// smtLibUnderlyingLogic =
		// SMTLogic.SMTLIBUnderlyingLogicV2_0.getInstance();
		aufliaLogic = SMTLogic.AUFLIAv2_0.getInstance();
	}

	private static void testLogic(final ITypeEnvironment iTypeEnv,
			final String ppPredStr, final SMTLogic expectedSMTLogic) {
		final Predicate goalPredicate = parse(ppPredStr, iTypeEnv);
		assertTrue("\'" + ppPredStr + "\' isn't a valid input.",
				Translator.isInGoal(goalPredicate));

		final SMTLogic logic = SMTThroughPP.determineLogic(goalPredicate, V2_0);

		assertEquals("", expectedSMTLogic.toString(), logic.toString());
	}

	@Test
	public void testInt() {
		testLogic(defaultTe, "a = 1", aufliaLogic);
	}

	@Test
	public void testBool() {
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitTRUE(AtomicExpression)
		 */
		testLogic(defaultTe, "TRUE = p", aufliaLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOOL(AtomicExpression)
		 */
		testLogic(defaultTe, "a↦BOOL↦BOOL ∈ X", aufliaLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOUND_IDENT_DECL(BoundIdentDecl)
		 */
		testLogic(defaultTe,
				"∀ x ⦂ ℤ, X ⦂ ℙ(ℤ), P ⦂ BOOL · (x ∈ X ⇒ P = TRUE)", aufliaLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * enterIN(RelationalPredicate)
		 */
		testLogic(defaultTe, "p ∈ P", aufliaLogic);
	}
}
