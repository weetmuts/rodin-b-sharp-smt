/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static org.eventb.smt.core.internal.provers.SMTProversCore.PLUGIN_ID;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.SolverKind;

/**
 * @author Systerel (yguyot)
 *
 */
public class BundledSolverList extends BundledDescriptorList<ISolverDescriptor> {

	public static final String BUNDLED_SOLVERS_ID = PLUGIN_ID + ".solvers"; //$NON-NLS-1$

	public BundledSolverList() {
		super(BUNDLED_SOLVERS_ID);
	}

	@Override
	public ISolverDescriptor[] newArray(int length) {
		return new ISolverDescriptor[length];
	}

	@Override
	protected ISolverDescriptor loadElement(IConfigurationElement element) {
		final BundledSolverLoader loader = new BundledSolverLoader(element);
		final String name = loader.getName();
		final SolverKind kind = loader.getKind();
		final IPath path = loader.getPath();
		return new SolverDescriptor(name, true, kind, path);
	}

}
