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
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

/**
 * @author Systerel (yguyot)
 *
 */
public class BundledConfigList extends BundledDescriptorList<IConfigDescriptor> {

	public static String SOLVER_CONFIGS_ID = PLUGIN_ID + ".configurations"; //$NON-NLS-1$

	public BundledConfigList() {
		super(SOLVER_CONFIGS_ID);
	}

	@Override
	protected IConfigDescriptor loadElement(IConfigurationElement element) {
		final BundledConfigLoader loader = new BundledConfigLoader(element);
		final String name = loader.getName();
		final String solverName = loader.getSolverName();
		final String args = loader.getArgs();
		final TranslationApproach approach = loader.getTranslationApproach();
		final SMTLIBVersion version = loader.getVersion();
		return new ConfigDescriptor(name, solverName, args, approach, version);
	}

	@Override
	protected IConfigDescriptor[] newArray(int length) {
		return new IConfigDescriptor[length];
	}

}
