/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences;

import static org.eventb.smt.core.internal.preferences.SolverConfiguration.EDITABLE;
import static org.eventb.smt.core.internal.preferences.Utils.checkId;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.preferences.ExtensionLoadingException;

/**
 * Bridge class used by the solver configurations registry to store data about
 * solver configurations.
 * 
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigDesc extends AbstractDescriptor {
	/**
	 * Solver configuration instance lazily loaded using
	 * <code>configurationElement</code>
	 */
	private SolverConfiguration instance;

	public SolverConfigDesc(final IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	@Override
	public void load() throws ExtensionLoadingException,
			InvalidRegistryObjectException {
		/**
		 * The ID of the extension.
		 */
		final String localId = configurationElement.getAttribute("id");
		checkId(localId);
		/**
		 * The bundle name of the extension. Example
		 * <code>org.eventb.smt.verit</code>.
		 */
		final String nameSpace = configurationElement.getNamespaceIdentifier();
		id = nameSpace + "." + localId;

		instance = new SolverConfiguration(id,
				configurationElement.getAttribute("name"),
				configurationElement.getAttribute("solverid"),
				configurationElement.getAttribute("args"),
				parseVersion(configurationElement.getAttribute("smt-lib")),
				!EDITABLE);
	}

	@Override
	public SolverConfiguration getInstance() {
		return instance;
	}
}
