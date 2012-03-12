/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.preferences;

import static org.eventb.smt.core.provers.SMTSolver.UNKNOWN;
import static org.eventb.smt.core.translation.SMTLIBVersion.LATEST;

import org.eventb.smt.core.preferences.ISolverConfiguration;
import org.eventb.smt.core.provers.SMTSolver;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This class describes an SMT solver configuration.
 * 
 */
public class SMTSolverConfiguration implements ISolverConfiguration {
	public static final boolean EDITABLE = true;
	public static final String SEPARATOR = "|"; //$NON-NLS-1$

	private static final String ESCAPE_CHAR = "`"; //$NON-NLS-1$

	private static final String DEFAULT_CONFIG_ID = "";
	private static final String DEFAULT_CONFIG_NAME = "";
	private static final SMTSolver DEFAULT_SOLVER = UNKNOWN;
	private static final String DEFAULT_SOLVER_PATH = "";
	private static final String DEFAULT_SOLVER_ARGS = "";
	private static final SMTLIBVersion DEFAULT_SMTLIB_VERSION = LATEST;

	final private String id;

	final private String name;

	final private SMTSolver solver;

	final private String path;

	final private String args;

	final private SMTLIBVersion smtlibVersion;

	final private boolean editable;

	/**
	 * Constructs a new SMTSolverConfiguration
	 * 
	 * @param id
	 *            the id of the solver
	 * @param solver
	 *            the solver
	 * @param path
	 *            the path of the solver
	 * @param args
	 *            arguments that the solver can use
	 * @param smtlibVersion
	 *            version of SMT-LIB to use with this solver configuration
	 */
	public SMTSolverConfiguration(final String id, final String name,
			final SMTSolver solver, final String path, final String args,
			final SMTLIBVersion smtlibVersion, final boolean editable) {
		this.id = id;
		this.name = name;
		this.solver = solver;
		this.path = path;
		this.args = args;
		this.smtlibVersion = smtlibVersion;
		this.editable = editable;
	}

	public SMTSolverConfiguration(final String id, final String name,
			final SMTSolver solver, final String path, final String args,
			final SMTLIBVersion smtlibVersion) {
		this(id, name, solver, path, args, smtlibVersion, EDITABLE);
	}

	public SMTSolverConfiguration() {
		this(DEFAULT_CONFIG_ID, DEFAULT_CONFIG_NAME, DEFAULT_SOLVER,
				DEFAULT_SOLVER_PATH, DEFAULT_SOLVER_ARGS,
				DEFAULT_SMTLIB_VERSION);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SMTSolver getSolver() {
		return solver;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public SMTLIBVersion getSmtlibVersion() {
		return smtlibVersion;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(id).append(SEPARATOR);
		builder.append(name).append(SEPARATOR);
		builder.append(solver).append(SEPARATOR);
		builder.append(path).append(SEPARATOR);
		builder.append(args).append(SEPARATOR);
		builder.append(smtlibVersion).append(SEPARATOR);
		builder.append(editable).append(SEPARATOR);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (solver == null ? 0 : solver.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		result = prime * result + (args == null ? 0 : args.hashCode());
		result = prime * result
				+ (smtlibVersion == null ? 0 : smtlibVersion.hashCode());
		result = prime * result + (editable ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SMTSolverConfiguration other = (SMTSolverConfiguration) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (solver == null) {
			if (other.solver != null) {
				return false;
			}
		} else if (!solver.equals(other.solver)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (args == null) {
			if (other.args != null) {
				return false;
			}
		} else if (!args.equals(other.args)) {
			return false;
		}
		if (smtlibVersion == null) {
			if (other.smtlibVersion != null) {
				return false;
			}
		} else if (!smtlibVersion.equals(other.smtlibVersion)) {
			return false;
		}
		if (editable != other.editable) {
			return false;
		}
		return true;
	}
}
