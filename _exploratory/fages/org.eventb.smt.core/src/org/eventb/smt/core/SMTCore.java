/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilSuccess;
import static org.eventb.core.seqprover.tactics.BasicTactics.failTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.smt.internal.preferences.SMTPreferences.getSolverConfigurations;
import static org.eventb.smt.internal.provers.core.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.internal.provers.core.SMTProversCore.DEFAULT_DELAY;
import static org.eventb.smt.internal.provers.core.SMTProversCore.NO_SOLVER_CONFIGURATION_ERROR;

import java.util.List;

import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.internal.core.ExternalSMTThroughPP;
import org.eventb.smt.internal.provers.internal.core.ExternalSMTThroughVeriT;
import org.eventb.smt.internal.provers.internal.core.SMTInput;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCore {
	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted,
			long timeout, final String configId) {
		if (configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = reasonerTac(
							new ExternalSMTThroughPP(),
							new SMTInput(restricted, timeout, solverConfigs
									.get(i)));
				}
				return composeUntilSuccess(smtTactics);
			} else {
				return failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else if (configId.equals("")) {
			return reasonerTac(new ExternalSMTThroughPP(), new SMTInput(
					restricted, timeout));
		} else {
			return reasonerTac(new ExternalSMTThroughPP(), //
					new SMTInput(restricted, timeout, configId));
		}
	}

	/**
	 * This tactic should be called when it is not parameterised and the user
	 * just want the current solver configuration to be used
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted, long timeout) {
		return reasonerTac(new ExternalSMTThroughPP(), //
				new SMTInput(restricted, timeout));
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using ppTrans. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughPP(final boolean restricted) {
		return externalSMTThroughPP(restricted, DEFAULT_DELAY);
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			long timeout, final String configId) {
		if (configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = reasonerTac(
							new ExternalSMTThroughVeriT(),
							new SMTInput(restricted, timeout, solverConfigs
									.get(i)));
				}
				return composeUntilSuccess(smtTactics);
			} else {
				return failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else if (configId.equals("")) {
			return reasonerTac(new ExternalSMTThroughVeriT(), new SMTInput(
					restricted, timeout));
		} else {
			return reasonerTac(new ExternalSMTThroughVeriT(), //
					new SMTInput(restricted, timeout, configId));
		}
	}

	/**
	 * This tactic should be called when it is not parameterised and the user
	 * just want the current solver configuration to be used
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeout
	 *            delay before timeout in milliseconds
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			long timeout) {
		return reasonerTac(new ExternalSMTThroughVeriT(), //
				new SMTInput(restricted, timeout));
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using veriT. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughVeriT(final boolean restricted) {
		return externalSMTThroughVeriT(restricted, DEFAULT_DELAY);
	}
}
