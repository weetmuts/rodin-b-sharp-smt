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

import static org.eventb.smt.core.internal.provers.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.core.internal.provers.SMTProversCore.getDefault;

import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.provers.SMTProversCore;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCore {

	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";

	public static ITactic allSMTSolversTactic() {
		return getDefault().getAllSMTSolversTactic().getTacticInstance();
	}

	public static void updateAllSMTSolversTactic() {
		getDefault().updateAllSMTSolversTactic();
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            amount of time in milliseconds after which the solver will be
	 *            interrupted
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay, final String configId) {
		return SMTProversCore.externalSMT(restricted, timeOutDelay, configId);
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent).
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay) {
		return externalSMT(restricted, timeOutDelay, ALL_SOLVER_CONFIGURATIONS);
	}
}
