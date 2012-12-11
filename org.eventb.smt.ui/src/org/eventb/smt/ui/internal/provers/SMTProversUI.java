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
import static org.eventb.internal.ui.EventBImage.registerImage;
import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.getDefaultAutoTactics;
import static org.eventb.smt.core.SMTCore.ALL_SMT_SOLVERS_PROFILE_ID;
import static org.eventb.smt.core.SMTCore.AUTO_TACTIC_SMT_PROFILE_ID;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.smt.core.internal.provers.SMTProversCore;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class SMTProversUI extends AbstractUIPlugin {
	/**
	 * the shared instance
	 */
	private static SMTProversUI plugin;
	private static final IPath ENABLE_IMAGE_PATH = new Path(
			"icons/enable_configuration.gif");
	private static final IPath DISABLE_IMAGE_PATH = new Path(
			"icons/disable_configuration.gif");

	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.ui";
	public static final String ENABLE_CONFIG_IMG_ID = "enable_configuration";
	public static final String DISABLE_CONFIG_IMG_ID = "disable_configuration";

	/**
	 * The constructor
	 */
	public SMTProversUI() {
		// Do nothing
	}

	private void registerImages() {
		final ImageRegistry imageRegistry = getImageRegistry();
		registerImage(imageRegistry, ENABLE_CONFIG_IMG_ID, PLUGIN_ID,
				ENABLE_IMAGE_PATH.toOSString());
		registerImage(imageRegistry, DISABLE_CONFIG_IMG_ID, PLUGIN_ID,
				DISABLE_IMAGE_PATH.toOSString());
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

		final ITacticDescriptor allSMTSolversTactic = SMTProversCore
				.getDefault().getAllSMTSolversTactic();

		if (allSMTSolversTactic != null) {
			if (profiles.exists(ALL_SMT_SOLVERS_PROFILE_ID)) {
				profiles.remove(ALL_SMT_SOLVERS_PROFILE_ID);
			}
			profiles.add(ALL_SMT_SOLVERS_PROFILE_ID, allSMTSolversTactic);
			profiles.store();
		}

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
		 * Adds the SMT tactic right after the BoundedGoalWithFiniteHypotheses
		 * tactic. We used this position for performance tests.
		 */
		final int afterBoundedGoalWithFiniteHyps = 5;

		if (!profiles.exists(ALL_SMT_SOLVERS_PROFILE_ID)) {
			smtError("All SMT Solvers Profile not found.", null);
			return;
		}

		final ITacticDescriptor allSMTTacticDescriptor = profiles.getEntry(
				ALL_SMT_SOLVERS_PROFILE_ID).getReference();

		if (allSMTTacticDescriptor != null) {
			combinedTactics.add(afterBoundedGoalWithFiniteHyps,
					allSMTTacticDescriptor);
			final ICombinatorDescriptor combinator = getAutoTacticRegistry()
					.getCombinatorDescriptor(combinatorId);
			final ICombinedTacticDescriptor defaultAutoWithSMT = combinator
					.combine(combinedTactics, "SMTTactic");

			/**
			 * If the SMT profile does not exist yet, stores it in the
			 * preferences.
			 */
			if (profiles.exists(AUTO_TACTIC_SMT_PROFILE_ID)) {
				profiles.remove(AUTO_TACTIC_SMT_PROFILE_ID);
			}
			profiles.add(AUTO_TACTIC_SMT_PROFILE_ID, defaultAutoWithSMT);
			profiles.store();
		}
	}

	public static void updateAllSMTSolversProfile() {
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

		final ITacticDescriptor allSMTSolversTactic = SMTProversCore
				.getDefault().getAllSMTSolversTactic();

		if (allSMTSolversTactic != null) {
			if (profiles.exists(ALL_SMT_SOLVERS_PROFILE_ID)) {
				profiles.remove(ALL_SMT_SOLVERS_PROFILE_ID);
			}
			profiles.add(ALL_SMT_SOLVERS_PROFILE_ID, allSMTSolversTactic);
			profiles.store();
		}
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		addSMTProfile();
		registerImages();
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
	public static SMTProversUI getDefault() {
		return plugin;
	}
}
