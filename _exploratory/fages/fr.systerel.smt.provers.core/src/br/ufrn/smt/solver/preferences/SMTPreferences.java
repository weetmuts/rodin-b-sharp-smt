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
	private static final String SEPARATOR1 = ",,";
	private static final String SEPARATOR2 = ";";
	public static final String VERITPATH = "veritpath";
	public static final String SOLVERINDEX = "solverindex";
	public static final String SOLVERPREFERENCES = "solverpreferences";
	public static final String DEFAULT_SOLVERPREFERENCES = "";
	public static final int DEFAULT_SOLVERINDEX = -1;
	public static final String DEFAULT_VERITPATH = "";
	public static final String PREFERENCES_ID = "fr.systerel.smt.provers.ui";

	private final String veriTPath;
	/**
	 * The solver's settings
	 */
	private SolverDetail solver;

	public SMTPreferences(final SolverDetail solver, final String veriTPath) {
		super();
		this.solver = solver;
		this.veriTPath = veriTPath;
	}

	/**
	 * Constructs a new SMT preferences
	 * 
	 * @param solverSettingsPreferences
	 *            The string that contains the details of the solvers
	 * @param selectedSolverIndex
	 *            the index of the selected solver
	 * @param veriTPath
	 *            The path of the veriT solver for pre-processing
	 */
	public SMTPreferences(final String solverSettingsPreferences,
			final int selectedSolverIndex, final String veriTPath) {
		final List<SolverDetail> solvers = CreateModel(solverSettingsPreferences);
		if (selectedSolverIndex >= 0 && selectedSolverIndex < solvers.size()) {
			solver = solvers.get(selectedSolverIndex);
		} else {
			if (solvers.size() > 0) {
				//TODO Show message "The solver 1 was selected automatically"
				solver = solvers.get(0);
			} else {
				throw new IllegalArgumentException("No SMT solver installed");
			}
		}
		this.veriTPath = veriTPath;
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

	public SolverDetail getSolver() {
		return solver;
	}

	public String getVeriTPath() {
		return veriTPath;
	}

	public void toString(final StringBuilder builder) {
		builder.append("SMTPreferences [solver=");
		builder.append(solver);
		builder.append(", ");
		builder.append(VERITPATH);
		builder.append("=");
		builder.append(veriTPath);
		builder.append("]");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (veriTPath == null ? 0 : veriTPath.hashCode());
		result = prime * result + (solver == null ? 0 : solver.hashCode());
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
		if (veriTPath == null) {
			if (other.veriTPath != null) {
				return false;
			}
		} else if (!veriTPath.equals(other.veriTPath)) {
			return false;
		}
		if (solver == null) {
			if (other.solver != null) {
				return false;
			}
		} else if (!solver.equals(other.solver)) {
			return false;
		}
		return true;
	}
}