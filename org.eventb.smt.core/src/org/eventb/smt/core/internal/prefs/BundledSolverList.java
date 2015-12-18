/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SolverKind;

/**
 * List of "solver" descriptors contributed to the extension point.
 * 
 * @author Yoann Guyot
 */
public class BundledSolverList extends BundledDescriptorList<ISolverDescriptor> {

	public BundledSolverList() {
		super("solver");
	}

	@Override
	protected ISolverDescriptor[] newArray(int length) {
		return new ISolverDescriptor[length];
	}

	@Override
	protected ISolverDescriptor loadElement(IConfigurationElement element) {
		final BundledSolverLoader loader = new BundledSolverLoader(element);
		final String name = loader.getName();
		final SolverKind kind = loader.getKind();
		final IPath path = loader.getPath();
		loader.extractLibraries();
		return new SolverDescriptor(name, true, kind, path);
	}

}
