/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT): Creation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import java.util.ArrayList;
import java.util.List;

public class SMTPreferencesStore {

	private static final String SEPARATOR1 = ",,";
	private static final String SEPARATOR2 = ";";

	public static String CreatePreferences(List<SolverDetail> model) {
		final StringBuilder sb = new StringBuilder();

		for (SolverDetail solverDetail : model) {
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

}