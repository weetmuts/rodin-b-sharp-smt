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

import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;

public class SMTInput extends XProverInput {
	private static final String CONFIG_ID = "config_id";
	private static final String SOLVER_CONFIG_ERROR = "Illegal solver configuration selected in the tactic";
	private static final String SOLVER_ERROR = "Illegal solver selected in the tactic SMT configuration";

	private final ISolverConfig solverConfig;
	private final ISMTSolver solver;
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
		final ISolverConfigsPreferences solverConfigPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		final ISMTSolversPreferences smtSolversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		solverConfig = solverConfigPrefs.getSolverConfig(configId);
		solver = smtSolversPrefs.get(solverConfig.getSolverId());
		error = validate();
	}

	/**
	 * Constructs an SMT input for the given solver configuration
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param solverConfig
	 *            the configuration to set up
	 */
	public SMTInput(final boolean restricted, final ISolverConfig solverConfig) {
		super(restricted, solverConfig.getTimeOut());
		final ISMTSolversPreferences smtSolversPrefs = getPreferenceManager()
				.getSMTSolversPrefs();
		this.solverConfig = solverConfig;
		solver = smtSolversPrefs.get(solverConfig.getSolverId());
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

		if (solverConfig == null) {
			if (errorBuilder.length() > 0) {
				errorBuilder.append("; ").append(SOLVER_CONFIG_ERROR);
			} else {
				errorBuilder.append(SOLVER_CONFIG_ERROR);
			}
		}

		if (solver == null) {
			if (errorBuilder.length() > 0) {
				errorBuilder.append("; ").append(SOLVER_ERROR);
			} else {
				errorBuilder.append(SOLVER_ERROR);
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

	public ISMTSolver getSolver() {
		return solver;
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
