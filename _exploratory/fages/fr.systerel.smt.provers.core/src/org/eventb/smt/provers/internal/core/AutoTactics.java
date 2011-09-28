/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.smt.provers.core.SMTProversCore;

/**
 * This class file contains static classes that extend the autoTactics extension
 * point in the sequent prover
 * 
 * 
 * @author YFT
 * 
 */
public class AutoTactics {

	// label for the 'restricted' tactic parameter
	private static final String RESTRICTED = "restricted";

	// label for the 'timeout' tactic parameter
	private static final String TIMEOUT = "timeout";

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	// TODO add SMTAltErgoPP, SMTCVC3PP, SMTVeriTPP and SMTZ3PP etc.. with
	// appropriate preferences
	public static class SMTPP extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTProversCore.externalSMTThroughPP(true);
		}
	}

	public static class SMTPPParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			// FIXME take timeout into account
			final long timeout = parameters.getLong(TIMEOUT);
			final boolean restricted = parameters.getBoolean(RESTRICTED);

			return SMTProversCore.externalSMTThroughPP(restricted);
		}

	}

	public static class SMTVeriT extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTProversCore.externalSMTThroughVeriT(true);
		}
	}

	public static class SMTVeriTParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			// FIXME take timeout into account
			final long timeout = parameters.getLong(TIMEOUT);
			final boolean restricted = parameters.getBoolean(RESTRICTED);

			return SMTProversCore.externalSMTThroughVeriT(restricted);
		}

	}
}
