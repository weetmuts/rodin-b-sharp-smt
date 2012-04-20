/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences;

import static org.eventb.smt.core.preferences.PreferenceManager.DEFAULT_SOLVER;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ARGS_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.EDITABLE_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ENABLED_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ID_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.NAME_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SMTLIB_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SOLVER_COL;
import static org.eventb.smt.ui.internal.preferences.SolverConfigsFieldEditor.DISABLED;
import static org.eventb.smt.ui.internal.preferences.SolverConfigsFieldEditor.ENABLED;

import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SolverConfigsLabelProvider extends AbstractTableLabelProvider {
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISolverConfig config = (ISolverConfig) element;
		final ISMTSolver solver = getPreferenceManager().getSMTSolversPrefs()
				.get(config.getSolverId());
		switch (columnIndex) {
		case ID_COL:
			return config.getID();
		case ENABLED_COL:
			if (solver == null) {
				return DISABLED;
			}
			return config.isEnabled() ? ENABLED : DISABLED;
		case NAME_COL:
			return config.getName();
		case SOLVER_COL:
			if (solver == null) {
				return DEFAULT_SOLVER;
			}
			return solver.getName();
		case ARGS_COL:
			return config.getArgs();
		case SMTLIB_COL:
			return config.getSmtlibVersion().toString();
		case EDITABLE_COL:
			return Boolean.toString(config.isEditable());
		}
		return null;
	}
}