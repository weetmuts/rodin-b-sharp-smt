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
import org.eventb.smt.core.IConfigDescriptor;

/**
 * List of "configuration" descriptors contributed to the extension point.
 *
 * @author Yoann Guyot
 */
public class BundledConfigList extends BundledDescriptorList<IConfigDescriptor> {

	public BundledConfigList() {
		super("configuration");
	}

	@Override
	protected IConfigDescriptor loadElement(IConfigurationElement element) {
		final BundledConfigLoader loader = new BundledConfigLoader(element);
		final String name = loader.getName();
		final String solverName = loader.getSolverName();
		final String args = loader.getArgs();
		final boolean enabled = true; // hard-coded default
		return new ConfigDescriptor(name, true, solverName, args, enabled);
	}

	@Override
	protected IConfigDescriptor[] newArray(int length) {
		return new IConfigDescriptor[length];
	}

}
