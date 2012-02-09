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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.smt.internal.provers.core.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.internal.provers.core.SMTProversCore.DEFAULT_DELAY;
import static org.eventb.smt.internal.provers.core.SMTProversCore.PLUGIN_ID;

import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators;
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

	private static final String LASSO = "org.eventb.core.seqprover.lasso";

	private static final ITacticDescriptor smtPpTacticDescriptor = getAutoTacticRegistry()
			.getTacticDescriptor(PLUGIN_ID + ".SMTPP");

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

	private static ITacticDescriptor lasso() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(LASSO);
	}

	private static ITacticDescriptor attempt(List<ITacticDescriptor> descs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.Attempt.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	private static ITacticDescriptor sequence(List<ITacticDescriptor> descs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.Sequence.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	private static ITacticDescriptor onAllPending(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.OnAllPending.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	public static ITacticDescriptor makeSMTPPTactic() {
		return attempt(
				singletonList(sequence(
						asList(lasso(),
								onAllPending(
										singletonList(smtPpTacticDescriptor),
										"onAllPendingId")), "sequenceId")),
				"attemptId");
	}
}
