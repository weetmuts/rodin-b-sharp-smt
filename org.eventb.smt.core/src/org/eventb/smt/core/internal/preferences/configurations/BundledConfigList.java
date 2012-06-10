/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences.configurations;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.preferences.BundledDescriptorList;
import org.eventb.smt.core.internal.prefs.ConfigDescriptor;
import org.eventb.smt.core.prefs.IConfigDescriptor;

/**
 * @author Systerel (yguyot)
 *
 */
public class BundledConfigList extends BundledDescriptorList<IConfigDescriptor> {

	public static String SOLVER_CONFIGS_ID = SMTCore.PLUGIN_ID
			+ ".configurations"; //$NON-NLS-1$

	public BundledConfigList() {
		super(SOLVER_CONFIGS_ID);
	}

	@Override
	protected void loadElement(IConfigurationElement element) {
		final SolverConfigLoader loader = new SolverConfigLoader(element);
		final SolverConfiguration config = loader.load();
		add(new ConfigDescriptor(config));
	}

	@Override
	protected IConfigDescriptor[] newArray(int length) {
		return new IConfigDescriptor[length];
	}

}
