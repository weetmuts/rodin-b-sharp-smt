/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.core.seqprover.eventbExtensions.TacticCombinators.ComposeUntilSuccess.COMBINATOR_ID;
import static org.eventb.smt.ui.internal.provers.SMTProversUI.PLUGIN_ID;
import static org.eventb.smt.core.SMTCore.externalSMT;
import static org.eventb.smt.core.SMTCore.getSMTConfiguration;
import static org.eventb.smt.ui.internal.UIUtils.logError;

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
import org.eventb.smt.core.provers.ISMTConfiguration;
import org.eventb.smt.ui.internal.preferences.configurations.EnablementStore;

/**
 * This class file contains static classes that extend the autoTactics extension
 * point in the sequent prover
 *
 *
 * @author YFT
 *
 */
public class AutoTactics {

	/*
	 * Default timeout for an SMT solver run in the auto tactic.
	 */
	private static final long DEFAULT_TIMEOUT_DELAY = 1000;

	/*
	 * Default value of restricted flag for an SMT solver run in the auto
	 * tactic.
	 */
	private static final boolean DEFAULT_RESTRICTED_VALUE = true;

	/**
	 * label for the 'restricted' tactic parameter
	 */
	private static final String RESTRICTED_LABEL = "restricted";
	private static final String TIMEOUT_DELAY_LABEL = "timeOutDelay";

	/**
	 * label for the 'configName' tactic parameter
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
			final String configName = parameters.getString(CONFIG_NAME_LABEL);
			return externalSMT(configName, restricted, timeOutDelay);
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

	private static IParamTacticDescriptor smtTactic(final ISMTConfiguration config) {
		final IParameterizerDescriptor smtParam = getAutoTacticRegistry()
				.getParameterizerDescriptor(PLUGIN_ID + ".SMTParam");

		final IParameterSetting params = smtParam.makeParameterSetting();
		params.setBoolean(RESTRICTED_LABEL, DEFAULT_RESTRICTED_VALUE);
		params.setLong(TIMEOUT_DELAY_LABEL, DEFAULT_TIMEOUT_DELAY);
		params.setString(CONFIG_NAME_LABEL, config.getName());

		final String tacticID = freshTacticID(config);
		final String tacticName = config.getName();
		final String tacticDescription = smtDescription(config);

		return smtParam.instantiate(tacticID, tacticName, tacticDescription,
				params);
	}

	private static String freshTacticID(final ISMTConfiguration config) {
		final String originalID = config.getName();
		String newID = originalID;
		int i = 0;
		while (getAutoTacticRegistry().isRegistered(newID)) {
			newID = originalID + "_" + i;
			i = i + 1;
		}
		return newID;
	}

	private static String smtDescription(final ISMTConfiguration config) {
		return "Tries to discharge a given sequent with the SMT solver "
				+ config.getSolverName()
				+ ". The sequent is translated into SMT-LIB "
				+ config.getSmtlibVersion() + " by the "
				+ config.getTranslationApproach() + " translator.";
	}

	/**
	 *
	 * @return a tactic descriptor of all SMT solvers applied sequentially or
	 *         <code>null</code> if no SMT configuration could be used as a
	 *         tactic.
	 */
	public static ITacticDescriptor makeAllSMTSolversTactic() {
		final List<String> configNames = EnablementStore.getEnabledConfigNames();
		final List<ITacticDescriptor> combinedTactics = new ArrayList<ITacticDescriptor>();
		for (final String configName : configNames) {
			final ISMTConfiguration config = getSMTConfiguration(configName);
			if (config == null) {
				continue;  // Ignore missing configuration
			}
			final IParamTacticDescriptor smtTactic;
			try {
				smtTactic = smtTactic(config);
			} catch (IllegalArgumentException iae) {
				logError("An error occured while trying to "
						+ "build a tactic with the SMT configuration "
						+ config.getName() + ".", iae);
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
				.combine(combinedTactics, "composeUntilSuccessId");
		return attemptAfterLasso(singletonList(allSMTSolvers),
				"attemptAfterLassoId");
	}
}
