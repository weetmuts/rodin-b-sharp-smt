/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSmt extends XProverReasoner {

	public static String REASONER_ID = SmtProversCore.PLUGIN_ID
			+ ".externalSMT";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		return new SmtProverCall(hypotheses, goal, pm, "rodin_sequent"); // TODO
																			// replace
																			// "rodin_sequent"
																			// with
																			// the
																			// name
																			// of
																			// the
																			// theorem
																			// being
																			// proved
																			// in
																			// Rodin
	}
}
