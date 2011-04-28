/**
 * 
 */
package fr.systerel.smt.provers.ui.tests;

import static br.ufrn.smt.solver.preferences.SMTPreferencesStore.CreatePreferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Test;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.SolverDetail;
import fr.systerel.smt.provers.core.SmtProversCore;
import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

/**
 * @author vitor
 * 
 */
public class UiPreferencesTests {

	public static final String PREPROPATH = "prepropath";

	public static final String USING_PREPRO = "usingprepro";

	public static final String SOLVER_INDEX = "solverindex";

	public static final String SOLVER_PREFERENCES = "solverpreferences";

	public static final String RODIN_SEQUENT = "rodin_sequent";

	public static final String REASONER_ID = SmtProversCore.PLUGIN_ID
			+ ".externalSMT";

	public static final String PREFERENCES_ID = "fr.systerel.smt.provers.ui";

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
		final String preferences = CreatePreferences(solvers);
		store.setValue("solverpreferences", preferences);
		store.setValue("solverindex", 0);
		store.setValue("usingprepro", true);
		store.setValue("prepropath", BIN_PATH + "verit");
	}

	protected static void setPreferencesForVeriTTest() {
		setSolverPreferences("verit", "", true, false);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences("cvc3", "-lang smt", true, false);

	}

	protected void setPreferencesForZ3Test() {
		String solver = "z3";
		if (System.getProperty("os.name").startsWith("Windows")) {
			solver = "bin" + System.getProperty("file.separator") + solver
					+ System.getProperty("file.separator") + "bin"
					+ System.getProperty("file.separator") + "z3";
		}

		setSolverPreferences(solver, "", true, false);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences("alt-ergo", "", true, false);
	}

	@Test
	public void testRecoverPreferences1() {
		setPreferencesForAltErgoTest();

		final IPreferencesService preferencesService = Platform
				.getPreferencesService();

		/**
		 * Get back preferences from UI
		 */
		final String solverPreferencesString = preferencesService.getString(
				PREFERENCES_ID, SOLVER_PREFERENCES, null, null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVER_INDEX, -1, null);
		final boolean usingPrePro = preferencesService.getBoolean(
				PREFERENCES_ID, USING_PREPRO, false, null);
		final String preProPath = preferencesService.getString(PREFERENCES_ID,
				PREPROPATH, null, null);
		final SMTPreferences smtPreferences = new SMTPreferences(
				solverPreferencesString, solverIndex, usingPrePro, preProPath);

		final String expectedId = "alt-ergo";
		String expectedSolverPath = BIN_PATH + expectedId;
		if (System.getProperty("os.name").startsWith("Windows")) {
			expectedSolverPath += ".exe";
		}
		final String args = "";
		final boolean smtV1_2 = true;
		final boolean smtV2_0 = false;
		final boolean expectedPrePro = true;
		final String expectedPreproPath = BIN_PATH + "verit";

		final SolverDetail expectedSolverDetail = new SolverDetail(expectedId,
				expectedSolverPath, args, smtV1_2, smtV2_0);
		final SMTPreferences expectedSMTPreferences = new SMTPreferences(
				expectedSolverDetail, expectedPrePro, expectedPreproPath);

		final SMTPreferences[] p = { smtPreferences };
		final SMTPreferences[] expP = { expectedSMTPreferences };

		Assert.assertArrayEquals(expP, p);

	}
}
