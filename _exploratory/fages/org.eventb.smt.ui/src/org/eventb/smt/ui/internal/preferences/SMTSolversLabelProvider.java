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

import org.eventb.smt.core.preferences.ISMTSolver;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SMTSolversLabelProvider extends AbstractTableLabelProvider {
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISMTSolver solver = (ISMTSolver) element;
		switch (columnIndex) {
		case 0:
			return solver.getID();
		case 1:
			return solver.getName();
		case 2:
			return solver.getKind().toString();
		case 3:
			return solver.getPath().toOSString();
		case 4:
			return Boolean.toString(solver.isEditable());
		}
		return null;
	}
}