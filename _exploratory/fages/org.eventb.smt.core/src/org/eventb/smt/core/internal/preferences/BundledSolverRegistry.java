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
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeIllegalExtensionException;
import static org.eventb.smt.core.preferences.BundledSolverLoadingException.makeNoBundledSolversPointException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.BundledSolverLoadingException;
import org.eventb.smt.core.preferences.IBundledSolverRegistry;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverRegistry implements IBundledSolverRegistry {
	public static String BUNDLED_SOLVERS_ID = SMTCore.PLUGIN_ID
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
			throws InvalidRegistryObjectException,
			BundledSolverLoadingException {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	public SMTSolverConfiguration getBundledSolverInstance(String id) {
		return registry.get(id).getInstance();
	}

	public synchronized boolean isRegistered(String id)
			throws BundledSolverLoadingException,
			InvalidRegistryObjectException {
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(id);
	}

	/**
	 * Initializes the registry using extensions to the bundled solver extension
	 * point.
	 * 
	 * @throws BundledSolverLoadingException
	 */
	private synchronized void loadRegistry()
			throws BundledSolverLoadingException,
			InvalidRegistryObjectException {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, BundledSolverDesc>();
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint point = xRegistry
				.getExtensionPoint(BUNDLED_SOLVERS_ID);
		checkPoint(point);
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

	/**
	 * @param point
	 * @throws BundledSolverLoadingException
	 */
	private static void checkPoint(final IExtensionPoint point)
			throws BundledSolverLoadingException {
		if (point == null)
			throw makeNoBundledSolversPointException();
	}

	@Override
	public List<SMTSolverConfiguration> getSolverConfigs()
			throws InvalidRegistryObjectException,
			BundledSolverLoadingException {
		final List<SMTSolverConfiguration> solverConfigs = new ArrayList<SMTSolverConfiguration>();
		if (registry == null) {
			loadRegistry();
		}
		for (final Map.Entry<String, BundledSolverDesc> entry : registry
				.entrySet()) {
			final SMTSolverConfiguration solverConfig = entry.getValue()
					.getInstance();
			if (solverConfig != null) {
				solverConfigs.add(solverConfig);
			}
		}
		return solverConfigs;
	}
}
