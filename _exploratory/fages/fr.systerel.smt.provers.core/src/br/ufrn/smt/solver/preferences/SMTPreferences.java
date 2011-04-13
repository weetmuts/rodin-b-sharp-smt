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

import br.ufrn.smt.solver.preferences.SolverDetail;

/**
 * The SMT preferences class
 */
public class SMTPreferences {

	public SMTPreferences(SolverDetail solver, boolean usingPrepro,
			String preproPath) {
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
	private boolean usingPrepro;

	/**
	 * The preprocessing Solver Path
	 */
	private String preproPath;

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
	public static List<SolverDetail> CreateModel(String preferences) {
		List<SolverDetail> model = new ArrayList<SolverDetail>();

		final String[] rows = preferences.split(SEPARATOR2);
		for (String row : rows) {
			if (row.length() > 0) {
				final String[] columns = row.split(SEPARATOR1);
				model.add(new SolverDetail(columns[0], columns[1], columns[2],
						Boolean.valueOf(columns[3]), Boolean
								.valueOf(columns[4])));
			}
		}
		return model;
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
	public SMTPreferences(String solverSettingsPreferences,
			int selectedSolverIndex, boolean usingprepro, String prepropath) {
		List<SolverDetail> solvers = CreateModel(solverSettingsPreferences);
		if (selectedSolverIndex == -1) {
			solver = null;
		} else if (selectedSolverIndex < solvers.size()) {
			solver = solvers.get(selectedSolverIndex);
		} else {
			solver = null;
		}

		this.usingPrepro = usingprepro;
		this.preproPath = prepropath;
	}

}