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

import static org.eventb.core.seqprover.tactics.BasicTactics.failTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;

import org.eclipse.core.runtime.IPath;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.prefs.ConfigDescriptor;
import org.eventb.smt.core.internal.prefs.ConfigPreferences;
import org.eventb.smt.core.internal.prefs.SolverDescriptor;
import org.eventb.smt.core.internal.prefs.SolverPreferences;
import org.eventb.smt.core.internal.provers.ExternalSMT;
import org.eventb.smt.core.internal.provers.SMTConfiguration;
import org.eventb.smt.core.internal.provers.SMTInput;
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
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";

	/**
	 * Name of the preference that contains the path to a temporary directory
	 * for storing intermediate files.
	 */
	public static final String TRANSLATION_PATH_ID = "translationPath"; //$NON-NLS-1$

	/**
	 * Name of the preference that contains the path to a veriT binary which
	 * will be used to expand the macros produced by the veriT translation.
	 */
	public static final String VERIT_PATH_ID = "veriTPath"; //$NON-NLS-1$

	/**
	 * Configuration ID value used when all configurations should be applied
	 * sequentially.
	 */
	public static final String NO_SUCH_SOLVER_CONFIGURATION_ERROR = "No such SMT configuration";

	/**
	 * Returns a tactic that will apply the given SMT solver configuration with
	 * the given parameters.
	 *
	 * This tactic should be called by the parameterized auto tactic.
	 *
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            amount of time in milliseconds after which the solver will be
	 *            interrupted
	 * @param configId
	 *            the selected solver configuration id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay, final String configId) {
		final ISMTConfiguration config = getSMTConfiguration(configId);
		if (config == null) {
			return failTac(NO_SUCH_SOLVER_CONFIGURATION_ERROR);
		}
		final IReasoner smtReasoner = new ExternalSMT();
		final IReasonerInput smtInput = new SMTInput(restricted, timeOutDelay,
				config);
		return reasonerTac(smtReasoner, smtInput);
	}

	/**
	 * Returns an array of all bundled configurations. Bundled configurations
	 * are configurations that are contributed by plug-ins, rather than the
	 * end-user.
	 *
	 * @return an array of all bundled configurations
	 */
	public static IConfigDescriptor[] getBundledConfigs2() {
		return ConfigPreferences.getBundledConfigs();
	}

	/**
	 * Returns an array of all user-defined configurations.
	 *
	 * @return an array of all user-defined configurations
	 */
	public static IConfigDescriptor[] getUserConfigs2() {
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
		return new ConfigDescriptor(name, solverName, args, approach, version);
	}

	/**
	 * Sets the user-defined configurations. This replaces the list of
	 * user-defined configurations with the given ones.
	 */
	public static void setUserConfigs2(IConfigDescriptor[] configs) {
		ConfigPreferences.setUserConfigs(configs);
	}

	/**
	 * Returns an array of all bundled solvers. Bundled solvers are solvers that
	 * are contributed by plug-ins, rather than the end-user.
	 *
	 * @return an array of all bundled solvers
	 */
	public static ISolverDescriptor[] getBundledSolvers2() {
		return SolverPreferences.getBundledSolvers();
	}

	/**
	 * Returns an array of all user-defined solvers.
	 *
	 * @return an array of all user-defined solvers
	 */
	public static ISolverDescriptor[] getUserSolvers2() {
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
		return new SolverDescriptor(name, kind, path);
	}

	/**
	 * Sets the user-defined solvers. This replaces the list of user-defined
	 * solvers with the given ones.
	 */
	public static void setUserSolvers2(ISolverDescriptor[] newSolvers) {
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
		return newSMTConfiguration(config, solver);
	}

	// For testing purposes
	public static ISMTConfiguration newSMTConfiguration(
			final IConfigDescriptor config, final ISolverDescriptor solver) {
		return new SMTConfiguration(config, solver);
	}

}
