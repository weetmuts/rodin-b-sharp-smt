/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.tests;

import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_SOLVER_PREFERENCES;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.preferences.SMTPreferences.DEFAULT_VERIT_PATH;
import static org.eventb.smt.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.provers.internal.core.SMTSolver.Z3;
import static org.eventb.smt.provers.ui.SmtProversUIPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.smt.preferences.SMTPreferences;
import org.eventb.smt.preferences.SolverDetails;
import org.eventb.smt.provers.ui.SmtProversUIPlugin;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author vitor
 * 
 */
public class UiPreferencesTests {
	/**
	 * In linux: '/home/username/bin/'
	 */
	protected static final String BIN_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "bin"
			+ System.getProperty("file.separator");

	protected static void setSolverPreferences(final String solverBinaryName,
			final String solverArgs, final boolean isSMTV1_2Compatible,
			final boolean isSMTV2_0Compatible) {
		final String OS = System.getProperty("os.name");
		final IPreferenceStore store = SmtProversUIPlugin
				.getDefaultPreferenceStore();
		final String solverPath;

		if (OS.startsWith("Windows")) {
			solverPath = BIN_PATH + solverBinaryName + ".exe";
		} else {
			solverPath = BIN_PATH + solverBinaryName;
		}

		System.out.println(solverPath);

		final List<SolverDetails> solvers = new ArrayList<SolverDetails>();
		solvers.add(new SolverDetails(solverBinaryName, solverPath, solverArgs,
				isSMTV1_2Compatible, isSMTV2_0Compatible));
		final String preferences = SolverDetails.toString(solvers);
		store.setValue(TRANSLATION_PATH_ID, "");
		store.setValue(SOLVER_PREFERENCES_ID, preferences);
		store.setValue(SOLVER_INDEX_ID, 0);
		store.setValue(VERIT_PATH_ID, BIN_PATH + VERIT);
	}

	protected static void setPreferencesForVeriTTest() {
		setSolverPreferences(VERIT.toString(), "", true, false);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences(CVC3.toString(), "-lang smt", true, false);

	}

	protected void setPreferencesForZ3Test() {
		String solver = Z3.toString();
		if (System.getProperty("os.name").startsWith("Windows")) {
			solver = "bin" + System.getProperty("file.separator") + Z3
					+ System.getProperty("file.separator") + "bin"
					+ System.getProperty("file.separator") + Z3;
		}

		setSolverPreferences(solver, "", true, false);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(ALT_ERGO.toString(), "", true, false);
	}

	@Test
	public void testRecoverPreferences1() {
		setPreferencesForAltErgoTest();

		final IPreferencesService preferencesService = Platform
				.getPreferencesService();

		/**
		 * Get back preferences from UI
		 */
		final String translationPath = preferencesService.getString(PLUGIN_ID,
				TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH, null);
		final String solverPreferencesString = preferencesService.getString(
				PLUGIN_ID, SOLVER_PREFERENCES_ID, DEFAULT_SOLVER_PREFERENCES,
				null);
		final int solverIndex = preferencesService.getInt(PLUGIN_ID,
				SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX, null);
		final String veriTPath = preferencesService.getString(PLUGIN_ID,
				VERIT_PATH_ID, DEFAULT_VERIT_PATH, null);
		final SMTPreferences smtPreferences = new SMTPreferences(
				translationPath, solverPreferencesString, solverIndex,
				veriTPath);

		final String expectedId = ALT_ERGO.toString();
		String expectedSolverPath = BIN_PATH + expectedId;
		if (System.getProperty("os.name").startsWith("Windows")) {
			expectedSolverPath += ".exe";
		}
		final String args = "";
		final boolean smtV1_2 = true;
		final boolean smtV2_0 = false;
		final String expectedVeriTPath = BIN_PATH + VERIT;

		final SolverDetails expectedSolverDetail = new SolverDetails(
				expectedId, expectedSolverPath, args, smtV1_2, smtV2_0);
		final SMTPreferences expectedSMTPreferences = new SMTPreferences(
				DEFAULT_TRANSLATION_PATH, expectedSolverDetail,
				expectedVeriTPath);

		final SMTPreferences[] p = { smtPreferences };
		final SMTPreferences[] expP = { expectedSMTPreferences };

		Assert.assertArrayEquals(expP, p);

	}
}
