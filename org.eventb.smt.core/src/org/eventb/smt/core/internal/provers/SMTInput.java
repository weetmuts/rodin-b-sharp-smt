/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import static org.eventb.smt.core.internal.provers.Messages.nullSMTConfigurationError;
import static org.eventb.smt.core.internal.provers.Messages.unknownSMTConfigurationError;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;

/**
 * Input to the reasoner that can run an external SMT solver. This is the same
 * input as an external prover, with the addition of the name of the SMT solver
 * configuration to run.
 * <p>
 * Validation is done lazily when the first selector method is called. This is
 * far from perfect and does not ensure timely verification by the reasoner, but
 * it is the best we can do with the current XProver API.
 * </p>
 * 
 * @author Yoann Guyot
 */
public class SMTInput extends XProverInput {

	private static final String CONFIG_NAME = "config_id";

	private final String configName;

	private volatile Validator validator;

	/**
	 * Constructs an SMT input from a serialized input.
	 * 
	 * @param reader
	 *            the reader which will deserialize the input to use
	 * @throws SerializeException
	 *             thrown if the deserialization didn't get well
	 */
	protected SMTInput(IReasonerInputReader reader) throws SerializeException {
		super(reader);
		configName = reader.getString(CONFIG_NAME);
	}

	/**
	 * Constructs an SMT input for the given solver configuration name.
	 * 
	 * @param configName
	 *            the name of the SMT configuration to run
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            delay after which the reasoner is cancelled, must be
	 *            non-negative. A zero value denotes an infinite delay
	 */
	public SMTInput(String configName, boolean restricted, long timeOutDelay) {
		super(restricted, timeOutDelay);
		this.configName = configName;
	}

	public String getConfigName() {
		return configName;
	}

	public SMTConfiguration getConfiguration() {
		maybeValidate();
		return validator.configuration;
	}

	@Override
	public String getError() {
		maybeValidate();
		return validator.error;
	}

	@Override
	public boolean hasError() {
		maybeValidate();
		return validator.error != null;
	}

	@Override
	protected void serialize(IReasonerInputWriter writer)
			throws SerializeException {
		super.serialize(writer);
		writer.putString(CONFIG_NAME, configName);
	}

	private void maybeValidate() {
		if (validator == null) {
			doValidate();
		}
	}

	/*
	 * Validates arguments lazily. This method is thread-safe because this
	 * method locks the instance, the validator field is volatile and Validator
	 * instances are immutable.
	 */
	private synchronized void doValidate() {
		if (validator == null) {
			final String knownErrors = super.hasError() ? super.getError() : "";
			validator = new Validator(configName, knownErrors);
		}
	}

	/**
	 * Implements validation of the configuration attribute. Instances of this
	 * helper class are immutable, and therefore thread-safe.
	 */
	private static class Validator {

		public final SMTConfiguration configuration;
		public final String error;

		public Validator(String configName, String knownErrors) {
			final StringBuilder errorBuilder = new StringBuilder(knownErrors);
			if (configName == null) {
				appendErrorMessage(errorBuilder, nullSMTConfigurationError);
				configuration = null;
			} else {
				configuration = SMTConfiguration.valueOf(configName);
				if (configuration == null) {
					appendErrorMessage(errorBuilder,
							unknownSMTConfigurationError(configName));
				}
			}
			if (errorBuilder.length() == 0) {
				error = null;
			} else {
				error = errorBuilder.toString();
			}
		}

		private static void appendErrorMessage(StringBuilder errorBuilder,
				String message) {
			if (errorBuilder.length() > 0) {
				errorBuilder.append("; ");
			}
			errorBuilder.append(message);
		}
	}

}
