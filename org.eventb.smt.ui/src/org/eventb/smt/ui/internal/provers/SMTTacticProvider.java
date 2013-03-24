/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.SMTCore;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Tactic provider for the SMT button in the Proof Control view.
 * 
 * @author Yoann Guyot
 */
public class SMTTacticProvider extends DefaultTacticProvider {

	public static class SMTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			return SMTCore.smtAutoTactic.getTacticInstance();
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
