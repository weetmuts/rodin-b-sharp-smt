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

import static org.eventb.smt.core.internal.provers.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.core.internal.provers.SMTProversCore.getDefault;
import static org.eventb.smt.core.preferences.PreferenceManager.getBundledSolverRegistry;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.PreferenceManager.getSolverConfigRegistry;
import static org.eventb.smt.core.preferences.SMTSolverFactory.newSolver;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.provers.SMTProversCore;
import org.eventb.smt.core.preferences.IRegistry;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISMTSolversPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;
import org.eventb.smt.core.preferences.PreferenceManager;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCore {

	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";

	public static ITactic allSMTSolversTactic() {
		return getDefault().getAllSMTSolversTactic().getTacticInstance();
	}

	public static void updateAllSMTSolversTactic() {
		getDefault().updateAllSMTSolversTactic();
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param timeOutDelay
	 *            amount of time in milliseconds after which the solver will be
	 *            interrupted
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay, final String configId) {
		return SMTProversCore.externalSMT(restricted, timeOutDelay, configId);
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent).
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMT(final boolean restricted,
			final long timeOutDelay) {
		return externalSMT(restricted, timeOutDelay, ALL_SOLVER_CONFIGURATIONS);
	}

	/**
	 * Returns an array of all bundled configurations. Bundled configurations
	 * are configurations that are contributed by plug-ins, rather than the
	 * end-user.
	 *
	 * @return an array of all bundled configurations
	 */
	public static ISolverConfig[] getBundledConfigs() {
		final IRegistry<ISolverConfig> registry = getSolverConfigRegistry();
		final Set<String> ids = registry.getIDs();
		final ISolverConfig[] result = new ISolverConfig[ids.size()];
		int count = 0;
		for (String id : ids) {
			result[count++] = registry.get(id);
		}
		return result;
	}

	/**
	 * Returns an array of all user-defined configurations.
	 *
	 * @return an array of all user-defined configurations
	 */
	public static ISolverConfig[] getUserConfigs() {
		final PreferenceManager prefMng = getPreferenceManager();
		final ISolverConfigsPreferences prefs = prefMng.getSolverConfigsPrefs();
		final Collection<ISolverConfig> configs = prefs.getSolverConfigs().values();
		final List<ISolverConfig> list = new ArrayList<ISolverConfig>(configs.size());
		for (ISolverConfig config : configs) {
			if (config.isEditable()) {
				list.add(config);
			}
		}
		return list.toArray(new ISolverConfig[list.size()]);
	}

	/**
	 * Sets the user-defined configurations. This replaces the list of
	 * user-defined configurations with the given ones.
	 */
	public static void setUserConfigs(ISolverConfig[] configs) {
		final PreferenceManager prefMng = getPreferenceManager();
		final ISolverConfigsPreferences prefs = prefMng.getSolverConfigsPrefs();
		prefs.loadDefault();
		for (ISolverConfig config : configs) {
			if (config.getID().length() != 0) {
				prefs.add(config);
			}
		}
		for (ISolverConfig config : configs) {
			if (config.getID().length() == 0) {
				prefs.add(computeId(prefs, config));
			}
		}
		prefs.save();
	}

	private static ISolverConfig computeId(ISolverConfigsPreferences prefs,
			ISolverConfig config) {
		final String id = prefs.freshID();
		final String name = config.getName();
		final String solverId = config.getSolverId();
		final String args = config.getArgs();
		final TranslationApproach approach = config.getTranslationApproach();
		final SMTLIBVersion version = config.getSmtlibVersion();
		return newConfig(id, name, solverId, args, approach, version);
	}

	/**
	 * Returns an array of all bundled solvers. Bundled solvers are solvers that
	 * are contributed by plug-ins, rather than the end-user.
	 *
	 * @return an array of all bundled solvers
	 */
	public static ISMTSolver[] getBundledSolvers() {
		final IRegistry<ISMTSolver> registry = getBundledSolverRegistry();
		final Set<String> ids = registry.getIDs();
		final ISMTSolver[] result = new ISMTSolver[ids.size()];
		int count = 0;
		for (String id : ids) {
			result[count++] = registry.get(id);
		}
		return result;
	}

	/**
	 * Returns an array of all user-defined solvers.
	 *
	 * @return an array of all user-defined solvers
	 */
	public static ISMTSolver[] getUserSolvers() {
		final PreferenceManager prefMng = getPreferenceManager();
		final ISMTSolversPreferences prefs = prefMng.getSMTSolversPrefs();
		final Collection<ISMTSolver> solvers = prefs.getSolvers().values();
		final List<ISMTSolver> list = new ArrayList<ISMTSolver>(solvers.size());
		for (ISMTSolver solver : solvers) {
			if (solver.isEditable()) {
				list.add(solver);
			}
		}
		return list.toArray(new ISMTSolver[list.size()]);
	}

	/**
	 * Sets the user-defined solvers. This replaces the list of user-defined
	 * solvers with the given ones.
	 */
	public static void setUserSolvers(ISMTSolver[] solvers) {
		final PreferenceManager prefMng = getPreferenceManager();
		final ISMTSolversPreferences prefs = prefMng.getSMTSolversPrefs();
		prefs.loadDefault();
		for (ISMTSolver solver : solvers) {
			if (solver.getID().length() != 0) {
				prefs.add(solver);
			}
		}
		for (ISMTSolver solver : solvers) {
			if (solver.getID().length() == 0) {
				prefs.add(computeId(prefs, solver));
			}
		}
		prefs.save();
	}

	private static ISMTSolver computeId(ISMTSolversPreferences prefs,
			ISMTSolver solver) {
		final String id = prefs.freshID();
		final String name = solver.getName();
		final SolverKind kind = solver.getKind();
		final IPath path = solver.getPath();
		return newSolver(id, name, kind, path);
	}

}
