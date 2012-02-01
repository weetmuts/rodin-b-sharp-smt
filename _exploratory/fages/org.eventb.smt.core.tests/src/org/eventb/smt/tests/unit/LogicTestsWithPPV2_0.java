/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.unit;

import static org.eventb.core.seqprover.transformer.SimpleSequents.make;
import static org.eventb.smt.translation.SMTLIBVersion.V2_0;
import static org.eventb.smt.translation.SMTThroughPP.determineLogic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.pptrans.Translator;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.tests.AbstractTests;
import org.junit.Test;

public class LogicTestsWithPPV2_0 extends AbstractTests {
	private static final ITypeEnvironment defaultTe;
	private static final SMTLogic qfufLogic;
	private static final SMTLogic aufliaLogic;
	private static final SMTLogic qfAufliaLogic;
	static {
		defaultTe = mTypeEnvironment("a", "ℤ", "p", "BOOL", "P", "ℙ(BOOL)");
		qfufLogic = SMTLogic.QF_UFv2_0.getInstance();
		aufliaLogic = SMTLogic.AUFLIAv2_0.getInstance();
		qfAufliaLogic = SMTLogic.QF_AUFLIAv2_0.getInstance();
	}

	private static void testLogic(final ITypeEnvironment iTypeEnv,
			final String ppPredStr, final SMTLogic expectedSMTLogic) {
		final Predicate goalPredicate = parse(ppPredStr, iTypeEnv);
		final ISimpleSequent sequent = make((List<Predicate>) null,
				goalPredicate, ff);
		assertTrue("\'" + ppPredStr + "\' isn't a valid input.",
				Translator.isInGoal(sequent));

		final SMTLogic logic = determineLogic(sequent, V2_0);

		assertEquals("", expectedSMTLogic.toString(), logic.toString());
	}

	@Test
	public void testInt() {
		testLogic(defaultTe, "a = 1", qfufLogic);
		testLogic(defaultTe, "a = 4 mod 2", qfufLogic);
		testLogic(defaultTe, "a = 2 + 3", qfAufliaLogic);
	}

	@Test
	public void testBool() {
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitTRUE(AtomicExpression)
		 */
		testLogic(defaultTe, "TRUE = p", qfufLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOOL(AtomicExpression)
		 */
		testLogic(defaultTe, "a↦BOOL↦BOOL ∈ X", qfufLogic);
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
		testLogic(defaultTe, "p ∈ P", qfufLogic);
	}
}
