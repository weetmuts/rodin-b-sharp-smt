/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;
import org.eventb.smt.provers.core.SMTProversCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSMTThroughPP extends XProverReasoner {
	public static String REASONER_ID = SMTProversCore.PLUGIN_ID
			+ ".externalSMT";

	public ExternalSMTThroughPP() {
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall newProverCall(final IReasonerInput input,
			final Iterable<Predicate> hypotheses, final Predicate goal,
			final IProofMonitor pm) {
		final SMTInput smtInput = (SMTInput) input;
		return new SMTPPCall(hypotheses, goal, pm, smtInput.getSolverConfig(),
				smtInput.getPOName(), smtInput.getTranslationPath());
	}
}
