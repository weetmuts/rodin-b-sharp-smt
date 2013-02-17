/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.tests.tactics;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;

/**
 * Utility methods for testing tactics.
 * 
 * @author Laurent Voisin
 */
public abstract class TacticTests {

	/**
	 * Checks that the given tactic can discharge a simple sequent. The used
	 * sequent is simple enough to be discharged by any SMT solver.
	 * 
	 * @param desc
	 *            the descriptor of the tactic to run
	 */
	protected static void assertDischarges(ITacticDescriptor desc) {
		final ITactic tactic = desc.getTacticInstance();
		final IProofTreeNode node = makeSimpleProofTreeNode();
		final Object result = tactic.apply(node, null);
		assertNull("The tactic failed", result);
		assertTrue(node.isClosed());
	}

	/**
	 * Checks that the given tactic can fail on a simple sequent. The used
	 * sequent is simple enough to be discharged by any SMT solver.
	 * 
	 * @param desc
	 *            the descriptor of the tactic to run
	 */
	protected static void assertFails(ITacticDescriptor desc) {
		final ITactic tactic = desc.getTacticInstance();
		final IProofTreeNode node = makeSimpleProofTreeNode();
		final Object result = tactic.apply(node, null);
		assertNotNull("The tactic succeeded", result);
		assertFalse(node.isClosed());
	}

	/*
	 * Returns an open proof tree node with a very simple sequent that should be
	 * discharged by any SMT solver.
	 */
	private static IProofTreeNode makeSimpleProofTreeNode() {
		final IProverSequent sequent = genSeq("|- 1+1 = 2");
		final IProofTree proofTree = makeProofTree(sequent, null);
		final IProofTreeNode node = proofTree.getRoot();
		assertFalse(node.isClosed());
		return node;
	}

}
