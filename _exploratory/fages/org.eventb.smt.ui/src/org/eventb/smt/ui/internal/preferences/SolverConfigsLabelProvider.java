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

import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SolverConfigsLabelProvider extends AbstractTableLabelProvider {
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISolverConfig config = (ISolverConfig) element;
		switch (columnIndex) {
		case 0:
			return config.getID();
		case 1:
			return config.getName();
		case 2:
			return config.getSolverId();
		case 3:
			return config.getArgs();
		case 4:
			return config.getSmtlibVersion().toString();
		case 5:
			return Boolean.toString(config.isEditable());
		}
		return null;
	}
}