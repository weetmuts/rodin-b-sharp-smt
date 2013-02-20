/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.getDefaultAutoTactics;
import static org.eventb.smt.ui.internal.SMTProversUI.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.smt.core.SMTCore;
import org.eventb.ui.EventBUIPlugin;

/**
 * Contributes a new tactic profile which is similar to the
 * "Default Auto Tactic Profile", but with the enabled SMT solvers added just
 * before the partition rewrite tactic.
 * 
 * TODO contribute the profile through an extension when available
 */
@SuppressWarnings("restriction")
public class TacticProfileContribution {

	/**
	 * Name of the profile that we contribute.
	 */
	private static final String PROFILE_NAME = "Default Auto Tactic with SMT (do not edit)";

	/**
	 * Id for the tactic descriptor which constitutes the added profile.
	 */
	private static final String TACTIC_ID = PLUGIN_ID + ".default";

	/**
	 * Id of the tactic after which we want to insert the SMT solvers.
	 */
	private static final String BEFORE_TACTIC_ID = "org.eventb.core.seqprover.partitionRewriteTac";

	/**
	 * Id for the tactic descriptor which contains the SMT auto-tactic embedded
	 * in an attempt after lasso.
	 */
	private static final String LASSO_ID = PLUGIN_ID + ".lasso";

	/**
	 * Auto-tactic registry
	 */
	private static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	/**
	 * Tactic combinator for running a tactic within an attempt after lasso.
	 */
	private static final ICombinatorDescriptor ATTEMPT_AFTER_LASSO = REGISTRY
			.getCombinatorDescriptor("org.eventb.core.seqprover.attemptAfterLasso");

	public TacticProfileContribution() {
		// Do nothing
	}

	/**
	 * Contributes the new tactic profile.
	 */
	public void contribute() {
		final TacticsProfilesCache profiles = loadProfileCache();
		final ICombinedTacticDescriptor defaultAutoDesc = getDefaultAutoProfile(profiles);
		final ITacticDescriptor smtAutoDesc = makeSMTAutoTactic(defaultAutoDesc);
		addContribution(profiles, smtAutoDesc);
	}

	/**
	 * Loads the list of tactics profiles from the Event-B UI plug-in.
	 */
	private TacticsProfilesCache loadProfileCache() {
		final EventBUIPlugin ui = EventBUIPlugin.getDefault();
		final IPreferenceStore pStore = ui.getPreferenceStore();
		final TacticsProfilesCache profiles = new TacticsProfilesCache(pStore);
		profiles.load();
		return profiles;
	}

	/**
	 * Retrieves the "Default Auto Tactic Profile".
	 */
	private ICombinedTacticDescriptor getDefaultAutoProfile(
			TacticsProfilesCache profiles) {
		final String profileName = getDefaultAutoTactics();
		final ITacticDescriptor desc = profiles.getEntry(profileName)
				.getValue();
		return (ICombinedTacticDescriptor) desc;
	}

	/**
	 * Creates a new tactic descriptor from the given one, by inserting the SMT
	 * auto tactic just before the partition rewrite tactic.
	 */
	private ITacticDescriptor makeSMTAutoTactic(ICombinedTacticDescriptor base) {
		final String combinatorId = base.getCombinatorId();
		final List<ITacticDescriptor> baseTactics = base.getCombinedTactics();
		final List<ITacticDescriptor> newTactics = insertIntoList(baseTactics);
		final ICombinatorDescriptor combinator = REGISTRY
				.getCombinatorDescriptor(combinatorId);
		return combinator.combine(newTactics, TACTIC_ID);
	}

	private List<ITacticDescriptor> insertIntoList(
			List<ITacticDescriptor> baseTactics) {
		final List<ITacticDescriptor> newTactics;
		newTactics = new ArrayList<ITacticDescriptor>(baseTactics);
		final ITacticDescriptor partDesc = REGISTRY
				.getTacticDescriptor(BEFORE_TACTIC_ID);
		final int index = newTactics.indexOf(partDesc);
		newTactics.add(index, makeSMTAutoInLasso());
		return newTactics;
	}

	/**
	 * Returns a tactic descriptor for running the SMT auto-tactic embedded
	 * within an attempt after lasso.
	 */
	private ITacticDescriptor makeSMTAutoInLasso() {
		return ATTEMPT_AFTER_LASSO.combine(
				singletonList(SMTCore.smtAutoTactic), LASSO_ID);
	}

	/**
	 * Adds our contribution to the list of profiles and persist the list.
	 */
	private void addContribution(TacticsProfilesCache profiles,
			ITacticDescriptor smtAutoDesc) {
		if (profiles.exists(PROFILE_NAME)) {
			profiles.remove(PROFILE_NAME);
		}
		profiles.add(PROFILE_NAME, smtAutoDesc);
		profiles.store();
	}

}
