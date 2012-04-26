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
import static org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilSuccess.COMBINATOR_ID;
import static org.eventb.smt.core.SMTCore.DEFAULT_RESTRICTED_VALUE;
import static org.eventb.smt.core.SMTCore.DEFAULT_TIMEOUT_DELAY;
import static org.eventb.smt.core.SMTCore.PLUGIN_ID;
import static org.eventb.smt.core.SMTCore.externalSMT;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.smt.core.preferences.ISolverConfig;

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
	private static final String RESTRICTED_LABEL = "restricted";
	private static final String TIMEOUT_DELAY_LABEL = "timeOutDelay";

	/**
	 * label for the 'configId' tactic parameter
	 */
	private static final String CONFIG_NAME_LABEL = "configName";

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics() {
		//
	}

	public static class SMTParameterizer implements ITacticParameterizer {
		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			final boolean restricted = parameters.getBoolean(RESTRICTED_LABEL);
			final long timeOutDelay = parameters.getLong(TIMEOUT_DELAY_LABEL);
			final String configId = getPreferenceManager()
					.getSolverConfigsPrefs().configNameToId(
							parameters.getString(CONFIG_NAME_LABEL));

			return externalSMT(restricted, timeOutDelay, configId);
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

	private static IParamTacticDescriptor smtTactic(
			final ISolverConfig solverConfig) {
		final String configName = solverConfig.getName();

		final IParameterizerDescriptor smtParam = getAutoTacticRegistry()
				.getParameterizerDescriptor(PLUGIN_ID + ".SMTParam");

		final IParameterSetting params = smtParam.makeParameterSetting();
		params.setBoolean(RESTRICTED_LABEL, DEFAULT_RESTRICTED_VALUE);
		params.setLong(TIMEOUT_DELAY_LABEL, DEFAULT_TIMEOUT_DELAY);
		params.setString(CONFIG_NAME_LABEL, configName);

		final String tacticID = freshTacticID(solverConfig.getID());
		final String tacticName = configName;
		final String tacticDescription = smtDescription(solverConfig);

		return smtParam.instantiate(tacticID, tacticName, tacticDescription,
				params);
	}

	private static String freshTacticID(final String originalID) {
		String newID = originalID;
		int i = 0;
		while (getAutoTacticRegistry().isRegistered(newID)) {
			newID = originalID + "_" + i;
			i = i + 1;
		}
		return newID;
	}

	private static String smtDescription(final ISolverConfig solverConfig) {
		final String solverName = getPreferenceManager().getSMTSolversPrefs()
				.get(solverConfig.getSolverId()).getName();
		return "Tries to discharge a given sequent with the SMT solver "
				+ solverName + ". The sequent is translated into SMT-LIB "
				+ solverConfig.getSmtlibVersion() + " by the "
				+ solverConfig.getTranslationApproach() + " translator.";
	}

	/**
	 * 
	 * @return a tactic descriptor of all SMT solvers applied sequentially or
	 *         <code>null</code> if no SMT configuration could be used as a
	 *         tactic.
	 */
	public static ITacticDescriptor makeAllSMTSolversTactic() {
		final List<ITacticDescriptor> combinedTactics = new ArrayList<ITacticDescriptor>();
		final List<ISolverConfig> enabledConfigs = getPreferenceManager()
				.getSolverConfigsPrefs().getEnabledConfigs();
		if (enabledConfigs != null && !enabledConfigs.isEmpty()) {
			for (final ISolverConfig enabledConfig : enabledConfigs) {
				if (enabledConfig.isBroken()) {
					continue;
				}

				final IParamTacticDescriptor smtTactic;
				try {
					smtTactic = smtTactic(enabledConfig);
				} catch (IllegalArgumentException iae) {
					smtError("An error occured while trying to "
							+ "build a tactic with the SMT configuration "
							+ enabledConfig.getName() + ".", iae);
					continue;
				}

				combinedTactics.add(smtTactic);
			}
			if (combinedTactics.isEmpty()) {
				return null;
			}

			final ICombinatorDescriptor compUntilSuccCombDesc = getAutoTacticRegistry()
					.getCombinatorDescriptor(COMBINATOR_ID);
			final ITacticDescriptor allSMTSolvers = compUntilSuccCombDesc
					.combine(combinedTactics, "AllSMTSolversTactic");
			return attemptAfterLasso(singletonList(allSMTSolvers),
					"attemptAfterLassoId");
		}
		return null;
	}
}
