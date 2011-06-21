/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *     Vitor Alcantara de Almeida - First integration Smt solvers 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import fr.systerel.smt.provers.core.SMTProversCore;

/**
 * Runs an external SMTPP prover as a reasoner.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSMTThroughPP extends XProverReasoner {
	private static final String RODIN_SEQUENT = "rodin_sequent";
	private final SMTPreferences preferences;

	public static String REASONER_ID = SMTProversCore.PLUGIN_ID
			+ ".externalSMT";

	public ExternalSMTThroughPP(final SMTPreferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall newProverCall(final IReasonerInput input,
			final Iterable<Predicate> hypotheses, final Predicate goal,
			final IProofMonitor pm) {

		return new SMTPPCall(hypotheses, goal, pm, preferences, RODIN_SEQUENT); // TODO
		// replace
		// "rodin_sequent"
		// with
		// the
		// name
		// of
		// the
		// theorem
		// being
		// proved
		// in
		// Rodin
	}
}