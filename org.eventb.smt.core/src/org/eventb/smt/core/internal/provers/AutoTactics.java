/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.provers;

import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.smt.core.SMTCore.externalSMTThroughPP;
import static org.eventb.smt.core.SMTCore.externalSMTThroughVeriT;
import static org.eventb.smt.core.internal.provers.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.SequentProver;
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
	 * label for the 'configId' tactic parameter
	 */
	private static final String CONFIG_NAME = "configName";

	private static final ITacticDescriptor smtPpTacticDescriptor = getAutoTacticRegistry()
			.getTacticDescriptor(SMTCore.PLUGIN_ID + ".SMTPP");

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	public static class SMTPP implements ITactic {
		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return externalSMTThroughPP(true, ALL_SOLVER_CONFIGURATIONS).apply(
					ptNode, pm);
		}
	}

	public static class SMTPPParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			final boolean restricted = parameters.getBoolean(RESTRICTED);
			final String configId = getPreferenceManager()
					.getSolverConfigsPrefs().configNameToId(
							parameters.getString(CONFIG_NAME));

			return externalSMTThroughPP(restricted, configId);
		}
	}

	public static class SMTVeriT implements ITactic {
		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return externalSMTThroughVeriT(true, ALL_SOLVER_CONFIGURATIONS)
					.apply(ptNode, pm);
		}
	}

	public static class SMTVeriTParameterizer implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			final boolean restricted = parameters.getBoolean(RESTRICTED);
			final String configId = getPreferenceManager()
					.getSolverConfigsPrefs().configNameToId(
							parameters.getString(CONFIG_NAME));

			return externalSMTThroughVeriT(restricted, configId);
		}
	}

	private static ITacticDescriptor attemptAfterLasso(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(SequentProver.PLUGIN_ID
						+ ".attemptAfterLasso");
		return comb.combine(descs, id);
	}

	public static ITacticDescriptor makeSMTPPTactic() {
		return attemptAfterLasso(singletonList(smtPpTacticDescriptor),
				"attemptAfterLassoId");
	}
}
