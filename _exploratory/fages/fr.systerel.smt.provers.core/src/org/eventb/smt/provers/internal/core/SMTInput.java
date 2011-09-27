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
import org.eventb.smt.translation.SMTLIBVersion;

public class SMTInput extends XProverInput {
	private static final String SMTLIB_VERSION = "smtlib";
	private static final String SOLVER = "solver";
	private static final String SOLVER_NAME = "solver_name";
	private static final String SOLVER_PATH = "solver_path";
	private static final String SOLVER_ARGS = "args";
	private static final String PO_NAME = "po_name";
	private static final String TRANSLATION_PATH = "translation_path";
	private static final String VERIT_PATH = "verit_path";

	private final SMTLIBVersion smtlibVersion;
	private final SMTSolver solver;
	private final String solverName;
	private final String solverPath;
	private final String solverArguments;
	private final String poName;
	private final String translationPath;
	private final String veritPath;
	private final String error;

	protected SMTInput(final IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		smtlibVersion = SMTLIBVersion.getVersion(reader
				.getString(SMTLIB_VERSION));
		solver = SMTSolver.getSolver(reader.getString(SOLVER));
		solverName = reader.getString(SOLVER_NAME);
		solverPath = reader.getString(SOLVER_PATH);
		solverArguments = reader.getString(SOLVER_ARGS);
		poName = reader.getString(PO_NAME);
		translationPath = reader.getString(TRANSLATION_PATH);
		veritPath = reader.getString(VERIT_PATH);
		error = validate();
	}

	public SMTInput(final boolean restricted, final long timeOutDelay,
			final SMTLIBVersion smtlibVersion, final SMTSolver solver,
			final String solverName, final String solverPath,
			final String solverParameters, final String poName,
			final String translationPath, final String veritPath) {
		super(restricted, timeOutDelay);
		this.smtlibVersion = smtlibVersion;
		this.solver = solver;
		this.solverName = solverName;
		this.solverPath = solverPath;
		this.solverArguments = solverParameters;
		this.poName = poName;
		this.translationPath = translationPath;
		this.veritPath = veritPath;
		error = validate();
	}

	public SMTInput(final boolean restricted, final long timeOutDelay,
			final SMTLIBVersion smtlibVersion, final SMTSolver solver,
			final String solverName, final String solverPath,
			final String solverParameters, final String poName,
			final String translationPath) {
		this(restricted, timeOutDelay, smtlibVersion, solver, solverName,
				solverPath, solverParameters, poName, translationPath, "");
	}

	private String validate() {
		if (poName != null && !poName.equals("")) {
			return null;
		} else {
			return "Illegal sequent name";
		}
	}

	public SMTLIBVersion getSmtlibVersion() {
		return smtlibVersion;
	}

	public SMTSolver getSolver() {
		return solver;
	}

	public String getSolverName() {
		return solverName;
	}

	public String getSolverPath() {
		return solverPath;
	}

	public String getSolverArguments() {
		return solverArguments;
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

		writer.putString(SMTLIB_VERSION, smtlibVersion.toString());
		writer.putString(SOLVER, solver.toString());
		writer.putString(SOLVER_NAME, solverName);
		writer.putString(SOLVER_PATH, solverPath);
		writer.putString(SOLVER_ARGS, solverArguments);
		writer.putString(PO_NAME, poName);
		writer.putString(TRANSLATION_PATH, translationPath);
		writer.putString(VERIT_PATH, veritPath);
	}
}
