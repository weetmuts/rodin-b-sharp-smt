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
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilSuccess.COMBINATOR_ID;
import static org.eventb.smt.core.SMTCore.getTacticDescriptor;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.prefs.IConfigDescriptor;

/**
 * This class file contains static classes that extend the autoTactics extension
 * point in the sequent prover
 *
 *
 * @author YFT
 *
 */
public class AutoTactics {

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	private static ITacticDescriptor attemptAfterLasso(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(SequentProver.PLUGIN_ID
						+ ".attemptAfterLasso");
		return comb.combine(descs, id);
	}

	/**
	 *
	 * @return a tactic descriptor of all SMT solvers applied sequentially or
	 *         <code>null</code> if no SMT configuration could be used as a
	 *         tactic.
	 */
	public static ITacticDescriptor makeAllSMTSolversTactic() {
		final IConfigDescriptor[] configs = SMTCore.getConfigurations();
		final List<ITacticDescriptor> combinedTactics = new ArrayList<ITacticDescriptor>();
		for (final IConfigDescriptor config : configs) {
			if (config.isEnabled()) {
				final String configName = config.getName();
				final ITacticDescriptor smtTactic = getTacticDescriptor(configName);
				combinedTactics.add(smtTactic);
			}
		}
		if (combinedTactics.isEmpty()) {
			return null;
		}

		final ICombinatorDescriptor compUntilSuccCombDesc = getAutoTacticRegistry()
				.getCombinatorDescriptor(COMBINATOR_ID);
		final ITacticDescriptor allSMTSolvers = compUntilSuccCombDesc
				.combine(combinedTactics, "composeUntilSuccessId");
		return attemptAfterLasso(singletonList(allSMTSolvers),
				"attemptAfterLassoId");
	}
}
