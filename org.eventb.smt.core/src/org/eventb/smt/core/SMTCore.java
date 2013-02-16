/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core;

import org.eclipse.core.runtime.IPath;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.smt.core.internal.prefs.ConfigDescriptor;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;
import org.eventb.smt.core.internal.prefs.SolverDescriptor;
import org.eventb.smt.core.internal.prefs.SolverPreferences;
import org.eventb.smt.core.internal.provers.SMTConfiguration;
import org.eventb.smt.core.internal.tactics.SMTTacticDescriptors;
import org.eventb.smt.core.prefs.IConfigDescriptor;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.eventb.smt.core.provers.ISMTConfiguration;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

/**
 * Facade class providing the interface to the SMT core plug-in functionality.
 *
 * @author Systerel (yguyot)
 */
public class SMTCore {

	/**
	 * Returns a tactic descriptor for running the given SMT solver
	 * configuration. This method does not verify that the configuration has
	 * been registered. The tactic is configured with the default values for the
	 * time out and restricted parameters.
	 * 
	 * @param configName
	 *            the name of an SMT configuration
	 * @return a tactic descriptor for running the given configuration
	 */
	public static ITacticDescriptor getTacticDescriptor(String configName) {
		return SMTTacticDescriptors.getTacticDescriptor(configName);
	}

	/**
	 * Returns an array of all bundled configurations. Bundled configurations
	 * are configurations that are contributed by plug-ins, rather than the
	 * end-user.
	 *
	 * @return an array of all bundled configurations
	 */
	public static IConfigDescriptor[] getBundledConfigs() {
		return ConfigPreferences.getBundledConfigs();
	}

	/**
	 * Returns an array of all user-defined configurations.
	 *
	 * @return an array of all user-defined configurations
	 */
	public static IConfigDescriptor[] getUserConfigs() {
		return ConfigPreferences.getUserConfigs();
	}

	/**
	 * Creates a new configuration descriptor from the given parameters.
	 *
	 * @param name
	 *            name of the configuration
	 * @param solverName
	 *            name of the solver
	 * @param args
	 *            arguments to pass to the solver
	 * @param approach
	 *            translation approach
	 * @param version
	 *            SMT-LIB version
	 * @return a configuration descriptor
	 */
	public static IConfigDescriptor newConfigDescriptor(String name,
			String solverName, String args, TranslationApproach approach,
			SMTLIBVersion version) {
		return new ConfigDescriptor(name, false, solverName, args, approach,
				version);
	}

	/**
	 * Sets the user-defined configurations. This replaces the list of
	 * user-defined configurations with the given ones.
	 */
	public static void setUserConfigs(IConfigDescriptor[] configs) {
		ConfigPreferences.setUserConfigs(configs);
	}

	/**
	 * Returns an array of all bundled solvers. Bundled solvers are solvers that
	 * are contributed by plug-ins, rather than the end-user.
	 *
	 * @return an array of all bundled solvers
	 */
	public static ISolverDescriptor[] getBundledSolvers() {
		return SolverPreferences.getBundledSolvers();
	}

	/**
	 * Returns an array of all user-defined solvers.
	 *
	 * @return an array of all user-defined solvers
	 */
	public static ISolverDescriptor[] getUserSolvers() {
		return SolverPreferences.getUserSolvers();
	}

	/**
	 * Creates a new solver descriptor from the given parameters.
	 *
	 * @param name
	 *            name of the solver
	 * @param kind
	 *            kind of the solver
	 * @param path
	 *            path to the solver binary
	 * @return a solver descriptor
	 */
	public static ISolverDescriptor newSolverDescriptor(String name,
			SolverKind kind, IPath path) {
		return new SolverDescriptor(name, false, kind, path);
	}

	/**
	 * Sets the user-defined solvers. This replaces the list of user-defined
	 * solvers with the given ones.
	 */
	public static void setUserSolvers(ISolverDescriptor[] newSolvers) {
		SolverPreferences.setUserSolvers(newSolvers);
	}

	/**
	 * Returns the SMT configuration with the given name, or <code>null</code>
	 * if unknown or if it references an unknown solver.
	 *
	 * @param name
	 *            configuration name
	 * @return the SMT configuration with the given name, or <code>null</code>
	 */
	public static ISMTConfiguration getSMTConfiguration(String name) {
		final IConfigDescriptor config = ConfigPreferences.get(name);
		if (config == null) {
			return null;
		}
		final String solverName = config.getSolverName();
		final ISolverDescriptor solver = SolverPreferences.get(solverName);
		if (solver == null) {
			return null;
		}
		return new SMTConfiguration(config, solver);
	}

}
