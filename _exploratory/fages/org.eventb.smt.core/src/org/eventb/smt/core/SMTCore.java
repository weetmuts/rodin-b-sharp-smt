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

import java.util.List;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.smt.internal.preferences.SMTPreferences;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.core.SMTProversCore;
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
		if (configId.equals(SMTProversCore.ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = SMTPreferences
					.getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = BasicTactics.reasonerTac(
							new ExternalSMTThroughPP(), new SMTInput(
									restricted, timeout, solverConfigs.get(i)));
				}
				return BasicTactics.composeUntilSuccess(smtTactics);
			} else {
				return BasicTactics
						.failTac(SMTProversCore.NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else if (configId.equals("")) {
			return BasicTactics.reasonerTac(new ExternalSMTThroughPP(),
					new SMTInput(restricted, timeout));
		} else {
			return BasicTactics.reasonerTac( //
					new ExternalSMTThroughPP(), //
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
		return BasicTactics.reasonerTac( //
				new ExternalSMTThroughPP(), //
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
		return externalSMTThroughPP(restricted, SMTProversCore.DEFAULT_DELAY);
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
		if (configId.equals(SMTProversCore.ALL_SOLVER_CONFIGURATIONS)) {
			final List<SMTSolverConfiguration> solverConfigs = SMTPreferences
					.getSolverConfigurations();
			if (solverConfigs != null && !solverConfigs.isEmpty()) {
				final int nbSolverConfigs = solverConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = BasicTactics.reasonerTac(
							new ExternalSMTThroughVeriT(), new SMTInput(
									restricted, timeout, solverConfigs.get(i)));
				}
				return BasicTactics.composeUntilSuccess(smtTactics);
			} else {
				return BasicTactics
						.failTac(SMTProversCore.NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else if (configId.equals("")) {
			return BasicTactics.reasonerTac(new ExternalSMTThroughVeriT(),
					new SMTInput(restricted, timeout));
		} else {
			return BasicTactics.reasonerTac(//
					new ExternalSMTThroughVeriT(), //
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
		return BasicTactics.reasonerTac(//
				new ExternalSMTThroughVeriT(), //
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
		return externalSMTThroughVeriT(restricted, SMTProversCore.DEFAULT_DELAY);
	}
}
