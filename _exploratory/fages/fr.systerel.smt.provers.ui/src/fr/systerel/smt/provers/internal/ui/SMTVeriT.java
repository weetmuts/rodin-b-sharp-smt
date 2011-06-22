/**
 * 
 */
package fr.systerel.smt.provers.internal.ui;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_VERITPATH;
import static br.ufrn.smt.solver.preferences.SMTPreferences.PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.VERITPATH;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.UIUtils;
import fr.systerel.smt.provers.core.SMTProversCore;

/**
 * @author guyot
 * 
 */
public class SMTVeriT extends DefaultTacticProvider implements ITacticProvider {
	SMTPreferences smtPreferences;

	private boolean smtPreferencesCorrectlySet() {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES,
				null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVERINDEX, DEFAULT_SOLVERINDEX, null);
		final String veriTPath = preferencesService.getString(PREFERENCES_ID,
				VERITPATH, DEFAULT_VERITPATH, null);
		try {
			smtPreferences = new SMTPreferences(solverPreferences, solverIndex,
					veriTPath);
		} catch (PatternSyntaxException pse) {
			UIUtils.showError(pse.getMessage());
			pse.printStackTrace(System.err);
			return false;
		} catch (IllegalArgumentException iae) {
			UIUtils.showError(iae.getMessage());
			iae.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	public class SMTVeriTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			return SMTProversCore.externalSMTThroughVeriT(smtPreferences, true);
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			final IProofTreeNode node, final Predicate hyp,
			final String globalInput) {
		if (node != null && node.isOpen() && smtPreferencesCorrectlySet()) {
			final ITacticApplication appli = new SMTVeriTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
