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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eventb.smt.core.preferences.ISolverConfiguration;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SMTSolverConfigurationsLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ISolverConfiguration solver = (ISolverConfiguration) element;
		switch (columnIndex) {
		case 0:
			return solver.getID();
		case 1:
			return solver.getName();
		case 2:
			return solver.getSolver().toString();
		case 3:
			return solver.getPath();
		case 4:
			return solver.getArgs();
		case 5:
			return solver.getSmtlibVersion().toString();
		case 6:
			return Boolean.toString(solver.isEditable());
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}
}