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

import static org.eventb.smt.internal.provers.core.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.internal.provers.core.SMTProversCore.DEFAULT_DELAY;

import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.smt.core.SMTCore;

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
	 * label for the 'restricted' tactic parameter
	 */
	private static final String RESTRICTED = "restricted";

	/**
	 * label for the 'timeout' tactic parameter
	 */
	private static final String TIMEOUT = "timeout";

	/**
	 * label for the 'configId' tactic parameter
	 */
	private static final String CONFIG_ID = "configId";

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	public static class SMTPP extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTCore.externalSMTThroughPP(true, DEFAULT_DELAY,
					ALL_SOLVER_CONFIGURATIONS);
		}
	}

	public static class SMTPPParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			final long timeout = parameters.getLong(TIMEOUT);
			final boolean restricted = parameters.getBoolean(RESTRICTED);
			final String configId = parameters.getString(CONFIG_ID);

			return SMTCore.externalSMTThroughPP(restricted, timeout, configId);
		}

	}

	public static class SMTVeriT extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return SMTCore.externalSMTThroughVeriT(true, DEFAULT_DELAY,
					ALL_SOLVER_CONFIGURATIONS);
		}
	}

	public static class SMTVeriTParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			final long timeout = parameters.getLong(TIMEOUT);
			final boolean restricted = parameters.getBoolean(RESTRICTED);
			final String configId = parameters.getString(CONFIG_ID);

			return SMTCore.externalSMTThroughVeriT(restricted, timeout,
					configId);
		}
	}
}
