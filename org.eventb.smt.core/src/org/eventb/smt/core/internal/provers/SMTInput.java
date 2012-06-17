/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.provers;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.provers.ISMTConfiguration;

public class SMTInput extends XProverInput {
	private static final String CONFIG_ID = "config_id";
	private static final String SOLVER_CONFIG_ERROR = "Illegal solver configuration selected in the tactic";

	private final ISMTConfiguration config;
	private final String error;

	/**
	 * Constructs an SMT input with a serialized input
	 * 
	 * @param reader
	 *            the reader which will deserialize the input to use
	 * @throws SerializeException
	 *             thrown if the deserialization didn't get well
	 */
	protected SMTInput(final IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		config = SMTCore.getSMTConfiguration(reader.getString(CONFIG_ID));  // FIXME
		error = validate();
	}

	/**
	 * Constructs an SMT input for the given solver configuration
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            delay after which the reasoner is cancelled, must be
	 *            non-negative. A zero value denotes an infinite delay
	 * @param config
	 *            the configuration to use
	 */
	public SMTInput(final boolean restricted, long timeOutDelay,
			final ISMTConfiguration config) {
		super(restricted, timeOutDelay);
		this.config = config;
		error = validate();
	}

	/**
	 * Validates this SMT input.
	 * 
	 * @return the string to add to the error buffer, or <code>null</code> if
	 *         the input is valid.
	 */
	private String validate() {
		final StringBuilder errorBuilder = new StringBuilder();

		if (config == null) {
			if (errorBuilder.length() > 0) {
				errorBuilder.append("; ").append(SOLVER_CONFIG_ERROR);
			} else {
				errorBuilder.append(SOLVER_CONFIG_ERROR);
			}
		}

		if (errorBuilder.length() != 0) {
			return errorBuilder.toString();
		} else {
			return null;
		}
	}

	public ISMTConfiguration getConfiguration() {
		return config;
	}

	@Override
	public String getError() {
		return error != null ? error : super.getError();
	}

	@Override
	public boolean hasError() {
		return error != null || super.hasError();
	}

	@Override
	protected void serialize(IReasonerInputWriter writer)
			throws SerializeException {
		super.serialize(writer);
		writer.putString(CONFIG_ID, config.getName());
	}
}
