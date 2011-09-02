/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT): Creation
 *     Systerel (YGU): Documentation
 *******************************************************************************/

package br.ufrn.smt.solver.preferences.ui;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import br.ufrn.smt.solver.preferences.SolverDetails;

/**
 * This class provides text for each column of a solvers table viewer.
 */
class SolversDetailsLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final SolverDetails solver = (SolverDetails) element;
		switch (columnIndex) {
		case 0:
			return solver.getId();
		case 1:
			return solver.getPath();
		case 2:
			return solver.getArgs();
		case 3:
			return Boolean.toString(solver.getsmtV1_2());
		case 4:
			return Boolean.toString(solver.getsmtV2_0());
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