/**
 * 
 */
package fr.systerel.smt.provers.ui.tests;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_TRANSLATIONPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERITPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.TRANSLATIONPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERITPATH;
import static fr.systerel.smt.provers.internal.core.SMTSolver.ALT_ERGO;
import static fr.systerel.smt.provers.internal.core.SMTSolver.CVC3;
import static fr.systerel.smt.provers.internal.core.SMTSolver.VERIT;
import static fr.systerel.smt.provers.internal.core.SMTSolver.Z3;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Test;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetail;
import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

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

		final List<SolverDetail> solvers = new ArrayList<SolverDetail>();
		solvers.add(new SolverDetail(solverBinaryName, solverPath, solverArgs,
				isSMTV1_2Compatible, isSMTV2_0Compatible));
		final String preferences = SolverDetail.toString(solvers);
		store.setValue(TRANSLATIONPATH, null);
		store.setValue(SOLVERPREFERENCES, preferences);
		store.setValue(SOLVERINDEX, 0);
		store.setValue(VERITPATH, BIN_PATH + VERIT);
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
		final String translationPath = preferencesService.getString(
				PREFERENCES_ID, TRANSLATIONPATH, DEFAULT_TRANSLATIONPATH, null);
		final String solverPreferencesString = preferencesService.getString(
				PREFERENCES_ID, SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES,
				null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVERINDEX, DEFAULT_SOLVERINDEX, null);
		final String veriTPath = preferencesService.getString(PREFERENCES_ID,
				VERITPATH, DEFAULT_VERITPATH, null);
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

		final SolverDetail expectedSolverDetail = new SolverDetail(expectedId,
				expectedSolverPath, args, smtV1_2, smtV2_0);
		final SMTPreferences expectedSMTPreferences = new SMTPreferences(
				expectedSolverDetail, expectedVeriTPath);

		final SMTPreferences[] p = { smtPreferences };
		final SMTPreferences[] expP = { expectedSMTPreferences };

		Assert.assertArrayEquals(expP, p);

	}
}
