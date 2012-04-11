/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.provers;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.core.seqprover.xprover.XProverReasoner2;
import org.eventb.smt.core.SMTCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSMTThroughPP extends XProverReasoner2 {
	public static String REASONER_ID = SMTCore.PLUGIN_ID + ".externalSMTPP";

	public ExternalSMTThroughPP() {
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall2 newProverCall(final IReasonerInput input,
			final ISimpleSequent sequent, final IProofMonitor pm) {
		final SMTInput smtInput = (SMTInput) input;
		return new SMTPPCall(sequent, pm, smtInput.getSolverConfig(),
				smtInput.getPOName(), smtInput.getTranslationPath());
	}

	@Override
	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		((SMTInput) rInput).serialize(writer);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new SMTInput(reader);
	}
}
