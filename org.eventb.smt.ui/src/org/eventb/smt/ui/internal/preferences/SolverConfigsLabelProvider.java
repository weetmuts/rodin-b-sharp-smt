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
import static org.eventb.smt.core.preferences.SolverConfigFactory.APPROACH_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ARGS_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.EDITABLE_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ENABLED_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ID_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.NAME_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SMTLIB_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SOLVER_COL;
import static org.eventb.smt.ui.internal.provers.SMTProversUI.DISABLE_CONFIG_IMG_ID;
import static org.eventb.smt.ui.internal.provers.SMTProversUI.ENABLE_CONFIG_IMG_ID;
import static org.eventb.smt.ui.internal.provers.SMTProversUI.getDefault;

import org.eclipse.swt.graphics.Image;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SolverConfigsLabelProvider extends AbstractTableLabelProvider {

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISolverConfig config = (ISolverConfig) element;
		switch (columnIndex) {
		case ID_COL:
			return config.getID();
		case ENABLED_COL:
			return null;
		case NAME_COL:
			return config.getName();
		case SOLVER_COL:
			final ISMTSolver solver = getPreferenceManager()
					.getSMTSolversPrefs().get(config.getSolverId());
			if (solver == null) {
				return DEFAULT_SOLVER;
			}
			return solver.getName();
		case ARGS_COL:
			return config.getArgs();
		case APPROACH_COL:
			return config.getTranslationApproach().toString();
		case SMTLIB_COL:
			return config.getSmtlibVersion().toString();
		case EDITABLE_COL:
			return Boolean.toString(config.isEditable());
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		final ISolverConfig config = (ISolverConfig) element;
		if (columnIndex == ENABLED_COL) {
			if (config.isEnabled()) {
				return getDefault().getImageRegistry()
						.get(ENABLE_CONFIG_IMG_ID);
			} else {
				return getDefault().getImageRegistry().get(
						DISABLE_CONFIG_IMG_ID);
			}
		} else {
			return super.getColumnImage(element, columnIndex);
		}
	}
}