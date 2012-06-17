/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.preferences.solvers;

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;
import static org.eventb.smt.core.internal.preferences.AbstractLoader.error;
import static org.eventb.smt.core.internal.preferences.Messages.BundledSolverRegistry_RegistrationError;
import static org.eventb.smt.core.internal.preferences.Messages.BundledSolverRegistry_SuccessfullRegistration;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG_DETAILS;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.internal.preferences.AbstractRegistry;
import org.eventb.smt.core.preferences.ISMTSolver;

/**
 * @author Systerel (yguyot)
 * 
 */
public class BundledSolverRegistry extends AbstractRegistry<ISMTSolver> {
	public static String BUNDLED_SOLVERS_ID = SMTCore.PLUGIN_ID + ".solvers"; //$NON-NLS-1$

	private HashMap<String, ISMTSolver> registry;

	private static final BundledSolverRegistry INSTANCE = new BundledSolverRegistry();

	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private BundledSolverRegistry() {
		// Singleton implementation
	}

	public static BundledSolverRegistry getBundledSolverRegistry() {
		INSTANCE.loadRegistry();
		return INSTANCE;
	}

	@Override
	public HashMap<String, ISMTSolver> getRegistry() {
		return registry;
	}

	/**
	 * Initializes the registry using extensions to the bundled solver extension
	 * point.
	 */
	@Override
	protected synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, ISMTSolver>();
		try {
			final IExtensionRegistry xRegistry = getExtensionRegistry();
			checkRegistry(xRegistry);
			final IExtensionPoint point = xRegistry
					.getExtensionPoint(BUNDLED_SOLVERS_ID);
			checkPoint(point, BUNDLED_SOLVERS_ID);
			for (IConfigurationElement element : point
					.getConfigurationElements()) {
				final BundledSolverLoader bundledSolverLoader = new BundledSolverLoader(
						element);
				try {
					final SMTSolver solver = bundledSolverLoader.load();
					final String solverId = solver.getID();
					final ISMTSolver oldSolver = registry.put(solverId, solver);
					if (oldSolver != null) {
						registry.put(solverId, oldSolver);
						smtError(BundledSolverRegistry_RegistrationError,
								error("Duplicated extension " + solverId + " ignored."));
					} else {
						if (DEBUG_DETAILS)
							System.out
									.println(BundledSolverRegistry_SuccessfullRegistration
											+ solverId);
					}
				} catch (Exception e) {
					smtError(BundledSolverRegistry_RegistrationError, e);
				}
			}
		} catch (Exception e) {
			smtError(BundledSolverRegistry_RegistrationError, e);
		}
	}
}
