/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eventb.smt.core.preferences.SMTSolverFactory.EDITABLE_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.ID_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.KIND_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.NAME_COL;
import static org.eventb.smt.core.preferences.SMTSolverFactory.PATH_COL;

import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.ui.internal.preferences.AbstractTableLabelProvider;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SMTSolversLabelProvider extends AbstractTableLabelProvider {
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISMTSolver solver = (ISMTSolver) element;
		switch (columnIndex) {
		case ID_COL:
			return solver.getID();
		case NAME_COL:
			return solver.getName();
		case KIND_COL:
			return solver.getKind().toString();
		case PATH_COL:
			return solver.getPath().toOSString();
		case EDITABLE_COL:
			return Boolean.toString(solver.isEditable());
		}
		return null;
	}
}