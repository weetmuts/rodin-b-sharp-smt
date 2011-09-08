/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.preferences;

import static org.eventb.smt.preferences.Messages.SMTPreferences_IllegalSMTSolverSettings;
import static org.eventb.smt.preferences.Messages.SMTPreferences_NoSMTSolverSelected;
import static org.eventb.smt.preferences.Messages.SMTPreferences_NoSMTSolverSet;
import static org.eventb.smt.preferences.Messages.SMTPreferences_VeriTPathNotSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * The SMT preferences class
 */
public class SMTPreferences {
	public static final IllegalArgumentException IllegalSMTSolverSettingsException = new IllegalArgumentException(
			SMTPreferences_IllegalSMTSolverSettings);
	public static final IllegalArgumentException NoSMTSolverSelectedException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSelected);
	public static final IllegalArgumentException NoSMTSolverSetException = new IllegalArgumentException(
			SMTPreferences_NoSMTSolverSet);
	public static final IllegalArgumentException VeriTPathNotSetException = new IllegalArgumentException(
			SMTPreferences_VeriTPathNotSet);
	public static final IllegalArgumentException TranslationPathNotSetException = new IllegalArgumentException(
			Messages.SMTPreferences_TranslationPathNotSet);

	public static final String SEPARATOR1 = ",,";
	public static final String SEPARATOR2 = ";";
	public static final String TRANSLATION_PATH_ID = "translationpath";
	public static final String VERIT_PATH_ID = "veritpath";
	public static final String SOLVER_INDEX_ID = "solverindex";
	public static final String SOLVER_PREFERENCES_ID = "solverpreferences";
	public static final String DEFAULT_SOLVER_PREFERENCES = "";
	public static final String DEFAULT_TRANSLATION_PATH = System
			.getProperty("java.io.tmpdir")
			+ File.separatorChar
			+ "rodin_smtlib_temp_files";
	public static final int DEFAULT_SOLVER_INDEX = -1;
	public static final String DEFAULT_VERIT_PATH = "";

	private String translationPath = null;

	private String veriTPath = null;
	/**
	 * The solver's settings
	 */
	private SolverDetails solver;

	public SMTPreferences(final String translationPath,
			final SolverDetails solver, final String veriTPath) {
		super();
		this.translationPath = translationPath;
		this.solver = solver;
		this.veriTPath = veriTPath;
	}

	/**
	 * Constructs a new SMT preferences
	 * 
	 * @param translationPath
	 *            the temporary files directory
	 * @param solverSettings
	 *            The string that contains the details of the solvers
	 * @param selectedSolverIndex
	 *            the index of the selected solver
	 * @throws PatternSyntaxException
	 *             if the given settings are not formatted correctly
	 */
	public SMTPreferences(final String translationPath,
			final String solverSettings, final int selectedSolverIndex)
			throws PatternSyntaxException, IllegalArgumentException {
		this.translationPath = translationPath;

		if (solverSettings == null) {
			throw IllegalSMTSolverSettingsException;
		}

		final List<SolverDetails> solvers = parsePreferencesString(solverSettings);
		try {
			solver = solvers.get(selectedSolverIndex);
		} catch (final IndexOutOfBoundsException ioobe) {
			if (solvers.size() > 0) {
				throw NoSMTSolverSelectedException;
			} else {
				throw NoSMTSolverSetException;
			}
		}
	}

	/**
	 * Constructs a new SMT preferences
	 * 
	 * @param solverSettings
	 *            The string that contains the details of the solvers
	 * @param selectedSolverIndex
	 *            the index of the selected solver
	 * @param veriTPath
	 *            The path of the veriT solver for pre-processing
	 * @throws PatternSyntaxException
	 *             if the given settings are not formatted correctly
	 */
	public SMTPreferences(final String translationPath,
			final String solverSettings, final int selectedSolverIndex,
			final String veriTPath) throws PatternSyntaxException,
			IllegalArgumentException {
		this(translationPath, solverSettings, selectedSolverIndex);

		if (veriTPath != null && !veriTPath.isEmpty()) {
			this.veriTPath = veriTPath;
		} else {
			throw VeriTPathNotSetException;
		}
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
	public static List<SolverDetails> parsePreferencesString(
			final String preferences) throws PatternSyntaxException {
		final List<SolverDetails> solverDetail = new ArrayList<SolverDetails>();

		final String[] rows = preferences.split(SEPARATOR2);
		for (final String row : rows) {
			if (row.length() > 0) {
				final String[] columns = row.split(SEPARATOR1);
				solverDetail.add(new SolverDetails(columns[0], columns[1],
						columns[2], Boolean.valueOf(columns[3]), Boolean
								.valueOf(columns[4])));
			}
		}
		return solverDetail;
	}

	public String getTranslationPath() {
		return translationPath;
	}

	public SolverDetails getSolver() {
		return solver;
	}

	public String getVeriTPath() {
		return veriTPath;
	}

	public void toString(final StringBuilder builder) {
		builder.append("SMTPreferences [");
		builder.append(TRANSLATION_PATH_ID).append("=").append(translationPath);
		builder.append(", ");
		builder.append("solver=").append(solver);
		builder.append(", ");
		builder.append(VERIT_PATH_ID).append("=").append(veriTPath);
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
				+ (translationPath == null ? 0 : translationPath.hashCode());
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

		if (translationPath == null) {
			if (other.translationPath != null) {
				return false;
			}
		} else if (!translationPath.equals(other.translationPath)) {
			return false;
		}

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