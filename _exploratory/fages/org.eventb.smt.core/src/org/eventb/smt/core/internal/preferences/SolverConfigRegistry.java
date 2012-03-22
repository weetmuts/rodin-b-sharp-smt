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

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG_DETAILS;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.ExtensionLoadingException;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigRegistry extends AbstractRegistry<SolverConfigDesc> {
	public static String SOLVER_CONFIGS_ID = SMTCore.PLUGIN_ID
			+ ".configurations";

	private static final SolverConfigRegistry INSTANCE = new SolverConfigRegistry();

	private Map<String, SolverConfigDesc> registry;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private SolverConfigRegistry() {
		// Singleton implementation
	}

	public static SolverConfigRegistry getSolverConfigRegistry()
			throws InvalidRegistryObjectException, ExtensionLoadingException {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	@Override
	public Map<String, SolverConfigDesc> getRegistry() {
		return registry;
	}

	/**
	 * Initializes the registry using extensions to the solver configurations
	 * extension point.
	 * 
	 * @throws ExtensionLoadingException
	 */
	@Override
	protected synchronized void loadRegistry()
			throws ExtensionLoadingException, InvalidRegistryObjectException {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, SolverConfigDesc>();
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint point = xRegistry
				.getExtensionPoint(SOLVER_CONFIGS_ID);
		checkPoint(point, SOLVER_CONFIGS_ID);
		for (IConfigurationElement element : point.getConfigurationElements()) {
			final SolverConfigDesc desc = new SolverConfigDesc(element);
			desc.load();
			final String id = desc.getId();
			final SolverConfigDesc oldDesc = registry.put(id, desc);
			if (oldDesc != null) {
				registry.put(id, oldDesc);
				throw ExtensionLoadingException
						.makeIllegalExtensionException(id);
			} else {
				if (DEBUG_DETAILS)
					System.out
							.println("Registered solver configuration extension "
									+ id);
			}
		}
	}
}
