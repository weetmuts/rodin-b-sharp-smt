/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_SOLVER_PREFERENCES;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_VERIT_PATH;
import static org.eventb.smt.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.provers.ui.SmtProversUIPlugin.PLUGIN_ID;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.preferences.SMTPreferences;
import org.eventb.smt.provers.core.SMTProversCore;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * @author guyot
 * 
 */
public class SMTVeriT extends DefaultTacticProvider implements ITacticProvider {

	public class SMTVeriTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			try {
				final IPreferencesService preferencesService = Platform
						.getPreferencesService();
				final String solverPreferences = preferencesService.getString(
						PLUGIN_ID, SOLVER_PREFERENCES_ID,
						DEFAULT_SOLVER_PREFERENCES, null);
				final int solverIndex = preferencesService.getInt(PLUGIN_ID,
						SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX, null);
				final String translationPath = preferencesService.getString(
						PLUGIN_ID, TRANSLATION_PATH_ID,
						DEFAULT_TRANSLATION_PATH, null);
				final String veriTPath = preferencesService.getString(
						PLUGIN_ID, VERIT_PATH_ID, DEFAULT_VERIT_PATH, null);
				final SMTPreferences smtPreferences = new SMTPreferences(
						translationPath, solverPreferences, solverIndex,
						veriTPath);

				return SMTProversCore.externalSMTThroughVeriT(smtPreferences
						.getSolver().getSmtlibVersion(), null, smtPreferences
						.getSolver().getId(), smtPreferences.getSolver()
						.getPath(), smtPreferences.getSolver().getArgs(),
						smtPreferences.getTranslationPath(), smtPreferences
								.getVeriTPath(), true);

			} catch (final PatternSyntaxException pse) {
				pse.printStackTrace(System.err);
				return SMTProversCore.smtSolverError();
			} catch (final IllegalArgumentException iae) {
				if (iae.equals(SMTPreferences.NoSMTSolverSelectedException)) {
					return SMTProversCore.noSMTSolverSelected();
				} else if (iae.equals(SMTPreferences.NoSMTSolverSetException)) {
					return SMTProversCore.noSMTSolverSet();
				} else if (iae.equals(SMTPreferences.VeriTPathNotSetException)) {
					return SMTProversCore.veriTPathNotSet();
				} else {
					return SMTProversCore.smtSolverError();
				}
			}
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			final IProofTreeNode node, final Predicate hyp,
			final String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTVeriTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
