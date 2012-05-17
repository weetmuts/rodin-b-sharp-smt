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
import static org.eventb.smt.ui.internal.UIUtils.showError;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
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

	private static final String AUTO_TACTIC_SMT_PROFILE_ID = "Default Auto Tactic with SMT";
	private static final String ALL_SMT_SOLVERS_PROFILE_ID = "All SMT Solvers";

	/**
	 * the shared instance
	 */
	private static SMTProversUI plugin;

	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.ui";

	/*
	 * Internal name of images used by this plug-in.
	 */
	public static final String ENABLE_CONFIG_IMG_ID = "enable_configuration";
	public static final String DISABLE_CONFIG_IMG_ID = "disable_configuration";

	public static void updateAllSMTSolversProfile() {
		final IPreferenceStore eventbUIPrefStore = EventBUIPlugin.getDefault()
				.getPreferenceStore();
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

	/**
	 * The constructor
	 */
	public SMTProversUI() {
		// Do nothing
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

	// FIXME update doc.
	/**
	 * Adds a new tactics profile to the Event-B UI preferences which is the
	 * default auto tactics profile plus the SMT tactic inserted right after the
	 * BoundedGoalWithFiniteHyps tactic.
	 */
	private void addSMTProfile() {
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
			showError("All SMT Solvers Profile not found.");
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

	private void registerImages() {
		final ImageRegistry imageRegistry = getImageRegistry();
		registerImage(imageRegistry, ENABLE_CONFIG_IMG_ID, PLUGIN_ID,
				"icons/enable_configuration.gif");
		registerImage(imageRegistry, DISABLE_CONFIG_IMG_ID, PLUGIN_ID,
				"icons/disable_configuration.gif");
	}

	public static Image getRegisteredImage(String key) {
		final ImageRegistry imageRegistry = getDefault().getImageRegistry();
		return imageRegistry.get(key);
	}

}
