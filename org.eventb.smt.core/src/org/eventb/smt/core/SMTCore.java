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
import static org.eventb.smt.core.internal.provers.SMTProversCore.externalSMT;
import static org.eventb.smt.core.translation.TranslationApproach.USING_PP;
import static org.eventb.smt.core.translation.TranslationApproach.USING_VERIT;

import org.eventb.core.seqprover.ITactic;

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
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted,
			final String configId) {
		return externalSMT(USING_PP, restricted, configId);
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
		return externalSMTThroughPP(restricted, ALL_SOLVER_CONFIGURATIONS);
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			final String configId) {
		return externalSMT(USING_VERIT, restricted, configId);
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
		return externalSMTThroughVeriT(restricted, ALL_SOLVER_CONFIGURATIONS);
	}
}
