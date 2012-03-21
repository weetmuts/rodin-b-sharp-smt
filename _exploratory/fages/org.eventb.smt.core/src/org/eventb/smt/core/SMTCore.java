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
import static org.eventb.smt.core.internal.provers.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.core.internal.provers.SMTProversCore.DEFAULT_DELAY;
import static org.eventb.smt.core.internal.provers.SMTProversCore.NO_SOLVER_CONFIGURATION_ERROR;
import static org.eventb.smt.core.preferences.AbstractPreferences.getSMTPrefs;

import java.util.Map;

import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.provers.ExternalSMTThroughPP;
import org.eventb.smt.core.internal.provers.ExternalSMTThroughVeriT;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.eventb.smt.core.preferences.AbstractSolverConfig;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCore {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";

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
			final Map<String, AbstractSolverConfig> solverConfigs = getSMTPrefs()
					.getSolverConfigs();
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
			final Map<String, AbstractSolverConfig> solverConfigs = getSMTPrefs()
					.getSolverConfigs();
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
