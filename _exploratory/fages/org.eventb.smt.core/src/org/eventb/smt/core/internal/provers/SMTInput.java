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

import static org.eventb.smt.core.preferences.PreferenceManager.getSMTPrefs;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.smt.core.preferences.IPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;

public class SMTInput extends XProverInput {
	private static final String CONFIG_ID = "config_id";
	private static final String RODIN_SEQUENT = "rodin_sequent";
	private static final String SEQUENT_NAME_ERROR = "Illegal sequent name";
	private static final String SOLVER_CONFIG_ERROR = "Illegal solver configuration selected in the tactic";

	private final ISolverConfig solverConfig;
	private final String poName;
	private final String translationPath;
	private final String veritPath;
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
		final String configId = reader.getString(CONFIG_ID);
		final IPreferences smtPrefs = getSMTPrefs();
		solverConfig = smtPrefs.getSolverConfig(configId);
		poName = RODIN_SEQUENT;
		translationPath = smtPrefs.getTranslationPath();
		veritPath = smtPrefs.getVeriTPath();
		error = validate();
	}

	/**
	 * Constructs an SMT input for the currently selected solver configuration
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            delay before timeout in milliseconds
	 */
	public SMTInput(final boolean restricted, final long timeOutDelay) {
		super(restricted, timeOutDelay);
		final IPreferences smtPrefs = getSMTPrefs();
		solverConfig = smtPrefs.getSelectedConfig();
		poName = RODIN_SEQUENT;
		translationPath = smtPrefs.getTranslationPath();
		veritPath = smtPrefs.getVeriTPath();
		error = validate();
	}

	/**
	 * Constructs an SMT input for the given solver configuration
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            delay before timeout in milliseconds
	 * @param solverConfig
	 *            the solver configuration to set up
	 */
	public SMTInput(final boolean restricted, final long timeOutDelay,
			final ISolverConfig solverConfig) {
		super(restricted, timeOutDelay);
		final IPreferences smtPrefs = getSMTPrefs();
		this.solverConfig = solverConfig;
		poName = RODIN_SEQUENT;
		translationPath = smtPrefs.getTranslationPath();
		veritPath = smtPrefs.getVeriTPath();
		error = validate();
	}

	/**
	 * Constructs an SMT input for the given solver configuration
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            delay before timeout in milliseconds
	 * @param configId
	 *            id of the solver configuration to set up
	 */
	public SMTInput(final boolean restricted, final long timeOutDelay,
			final String configId) {
		super(restricted, timeOutDelay);
		final IPreferences smtPrefs = getSMTPrefs();
		solverConfig = smtPrefs.getSolverConfig(configId);
		poName = RODIN_SEQUENT;
		translationPath = smtPrefs.getTranslationPath();
		veritPath = smtPrefs.getVeriTPath();
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

		if (poName == null || poName.equals("")) {
			errorBuilder.append(SEQUENT_NAME_ERROR);
		}

		if (solverConfig == null) {
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

	public ISolverConfig getSolverConfig() {
		return solverConfig;
	}

	public String getPOName() {
		return poName;
	}

	public String getTranslationPath() {
		return translationPath;
	}

	public String getVeritPath() {
		return veritPath;
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

		writer.putString(CONFIG_ID, solverConfig.getID());
	}
}
