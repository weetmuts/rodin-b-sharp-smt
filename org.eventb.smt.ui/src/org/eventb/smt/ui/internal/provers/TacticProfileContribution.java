/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.preferences.autotactics.TacticPreferenceFactory;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.smt.core.SMTCore;

/**
 * Contributes a new tactic profile which is similar to the
 * "Default Auto Tactic Profile", but with the enabled SMT solvers added just
 * before the partition rewrite tactic.
 * 
 * TODO contribute the profile through an extension when available
 */
public class TacticProfileContribution {

	/**
	 * Name of the profile that we contribute.
	 */
	private static final String PROFILE_NAME = "Default Auto Tactic with SMT (do not edit)";

	public TacticProfileContribution() {
		// Do nothing
	}

	/**
	 * Contributes the new tactic profile.
	 */
	public void contribute() {
		final ITacticProfileCache profiles = loadProfileCache();
		final ITacticDescriptor smtAutoDesc = SMTCore.getDefaultAutoWithSMT();
		addContribution(profiles, smtAutoDesc);
		profiles.store();
	}

	/**
	 * Loads the list of tactics profiles from the Event-B UI plug-in.
	 */
	private ITacticProfileCache loadProfileCache() {
		final IEclipsePreferences corePref = InstanceScope.INSTANCE
				.getNode(EventBPlugin.PLUGIN_ID);
		final ITacticProfileCache profiles = TacticPreferenceFactory.makeTacticProfileCache(corePref);
		profiles.load();
		return profiles;
	}

	/**
	 * Adds our contribution to the list of profiles.
	 */
	private void addContribution(ITacticProfileCache profiles,
			ITacticDescriptor smtAutoDesc) {
		if (profiles.exists(PROFILE_NAME)) {
			profiles.remove(PROFILE_NAME);
		}
		profiles.add(PROFILE_NAME, smtAutoDesc);
	}

}
