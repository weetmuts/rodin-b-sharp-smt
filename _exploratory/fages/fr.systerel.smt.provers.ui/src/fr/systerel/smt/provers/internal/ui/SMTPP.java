package fr.systerel.smt.provers.internal.ui;

import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.DEFAULT_SOLVERPREFERENCES;
import static br.ufrn.smt.solver.preferences.SMTPreferences.PREFERENCES_ID;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERINDEX;
import static br.ufrn.smt.solver.preferences.SMTPreferences.SOLVERPREFERENCES;
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
import fr.systerel.smt.provers.core.SMTProversCore;

public class SMTPP extends DefaultTacticProvider implements ITacticProvider {
	SMTPreferences smtPreferences;

	private boolean smtPreferencesCorrectlySet() {
		final IPreferencesService preferencesService = Platform
				.getPreferencesService();
		final String solverPreferences = preferencesService.getString(
				PREFERENCES_ID, SOLVERPREFERENCES, DEFAULT_SOLVERPREFERENCES,
				null);
		final int solverIndex = preferencesService.getInt(PREFERENCES_ID,
				SOLVERINDEX, DEFAULT_SOLVERINDEX, null);
		try {
			smtPreferences = new SMTPreferences(solverPreferences, solverIndex,
					null);
		} catch (PatternSyntaxException pse) {
			// UIUtils.showInfo(pse.getMessage());
			pse.printStackTrace(System.err);
			return false;
		} catch (IllegalArgumentException iae) {
			// UIUtils.showInfo(iae.getMessage());
			iae.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	public class SMTPPApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			return SMTProversCore.externalSMTThroughPP(smtPreferences, true);
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			final IProofTreeNode node, final Predicate hyp,
			final String globalInput) {
		if (node != null && node.isOpen() && smtPreferencesCorrectlySet()) {
			final ITacticApplication appli = new SMTPPApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
