/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.tests;

import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.ALT_ERGO;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.CVC3;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.VERIT;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.Z3;
import static org.eventb.smt.internal.translation.SMTLIBVersion.V1_2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.smt.internal.preferences.SMTPreferences;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.internal.core.SMTSolver;
import org.eventb.smt.internal.translation.SMTLIBVersion;
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
			final SMTSolver solver, final String solverArgs,
			final SMTLIBVersion smtlibVersion) {
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

		final List<SMTSolverConfiguration> solvers = new ArrayList<SMTSolverConfiguration>();
		solvers.add(new SMTSolverConfiguration(solverBinaryName, solver,
				solverPath, solverArgs, smtlibVersion));
		final String preferences = SMTSolverConfiguration.toString(solvers);
		store.setValue(TRANSLATION_PATH_ID, "");
		store.setValue(SOLVER_PREFERENCES_ID, preferences);
		store.setValue(SOLVER_INDEX_ID, 0);
		store.setValue(VERIT_PATH_ID, BIN_PATH + VERIT);
	}

	protected static void setPreferencesForVeriTTest() {
		setSolverPreferences(VERIT.toString(), VERIT, "", V1_2);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences(CVC3.toString(), CVC3, "-lang smt", V1_2);

	}

	protected void setPreferencesForZ3Test() {
		String solver = Z3.toString();
		if (System.getProperty("os.name").startsWith("Windows")) {
			solver = "bin" + System.getProperty("file.separator") + Z3
					+ System.getProperty("file.separator") + "bin"
					+ System.getProperty("file.separator") + Z3;
		}

		setSolverPreferences(solver, Z3, "", V1_2);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(ALT_ERGO.toString(), ALT_ERGO, "", V1_2);
	}

	@Test
	public void testRecoverPreferences1() {
		setPreferencesForAltErgoTest();

		/**
		 * Get back preferences from UI
		 */
		final String expectedId = ALT_ERGO.toString();
		final SMTSolver expectedSolver = ALT_ERGO;
		String expectedSolverPath = BIN_PATH + expectedId;
		if (System.getProperty("os.name").startsWith("Windows")) {
			expectedSolverPath += ".exe";
		}
		final String args = "";
		final SMTLIBVersion smtlibVersion = V1_2;
		final String expectedVeriTPath = BIN_PATH + VERIT;

		final SMTSolverConfiguration expectedSolverConfig = new SMTSolverConfiguration(
				expectedId, expectedSolver, expectedSolverPath, args,
				smtlibVersion);
		final String expectedTranslationPath = DEFAULT_TRANSLATION_PATH;

		final SMTSolverConfiguration solverConfig = SMTPreferences
				.getSolverConfiguration();
		final String translationPath = SMTPreferences.getTranslationPath();
		final String veritPath = SMTPreferences.getVeriTPath();

		Assert.assertEquals(expectedSolverConfig, solverConfig);
		Assert.assertEquals(expectedTranslationPath, translationPath);
		Assert.assertEquals(expectedVeriTPath, veritPath);
	}
}
