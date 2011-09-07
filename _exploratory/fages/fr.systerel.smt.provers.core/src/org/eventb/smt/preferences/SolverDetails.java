/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.preferences;

import static org.eventb.smt.preferences.SMTPreferences.SEPARATOR1;
import static org.eventb.smt.preferences.SMTPreferences.SEPARATOR2;

import java.util.List;

/**
 * This is a class to describe a solver detail
 * 
 */
public class SolverDetails {

	private String id;

	private String path;

	private String args;

	private boolean smtV1_2;

	private boolean smtV2_0;

	/**
	 * Constructs a new SolverDetails
	 * 
	 * @param id
	 *            the id of the solver
	 * @param path
	 *            the path of the solver
	 * @param args
	 *            arguments that the solver can use
	 * @param smtV1_2
	 *            determines if the solver will be used for SMT 1.2
	 * @param smtV2_0
	 *            determines if the solver will be used for SMT 2.0
	 */
	public SolverDetails(final String id, final String path, final String args,
			final boolean smtV1_2, final boolean smtV2_0) {
		this.id = id;
		this.path = path;
		this.args = args;
		this.smtV1_2 = smtV1_2;
		this.smtV2_0 = smtV2_0;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getArgs() {
		return args;
	}

	public boolean getsmtV1_2() {
		return smtV1_2;
	}

	public boolean getsmtV2_0() {
		return smtV2_0;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setArgs(final String args) {
		this.args = args;
	}

	public void setSmtV1_2(final boolean smtV1_2) {
		this.smtV1_2 = smtV1_2;
	}

	public void setSmtV2_0(final boolean smtV2_0) {
		this.smtV2_0 = smtV2_0;
	}

	public static final String toString(final List<SolverDetails> solversDetails) {
		final StringBuilder sb = new StringBuilder();

		for (final SolverDetails solverDetail : solversDetails) {
			sb.append(solverDetail.getId());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getPath());
			sb.append(SEPARATOR1);
			sb.append(solverDetail.getArgs());
			sb.append(SEPARATOR1);
			sb.append(Boolean.toString(solverDetail.getsmtV1_2()));
			sb.append(SEPARATOR1);
			sb.append(Boolean.toString(solverDetail.getsmtV2_0()));
			sb.append(SEPARATOR2);
		}

		return sb.toString();
	}

	public void toString(final StringBuilder builder) {
		builder.append("SolverDetails [id=");
		builder.append(id);
		builder.append(", path=");
		builder.append(path);
		builder.append(", args=");
		builder.append(args);
		builder.append(", smtV1_2=");
		builder.append(smtV1_2);
		builder.append(", smtV2_0=");
		builder.append(smtV2_0);
		builder.append("]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (args == null ? 0 : args.hashCode());
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		result = prime * result + (smtV1_2 ? 1231 : 1237);
		result = prime * result + (smtV2_0 ? 1231 : 1237);
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
		final SolverDetails other = (SolverDetails) obj;
		if (args == null) {
			if (other.args != null) {
				return false;
			}
		} else if (!args.equals(other.args)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (smtV1_2 != other.smtV1_2) {
			return false;
		}
		if (smtV2_0 != other.smtV2_0) {
			return false;
		}
		return true;
	}
}
