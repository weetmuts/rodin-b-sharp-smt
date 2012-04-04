/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.provers;

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.getDefaultAutoTactics;
import static org.eventb.smt.core.internal.provers.AutoTactics.makeSMTPPTactic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class SmtProversUIPlugin extends AbstractUIPlugin {
	private static final String AUTO_TACTIC_SMTPP_PROFILE_ID = "Default Auto Tactic with SMT Profile";

	/**
	 * the shared instance
	 */
	private static SmtProversUIPlugin plugin;

	/**
	 * The constructor
	 */
	public SmtProversUIPlugin() {
		// Do nothing
	}

	/**
	 * Adds a new tactics profile in the Rodin platform preferences which is the
	 * default auto tactics profile plus the SMT tactic inserted right after the
	 * BoundedGoalWithFiniteHyps tactic.
	 * 
	 */
	private static void addSMTProfile() {
		/**
		 * Accesses the Event-B UI preferences
		 */
		final IPreferenceStore eventbUIPrefStore = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		/**
		 * Loads the list of tactics profiles
		 */
		final TacticsProfilesCache profiles = new TacticsProfilesCache(
				eventbUIPrefStore);
		profiles.load();
		/**
		 * Gets the default auto tactics profile
		 */
		final String defaultAutoTactics = getDefaultAutoTactics();
		final IPrefMapEntry<ITacticDescriptor> defaultAuto = profiles
				.getEntry(defaultAutoTactics);
		final ITacticDescriptor defaultAutoTac = defaultAuto.getValue();
		final ICombinedTacticDescriptor defaultAutoDesc = (ICombinedTacticDescriptor) defaultAutoTac;
		final String combinatorId = defaultAutoDesc.getCombinatorId();
		/**
		 * Makes a copy of the unmodifiable list.
		 */
		final List<ITacticDescriptor> combinedTactics = new ArrayList<ITacticDescriptor>(
				defaultAutoDesc.getCombinedTactics());
		/**
		 * Adds the SMT PP tactic right after the
		 * BoundedGoalWithFiniteHypotheses tactic. We used this position for
		 * performance tests.
		 */
		final int afterBoundedGoalWithFiniteHyps = 5;
		combinedTactics.add(afterBoundedGoalWithFiniteHyps, makeSMTPPTactic());
		final ICombinatorDescriptor combinator = getAutoTacticRegistry()
				.getCombinatorDescriptor(combinatorId);
		final ICombinedTacticDescriptor defaultAutoWithSMTPP = combinator
				.combine(combinedTactics, "SMTTactic");
		/**
		 * If the SMT profile does not exist yet, stores it in the preferences.
		 */
		if (profiles.getEntry(AUTO_TACTIC_SMTPP_PROFILE_ID) == null) {
			profiles.add(AUTO_TACTIC_SMTPP_PROFILE_ID, defaultAutoWithSMTPP);
			profiles.store();
		}
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		addSMTProfile();
	}

	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		final IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SmtProversUIPlugin getDefault() {
		return plugin;
	}
}
