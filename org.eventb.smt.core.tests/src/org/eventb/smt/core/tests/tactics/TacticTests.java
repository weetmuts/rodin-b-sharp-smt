/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.tests.tactics;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.smt.core.SMTCore.newConfigDescriptor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.SMTCore;
import org.junit.AfterClass;

/**
 * Utility methods for testing tactics.
 * 
 * @author Laurent Voisin
 */
public abstract class TacticTests {

	private static final String SIMPLE_SEQUENT = ";H; ;S; |- 1+1 = 2";

	private static final IConfigDescriptor[] originalConfigs = SMTCore
			.getConfigurations();

	/*
	 * Puts back the original list of configurations after these tests.
	 */
	@AfterClass
	public static void resetAllConfigurations() {
		SMTCore.setConfigurations(originalConfigs);
	}

	/*
	 * Change the enablement of all known SMT configurations to the given value.
	 */
	public static void enableAllConfigurations(boolean enabled) {
		final int length = originalConfigs.length;
		assertTrue("No known SMT configuration !", length != 0);
		final IConfigDescriptor[] newConfigs = new IConfigDescriptor[length];
		for (int i = 0; i < length; i++) {
			newConfigs[i] = makeEnabled(originalConfigs[i], enabled);
		}
		SMTCore.setConfigurations(newConfigs);
	}

	/*
	 * Returns an enabled copy of the given descriptor.
	 */
	private static IConfigDescriptor makeEnabled(IConfigDescriptor desc,
			boolean enabled) {
		return newConfigDescriptor(desc.getName(), desc.getSolverName(),
				desc.getArgs(), desc.getTranslationApproach(),
				desc.getSmtlibVersion(), enabled);
	}

	/**
	 * Checks that the given tactic can discharge a simple sequent. The used
	 * sequent is simple enough to be discharged by any SMT solver.
	 * 
	 * @param desc
	 *            the descriptor of the tactic to run
	 */
	protected static void assertDischarges(ITacticDescriptor desc) {
		assertDischarges(desc, SIMPLE_SEQUENT);
	}

	/**
	 * Checks that the given tactic can discharge the given sequent.
	 * 
	 * @param desc
	 *            the descriptor of the tactic to run
	 * @param sequentImage
	 *            the sequent to discharge as a string
	 * 
	 */
	protected static void assertDischarges(ITacticDescriptor desc,
			String sequentImage) {
		final ITactic tactic = desc.getTacticInstance();
		final IProofTreeNode node = makeProofTreeNode(sequentImage);
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
		return makeProofTreeNode(SIMPLE_SEQUENT);
	}

	private static IProofTreeNode makeProofTreeNode(final String sequentImage) {
		final IProverSequent sequent = genFullSeq(sequentImage);
		final IProofTree proofTree = makeProofTree(sequent, null);
		final IProofTreeNode node = proofTree.getRoot();
		assertFalse(node.isClosed());
		return node;
	}

}
