/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.provers;

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;
import static org.eventb.smt.core.SMTCore.ALL_SMT_SOLVERS_PROFILE_ID;
import static org.eventb.smt.core.SMTCore.AUTO_TACTIC_SMT_PROFILE_ID;
import static org.eventb.smt.core.SMTCore.updateAllSMTSolversTactic;
import static org.eventb.smt.core.internal.log.SMTStatus.smtError;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.preferences.tactics.TacticsProfilesCache;
import org.eventb.smt.core.internal.provers.SMTProversCore;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author Systerel (yguyot)
 * 
 */
@SuppressWarnings("restriction")
public class SMTHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		updateAllSMTSolversTactic();
		final IAutoTacticPreference autoTacticPreference = getAutoPostTacticManager()
				.getAutoTacticPreference();
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

			final ITacticDescriptor autoTacticSMTProfile = profiles.getEntry(
					AUTO_TACTIC_SMT_PROFILE_ID).getValue();
			if (autoTacticSMTProfile != null) {
				autoTacticPreference
						.setSelectedDescriptor(autoTacticSMTProfile);
			} else {
				smtError(
						"Could not set the Auto-Tactic SMT Profile: the profile could not be found.",
						null);
			}
		}

		// nothing to return
		return null;
	}
}
