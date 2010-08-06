/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *     Vítor Alcântara de Almeida - First integration Smt solvers 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * Implementation of a call to the SMT Prover.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSmt extends XProverReasoner {
	
	public static String REASONER_ID = SmtProversCore.PLUGIN_ID + ".externalSMT";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {		
		return new SmtCall(hypotheses, goal, pm);
	}
}
