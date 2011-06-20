/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YGU) - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author YGU
 */
public class ExternalSMTThroughVeriT extends XProverReasoner {
	private static final String PREPROPATH = "prepropath";
	private static final boolean USINGPREPRO = true;
	private static final String SOLVERINDEX = "solverindex";
	private static final String SOLVERPREFERENCES = "solverpreferences";
	private static final String RODIN_SEQUENT = "rodin_sequent";
	private static String PREFS_ID = "fr.systerel.smt.provers.ui";

	public static String REASONER_ID = SmtProversCore.PLUGIN_ID
			+ ".externalSMT";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public XProverCall newProverCall(final IReasonerInput input,
			final Iterable<Predicate> hypotheses, final Predicate goal,
			final IProofMonitor pm) {

		final IPreferencesService preferencesService = Platform
				.getPreferencesService();

		/**
		 * Get back preferences from UI TODO remove UI dependance here
		 */
		final String solverPreferences = preferencesService.getString(PREFS_ID,
				SOLVERPREFERENCES, null, null);
		final int solverIndex = preferencesService.getInt(PREFS_ID,
				SOLVERINDEX, -1, null);
		final String preProPath = preferencesService.getString(PREFS_ID,
				PREPROPATH, null, null);
		final SMTPreferences smtPreferences = new SMTPreferences(
				solverPreferences, solverIndex, USINGPREPRO, preProPath);

		return new SMTVeriTCall(hypotheses, goal, pm, smtPreferences,
				RODIN_SEQUENT); // TODO
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
