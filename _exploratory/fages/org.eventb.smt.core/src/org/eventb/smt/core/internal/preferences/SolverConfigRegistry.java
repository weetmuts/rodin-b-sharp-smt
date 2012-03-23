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
import static org.eventb.smt.core.preferences.ExtensionLoadingException.makeIllegalExtensionException;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.ExtensionLoadingException;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SolverConfigRegistry extends AbstractRegistry<ISolverConfig> {
	public static String SOLVER_CONFIGS_ID = SMTCore.PLUGIN_ID
			+ ".configurations";

	private HashMap<String, ISolverConfig> registry;

	private static final SolverConfigRegistry INSTANCE = new SolverConfigRegistry();

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
	public HashMap<String, ISolverConfig> getRegistry() {
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
		registry = new HashMap<String, ISolverConfig>();
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint point = xRegistry
				.getExtensionPoint(SOLVER_CONFIGS_ID);
		checkPoint(point, SOLVER_CONFIGS_ID);
		for (IConfigurationElement element : point.getConfigurationElements()) {
			final SolverConfigLoader solverConfigLoader = new SolverConfigLoader(
					element);
			final SolverConfiguration config = solverConfigLoader.load();
			final String configId = config.getID();
			final ISolverConfig oldConfig = registry.put(configId, config);
			if (oldConfig != null) {
				registry.put(configId, oldConfig);
				// FIXME must not throw an exception, but log the error silently
				throw makeIllegalExtensionException(configId);
			} else {
				if (DEBUG_DETAILS)
					System.out
							.println("Registered solver configuration extension "
									+ configId);
			}
		}
	}
}
