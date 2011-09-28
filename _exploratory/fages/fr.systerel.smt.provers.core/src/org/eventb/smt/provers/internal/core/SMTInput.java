/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.core;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.smt.preferences.SMTPreferences;
import org.eventb.smt.preferences.SMTSolverConfiguration;

public class SMTInput extends XProverInput {
	private static final String SOLVER_ID = "solver_id";
	private static final String RODIN_SEQUENT = "rodin_sequent";

	private final SMTSolverConfiguration solverConfig;
	private final String poName;
	private final String translationPath;
	private final String veritPath;
	private final String error;

	protected SMTInput(final IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		final String solverId = reader.getString(SOLVER_ID);
		solverConfig = SMTPreferences.getSolverConfiguration(solverId);
		poName = RODIN_SEQUENT;
		translationPath = SMTPreferences.getTranslationPath();
		veritPath = SMTPreferences.getVeriTPath();
		error = validate();
	}

	public SMTInput(final boolean restricted, final long timeOutDelay,
			final String poName) {
		super(restricted, timeOutDelay);
		solverConfig = SMTPreferences.getSolverConfiguration();
		this.poName = poName;
		translationPath = SMTPreferences.getTranslationPath();
		veritPath = SMTPreferences.getVeriTPath();
		error = validate();
	}

	public SMTInput(final boolean restricted, final long timeOutDelay) {
		super(restricted, timeOutDelay);
		solverConfig = SMTPreferences.getSolverConfiguration();
		poName = RODIN_SEQUENT;
		translationPath = SMTPreferences.getTranslationPath();
		veritPath = SMTPreferences.getVeriTPath();
		error = validate();
	}

	private String validate() {
		if (poName != null && !poName.equals("")) {
			return null;
		} else {
			return "Illegal sequent name";
		}
	}

	public SMTSolverConfiguration getSolverConfig() {
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

		writer.putString(SOLVER_ID, solverConfig.getId());
	}
}
