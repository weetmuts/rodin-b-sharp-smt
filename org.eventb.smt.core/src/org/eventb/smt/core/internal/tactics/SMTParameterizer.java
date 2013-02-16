/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.tactics;

import static org.eventb.core.seqprover.tactics.BasicTactics.failTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.smt.core.SMTCore.getSMTConfiguration;
import static org.eventb.smt.core.internal.provers.Messages.unknownSMTConfigurationError;
import static org.eventb.smt.core.internal.provers.SMTProversCore.PLUGIN_ID;

import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.smt.core.internal.provers.ExternalSMT;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.eventb.smt.core.provers.ISMTConfiguration;

/**
 * Creates parameterized tactics for running an SMT solver configuration. This
 * tactic takes three parameters:
 * <ul>
 * <li>the name of an SMT configuration to run.</li>
 * <li>a restricted flag: when <code>true</code>, only selected hypotheses are
 * passed to the SMT solver; otherwise, all visible hypotheses.</li>
 * <li>a timeout delay in milliseconds: the SMT solver will be forcibly
 * terminated if it does not answer in the required delay.</li>
 * </ul>
 * <p>
 * The declared constants correspond to the extension of point
 * <code>org.eventb.core.seqprover.tacticParameterizers</code> in
 * <code>plugin.xml</code>. Their value must be kept in sync with the extension.
 * </p>
 * 
 * @author Yoann Guyot
 */
public class SMTParameterizer implements ITacticParameterizer {

	/**
	 * The id of this parameterizer in the auto-tactic registry. Should be used
	 * to retrieve the instance of this class.
	 */
	public static final String ID = PLUGIN_ID + ".SMTParam";

	/**
	 * Label for the 'configName' tactic parameter.
	 */
	public static final String CONFIG_NAME_LABEL = "configName";

	/**
	 * Label for the 'restricted' tactic parameter.
	 */
	public static final String RESTRICTED_LABEL = "restricted";

	/**
	 * Label for the 'timeOutDelay' tactic parameter.
	 */
	public static final String TIMEOUT_DELAY_LABEL = "timeOutDelay";

	@Override
	public ITactic getTactic(IParameterValuation parameters) {
		final String configName = parameters.getString(CONFIG_NAME_LABEL);
		final boolean restricted = parameters.getBoolean(RESTRICTED_LABEL);
		final long timeOutDelay = parameters.getLong(TIMEOUT_DELAY_LABEL);
		return externalSMT(configName, restricted, timeOutDelay);
	}

	/**
	 * Returns a tactic that will apply the given SMT solver configuration with
	 * the given parameters. If there is no configuration with the given name,
	 * returns a tactic that always fails.
	 * 
	 * This tactic should be called by the parameterized auto tactic.
	 * 
	 * @param configId
	 *            the selected solver configuration id
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            amount of time in milliseconds after which the solver will be
	 *            interrupted
	 * 
	 * @return an SMT tactic that will run the given configuration
	 */
	// FIXME the configuration should be checked later in the reasoner.
	private ITactic externalSMT(final String configId,
			final boolean restricted, final long timeOutDelay) {
		final ISMTConfiguration config = getSMTConfiguration(configId);
		if (config == null) {
			return failTac(unknownSMTConfigurationError + ": " + configId);
		}
		final IReasoner smtReasoner = new ExternalSMT();
		final IReasonerInput smtInput = new SMTInput(restricted, timeOutDelay,
				config);
		return reasonerTac(smtReasoner, smtInput);
	}

}
