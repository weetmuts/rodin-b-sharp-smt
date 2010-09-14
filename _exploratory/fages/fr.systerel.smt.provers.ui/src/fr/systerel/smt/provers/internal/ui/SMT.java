/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import fr.systerel.smt.provers.core.SmtProversCore;

public class SMT implements ITacticProvider {

	public static class SMTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return SmtProversCore.ExternalSmtTac(true);
		}
		
	} 
	
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
