/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.internal.preferences;

import static org.eventb.smt.internal.translation.Translator.DEBUG_DETAILS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.smt.internal.preferences.BundledSolverDesc.BundledSolverLoadingException;
import org.eventb.smt.internal.provers.core.SMTProversCore;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverRegistry {
	public static final IllegalArgumentException IllegalBundledSolverExtensionException(
			final String id) {
		final StringBuilder description = new StringBuilder();
		description.append("Duplicated bundled solver extension ");
		description.append(id);
		description.append(" ignored.");
		return new IllegalArgumentException(description.toString());
	}

	private static String BUNDLED_SOLVERS_ID = SMTProversCore.PLUGIN_ID
			+ ".bundledsolvers";

	private static final BundledSolverRegistry INSTANCE = new BundledSolverRegistry();

	private Map<String, BundledSolverDesc> registry;

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private BundledSolverRegistry() {
		// Singleton implementation
	}

	public static BundledSolverRegistry getBundledSolverRegistry()
			throws BundledSolverLoadingException {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	public SMTSolverConfiguration getBundledSolverInstance(String id) {
		return registry.get(id).getInstance();
	}

	public synchronized boolean isRegistered(String id)
			throws BundledSolverLoadingException {
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(id);
	}

	public synchronized String[] getRegisteredIDs()
			throws BundledSolverLoadingException {
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet().toArray(new String[] {});
	}

	/**
	 * Initializes the registry using extensions to the bundled solver extension
	 * point.
	 * 
	 * @throws BundledSolverLoadingException
	 */
	private synchronized void loadRegistry()
			throws BundledSolverLoadingException {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, BundledSolverDesc>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry
				.getExtensionPoint(BUNDLED_SOLVERS_ID);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			final BundledSolverDesc desc = new BundledSolverDesc(element);
			desc.load();
			final String id = desc.getId();
			final BundledSolverDesc oldDesc = registry.put(id, desc);
			if (oldDesc != null) {
				registry.put(id, oldDesc);
				throw IllegalBundledSolverExtensionException(id);
			} else {
				if (DEBUG_DETAILS)
					System.out.println("Registered bundled solver extension "
							+ id);
			}
		}
	}

	public List<SMTSolverConfiguration> getSolverConfigs()
			throws BundledSolverLoadingException {
		final List<SMTSolverConfiguration> solverConfigs = new ArrayList<SMTSolverConfiguration>();
		for (final Map.Entry<String, BundledSolverDesc> entry : registry
				.entrySet()) {
			solverConfigs.add(entry.getValue().getInstance());
		}
		return solverConfigs;
	}
}
