/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.preferences;

import static org.eventb.smt.internal.preferences.SMTPreferences.SEPARATOR1;
import static org.eventb.smt.internal.preferences.SMTPreferences.SEPARATOR2;
import static org.eventb.smt.internal.provers.core.SMTSolver.UNKNOWN;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.smt.internal.provers.core.SMTSolver;
import org.eventb.smt.internal.translation.SMTLIBVersion;

/**
 * This is a class to describe a solver detail
 * 
 */
public class SMTSolverConfiguration {
	public static final String DEFAULT_CONFIG_ID = "";
	public static final SMTSolver DEFAULT_SOLVER = UNKNOWN;
	public static final String DEFAULT_SOLVER_PATH = "";
	public static final String DEFAULT_SOLVER_ARGS = "";
	public static final SMTLIBVersion DEFAULT_SMTLIB_VERSION = V1_2;

	private String id;

	private SMTSolver solver;

	private String path;

	private String args;

	private SMTLIBVersion smtlibVersion;

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
	public SMTSolverConfiguration(final String id, final SMTSolver solver,
			final String path, final String args,
			final SMTLIBVersion smtlibVersion) {
		this.id = id;
		this.solver = solver;
		this.path = path;
		this.args = args;
		this.smtlibVersion = smtlibVersion;
	}

	public SMTSolverConfiguration() {
		this(DEFAULT_CONFIG_ID, DEFAULT_SOLVER, DEFAULT_SOLVER_PATH,
				DEFAULT_SOLVER_ARGS, DEFAULT_SMTLIB_VERSION);
	}

	public String getId() {
		return id;
	}

	public SMTSolver getSolver() {
		return solver;
	}

	public String getPath() {
		return path;
	}

	public String getArgs() {
		return args;
	}

	public SMTLIBVersion getSmtlibVersion() {
		return smtlibVersion;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setSolver(final SMTSolver solver) {
		this.solver = solver;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setArgs(final String args) {
		this.args = args;
	}

	public void setSmtlibVersion(final SMTLIBVersion smtlibVersion) {
		this.smtlibVersion = smtlibVersion;
	}

	public static final String toString(
			final List<SMTSolverConfiguration> solversDetails) {
		final StringBuilder sb = new StringBuilder();

		for (final SMTSolverConfiguration solverDetail : solversDetails) {
			sb.append(solverDetail.getId());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getSolver());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getPath());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getArgs());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getSmtlibVersion());
			sb.append(SEPARATOR2);
		}

		return sb.toString();
	}

	public void toString(final StringBuilder builder) {
		builder.append("SMTSolverConfiguration [id=");
		builder.append(id);
		builder.append(", solver=");
		builder.append(solver);
		builder.append(", path=");
		builder.append(path);
		builder.append(", args=");
		builder.append(args);
		builder.append(", smtlibVersion=");
		builder.append(smtlibVersion);
		builder.append("]");
	}

	public static Set<String> getUsedIds(
			List<SMTSolverConfiguration> solverConfigs) {
		final Set<String> usedIds = new HashSet<String>();
		for (final SMTSolverConfiguration solverConfig : solverConfigs) {
			usedIds.add(solverConfig.getId());
		}
		return usedIds;
	}

	public static boolean contains(
			final List<SMTSolverConfiguration> solverConfigs,
			final SMTSolverConfiguration solverConfig) {
		for (final SMTSolverConfiguration curConfig : solverConfigs) {
			if (curConfig.equals(solverConfig)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (solver == null ? 0 : solver.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		result = prime * result + (args == null ? 0 : args.hashCode());
		result = prime * result
				+ (smtlibVersion == null ? 0 : smtlibVersion.hashCode());
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
		return true;
	}
}
