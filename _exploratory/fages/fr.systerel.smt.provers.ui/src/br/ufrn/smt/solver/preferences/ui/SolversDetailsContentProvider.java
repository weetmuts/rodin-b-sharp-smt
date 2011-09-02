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

package br.ufrn.smt.solver.preferences.ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.ufrn.smt.solver.preferences.SolverDetails;

/**
 * This is a content provider for the Solvers' details viewer.
 */
public class SolversDetailsContentProvider implements
		IStructuredContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(final Object inputElement) {
		return ((List<SolverDetails>) inputElement).toArray();
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		// TODO Auto-generated method stub

	}
}