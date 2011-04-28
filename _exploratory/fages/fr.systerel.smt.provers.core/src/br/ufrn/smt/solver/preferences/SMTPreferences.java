/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * The SMT preferences class
 */
public class SMTPreferences {

	public SMTPreferences(final SolverDetail solver, final boolean usingPrepro,
			final String preproPath) {
		super();
		this.solver = solver;
		this.usingPrepro = usingPrepro;
		this.preproPath = preproPath;
	}

	private static final String SEPARATOR1 = ",,";
	private static final String SEPARATOR2 = ";";

	/**
	 * The solver's settings
	 */
	private SolverDetail solver;

	/**
	 * The preprocessing boolean option
	 */
	private final boolean usingPrepro;

	/**
	 * The preprocessing Solver Path
	 */
	private final String preproPath;

	public SolverDetail getSolver() {
		return solver;
	}

	public Boolean getUsingPrepro() {
		return usingPrepro;
	}

	public String getPreproPath() {
		return preproPath;
	}

	/**
	 * Creates a list with all solver detail elements from the preferences
	 * String
	 * 
	 * @param preferences
	 *            The String that contains the details of the solver
	 * @return The list of solvers and its details parsed from the preferences
	 *         String
	 */
	public static List<SolverDetail> CreateModel(final String preferences) {
		final List<SolverDetail> model = new ArrayList<SolverDetail>();

		final String[] rows = preferences.split(SEPARATOR2);
		for (final String row : rows) {
			if (row.length() > 0) {
				final String[] columns = row.split(SEPARATOR1);
				model.add(new SolverDetail(columns[0], columns[1], columns[2],
						Boolean.valueOf(columns[3]), Boolean
								.valueOf(columns[4])));
			}
		}
		return model;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	public void toString(final StringBuilder builder) {
		builder.append("SMTPreferences [solver=");
		builder.append(solver);
		builder.append(", usingPrepro=");
		builder.append(usingPrepro);
		builder.append(", preproPath=");
		builder.append(preproPath);
		builder.append("]");
	}

	/**
	 * Constructs a new SMT preferences
	 * 
	 * @param solverSettingsPreferences
	 *            The string that contains the details of the solvers
	 * @param selectedSolverIndex
	 *            the index of the selected solver
	 * @param usingprepro
	 *            the boolean that defines if it will use veriT pre-processing
	 *            or not
	 * @param prepropath
	 *            The path of the veriT solver for pre-processing
	 */
	public SMTPreferences(final String solverSettingsPreferences,
			final int selectedSolverIndex, final boolean usingprepro,
			final String prepropath) {
		final List<SolverDetail> solvers = CreateModel(solverSettingsPreferences);
		if (selectedSolverIndex == -1) {
			solver = null;
		} else if (selectedSolverIndex < solvers.size()) {
			solver = solvers.get(selectedSolverIndex);
		} else {
			solver = null;
		}

		usingPrepro = usingprepro;
		preproPath = prepropath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (preproPath == null ? 0 : preproPath.hashCode());
		result = prime * result + (solver == null ? 0 : solver.hashCode());
		result = prime * result + (usingPrepro ? 1231 : 1237);
		return result;
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
		final SMTPreferences other = (SMTPreferences) obj;
		if (preproPath == null) {
			if (other.preproPath != null) {
				return false;
			}
		} else if (!preproPath.equals(other.preproPath)) {
			return false;
		}
		if (solver == null) {
			if (other.solver != null) {
				return false;
			}
		} else if (!solver.equals(other.solver)) {
			return false;
		}
		if (usingPrepro != other.usingPrepro) {
			return false;
		}
		return true;
	}

}