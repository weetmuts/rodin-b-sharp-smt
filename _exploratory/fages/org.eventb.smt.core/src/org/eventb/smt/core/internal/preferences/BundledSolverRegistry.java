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
public class BundledSolverRegistry extends AbstractRegistry<BundledSolverDesc> {
	public static String BUNDLED_SOLVERS_ID = SMTCore.PLUGIN_ID
			+ ".solvers";

	private Map<String, BundledSolverDesc> registry;

	private static final BundledSolverRegistry INSTANCE = new BundledSolverRegistry();

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private BundledSolverRegistry() {
		// Singleton implementation
	}

	public static BundledSolverRegistry getBundledSolverRegistry()
			throws InvalidRegistryObjectException, ExtensionLoadingException {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	@Override
	public Map<String, BundledSolverDesc> getRegistry() {
		return registry;
	}

	/**
	 * Initializes the registry using extensions to the bundled solver extension
	 * point.
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
		registry = new HashMap<String, BundledSolverDesc>();
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint point = xRegistry
				.getExtensionPoint(BUNDLED_SOLVERS_ID);
		checkPoint(point, BUNDLED_SOLVERS_ID);
		for (IConfigurationElement element : point.getConfigurationElements()) {
			final BundledSolverDesc desc = new BundledSolverDesc(element);
			desc.load();
			final String id = desc.getId();
			final BundledSolverDesc oldDesc = registry.put(id, desc);
			if (oldDesc != null) {
				registry.put(id, oldDesc);
				throw makeIllegalExtensionException(id);
			} else {
				if (DEBUG_DETAILS)
					System.out.println("Registered bundled solver extension "
							+ id);
			}
		}
	}
}
