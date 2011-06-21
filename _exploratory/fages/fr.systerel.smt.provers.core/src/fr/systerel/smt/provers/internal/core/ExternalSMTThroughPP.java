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
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import fr.systerel.smt.provers.core.SMTProversCore;

/**
 * Runs an external SMT prover as a reasoner.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSMTThroughPP extends XProverReasoner {
	private final SMTPreferences preferences;
	private static final String ARG_KEY = "arg";

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
	public void serializeInput(final IReasonerInput rInput,
			final IReasonerInputWriter writer) throws SerializeException {

		final SMTInput input = (SMTInput) rInput;
		final String delayString = Long.toString(input.timeOutDelay);
		final String restrictedString = Boolean.toString(input.restricted);
		writer.putString(ARG_KEY, restrictedString + ":" + delayString + ":"
				+ input.sequentName);
	}

	@Override
	public IReasonerInput deserializeInput(
			final IReasonerInputReader reasonerInputReader)
			throws SerializeException {

		final String arg = reasonerInputReader.getString(ARG_KEY);
		final String[] args = arg.split(":");
		if (args.length != 3) {
			throw new SerializeException(new IllegalStateException(
					"Malformed argument: " + arg));
		}
		return new SMTInput(Boolean.parseBoolean(args[2]),
				Long.parseLong(args[1]), args[2]);
	}

	@Override
	public XProverCall newProverCall(final IReasonerInput input,
			final Iterable<Predicate> hypotheses, final Predicate goal,
			final IProofMonitor pm) {
		final String sequentName = ((SMTInput) input).sequentName;
		return new SMTPPCall(hypotheses, goal, pm, preferences, sequentName);
	}
}
