/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.smt.ui.internal.provers.SMTFailureTactic.SMT_SOLVER_CONFIG_ERROR;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMT extends DefaultTacticProvider {
	public class SMTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			try {
				return SMTProversUI.getDefault().getAllSMTSolversTactic()
						.getTacticInstance();
			} catch (final IllegalArgumentException iae) {
				return SMT_SOLVER_CONFIG_ERROR;
			}
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			final IProofTreeNode node, final Predicate hyp,
			final String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
