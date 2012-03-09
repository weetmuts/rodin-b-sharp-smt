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

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eventb.smt.internal.preferences.BundledSolverRegistry.BundledSolverLoadingException.makeNoBundledSolversXPointException;
import static org.eventb.smt.internal.preferences.Messages.BundledSolverLoadingException_DefaultMessage;
import static org.eventb.smt.internal.translation.Translator.DEBUG_DETAILS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.internal.provers.core.SMTProversCore;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverRegistry {

	public static class BundledSolverLoadingException extends Exception {
		/**
		 * Generated serial version ID.
		 */
		private static final long serialVersionUID = -2787953160141168010L;

		public BundledSolverLoadingException() {
			this(BundledSolverLoadingException_DefaultMessage);
		}

		public BundledSolverLoadingException(String message) {
			super(message);
		}

		public static final BundledSolverLoadingException makeDotInIDException(
				final String id) {
			final StringBuilder errBuilder = new StringBuilder();
			errBuilder.append("Invalid id: ").append(id);
			errBuilder.append(" (must not contain a dot).");
			return new BundledSolverLoadingException(errBuilder.toString());
		}

		public static final BundledSolverLoadingException makeWhitespaceOrColonInIDException(
				final String id) {
			final StringBuilder errBuilder = new StringBuilder();
			errBuilder.append("Invalid id: ").append(id);
			errBuilder.append(" (must not contain a whitespace or a colon).");
			return new BundledSolverLoadingException(errBuilder.toString());
		}

		public static final BundledSolverLoadingException makeNullIDException() {
			return new BundledSolverLoadingException(
					"Invalid id: null pointer.");
		}

		public static final BundledSolverLoadingException makeNullBinaryNameException() {
			return new BundledSolverLoadingException(
					"Invalid binary name: null pointer.");
		}

		public static final BundledSolverLoadingException makeNoSuchBundleException(
				final String bundleName) {
			final StringBuilder errBuilder = new StringBuilder();
			errBuilder.append("Invalid bundle name: ").append(bundleName);
			errBuilder.append(" (no such bundle was found installed).");
			return new BundledSolverLoadingException(errBuilder.toString());
		}

		public static final BundledSolverLoadingException makeNullPathException() {
			return new BundledSolverLoadingException(
					"Invalid path: null pointer.");
		}

		public static final BundledSolverLoadingException makeNoBundledSolversXPointException() {
			final StringBuilder errBuilder = new StringBuilder();
			errBuilder.append("Invalid extension point id: ").append(
					BUNDLED_SOLVERS_ID);
			errBuilder.append(" (no such extension point was found).");
			return new BundledSolverLoadingException(errBuilder.toString());
		}
	}

	public static final IllegalArgumentException makeIllegalBundledSolverXException(
			final String id) {
		final StringBuilder description = new StringBuilder();
		description.append("Duplicated bundled solver extension ");
		description.append(id);
		description.append(" ignored.");
		return new IllegalArgumentException(description.toString());
	}

	static String BUNDLED_SOLVERS_ID = SMTProversCore.PLUGIN_ID
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
			BundledSolverLoadingException, IllegalArgumentException {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	public SMTSolverConfiguration getBundledSolverInstance(String id) {
		return registry.get(id).getInstance();
	}

	public synchronized boolean isRegistered(String id)
			throws BundledSolverLoadingException,
			InvalidRegistryObjectException, IllegalArgumentException {
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
			InvalidRegistryObjectException, IllegalArgumentException {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, BundledSolverDesc>();
		final IExtensionRegistry xRegistry = getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry
				.getExtensionPoint(BUNDLED_SOLVERS_ID);
		checkXPoint(xPoint);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			final BundledSolverDesc desc = new BundledSolverDesc(element);
			desc.load();
			final String id = desc.getId();
			final BundledSolverDesc oldDesc = registry.put(id, desc);
			if (oldDesc != null) {
				registry.put(id, oldDesc);
				throw makeIllegalBundledSolverXException(id);
			} else {
				if (DEBUG_DETAILS)
					System.out.println("Registered bundled solver extension "
							+ id);
			}
		}
	}

	/**
	 * @param xPoint
	 * @throws BundledSolverLoadingException
	 */
	private static void checkXPoint(final IExtensionPoint xPoint)
			throws BundledSolverLoadingException {
		if (xPoint == null)
			throw makeNoBundledSolversXPointException();
	}

	public List<SMTSolverConfiguration> getSolverConfigs()
			throws InvalidRegistryObjectException, IllegalArgumentException,
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
