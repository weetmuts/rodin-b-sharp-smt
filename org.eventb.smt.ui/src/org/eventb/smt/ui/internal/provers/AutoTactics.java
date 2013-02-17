/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.smt.core.SMTCore;

/**
 * This class file contains static classes that extend the autoTactics extension
 * point in the sequent prover
 * 
 * @author YFT
 */
public class AutoTactics {

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	/**
	 * Returns a tactic descriptor that runs all enabled SMT configurations
	 * after a lasso. The lasso is undone if no solver succeeds.
	 * 
	 * @return a tactic descriptor of all enabled SMT configurations applied
	 *         sequentially
	 */
	public static ITacticDescriptor makeAllSMTSolversTactic() {
		return attemptAfterLasso(singletonList(SMTCore.smtAutoTactic),
				"attemptAfterLassoId");
	}

	private static ITacticDescriptor attemptAfterLasso(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(SequentProver.PLUGIN_ID
						+ ".attemptAfterLasso");
		return comb.combine(descs, id);
	}

}
