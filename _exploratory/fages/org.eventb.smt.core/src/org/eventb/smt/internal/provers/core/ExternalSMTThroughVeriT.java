/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.provers.core;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.core.seqprover.xprover.XProverReasoner2;
import org.eventb.smt.core.SMTCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author YGU
 */
public class ExternalSMTThroughVeriT extends XProverReasoner2 {
	public static String REASONER_ID = SMTCore.PLUGIN_ID + ".externalSMTVeriT";

	public ExternalSMTThroughVeriT() {
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall2 newProverCall(final IReasonerInput input,
			final ISimpleSequent sequent, final IProofMonitor pm) {
		final SMTInput smtInput = (SMTInput) input;
		return new SMTVeriTCall(sequent, pm, smtInput.getSolverConfig(),
				smtInput.getPOName(), smtInput.getTranslationPath(),
				smtInput.getVeritPath());
	}
}
