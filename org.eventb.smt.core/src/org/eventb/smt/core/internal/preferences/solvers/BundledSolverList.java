/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences.solvers;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.preferences.BundledDescriptorList;
import org.eventb.smt.core.internal.prefs.SolverDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.SolverKind;

/**
 * @author Systerel (yguyot)
 *
 */
public class BundledSolverList extends BundledDescriptorList<ISolverDescriptor> {

	public static final String BUNDLED_SOLVERS_ID = SMTCore.PLUGIN_ID
			+ ".solvers"; //$NON-NLS-1$

	public BundledSolverList() {
		super(BUNDLED_SOLVERS_ID);
	}

	@Override
	public ISolverDescriptor[] newArray(int length) {
		return new ISolverDescriptor[length];
	}

	@Override
	protected void loadElement(IConfigurationElement element) {
		final BundledSolverLoader loader = new BundledSolverLoader(element);
		final String name = loader.getName();
		final SolverKind kind = loader.getKind();
		final IPath path = loader.getPath();
		add(new SolverDescriptor(name, kind, path));
	}

}
