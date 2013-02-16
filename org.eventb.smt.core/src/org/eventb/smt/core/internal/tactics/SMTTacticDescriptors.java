/*******************************************************************************
 * Copyright (c) 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.tactics;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.CONFIG_NAME_LABEL;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.RESTRICTED_LABEL;
import static org.eventb.smt.core.internal.tactics.SMTParameterizer.TIMEOUT_DELAY_LABEL;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.smt.core.internal.provers.SMTProversCore;

/**
 * Factory class for building tactic descriptors that allow to run an SMT solver
 * configuration. All tactics are built based on the tactic parameterizer for
 * SMT solvers.
 * 
 * @author Laurent Voisin
 */
public class SMTTacticDescriptors {

	// Auto-tactic registry
	private static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	// Descriptor of the Parameterizer tactic for running SMT solvers
	private static final IParameterizerDescriptor DESC = REGISTRY
			.getParameterizerDescriptor(SMTParameterizer.ID);

	// Common prefix for the IDs of the tactic descriptors built by this class
	private static final String PREFIX = SMTProversCore.PLUGIN_ID + ".config.";

	/**
	 * Returns a tactic descriptor for running the given SMT solver
	 * configuration. This method does not verify that the configuration has
	 * been registered. The tactic is configured with the default values for the
	 * time out and restricted parameters.
	 * 
	 * @param configName
	 *            the name of an SMT configuration
	 * @return a tactic descriptor for running the given configuration
	 */
	public static ITacticDescriptor getTacticDescriptor(String configName) {
		final IParameterSetting params = DESC.makeParameterSetting();
		params.setString(CONFIG_NAME_LABEL, configName);

		final String tacticID = PREFIX + configName;
		final String tacticName = getTacticName(params);
		final String tacticDescr = getTacticDescription(params);
		return DESC.instantiate(tacticID, tacticName, tacticDescr, params);
	}

	private static String getTacticName(IParameterValuation params) {
		final String configName = params.getString(CONFIG_NAME_LABEL);
		return "SMT Solver " + configName + " (Discharge)";
	}

	private static String getTacticDescription(IParameterValuation params) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Run the SMT configuration ");
		sb.append(params.getString(CONFIG_NAME_LABEL));
		sb.append(" with ");
		if (params.getBoolean(RESTRICTED_LABEL)) {
			sb.append("selected");
		} else {
			sb.append("all");
		}
		sb.append(" hypotheses and a time out of ");
		sb.append(params.getLong(TIMEOUT_DELAY_LABEL));
		sb.append(" ms.");
		return sb.toString();
	}

}
