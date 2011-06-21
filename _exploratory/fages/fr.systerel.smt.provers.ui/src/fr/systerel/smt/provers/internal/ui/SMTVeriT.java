/**
 * 
 */
package fr.systerel.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

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

/**
 * @author guyot
 * 
 */
public class SMTVeriT extends DefaultTacticProvider implements ITacticProvider {

	public static class SMTVeriTApplication extends DefaultPredicateApplication {
		private static final String PREPROPATH = "prepropath";
		private static final boolean USINGPREPRO = true;
		private static final String SOLVERINDEX = "solverindex";
		private static final String SOLVERPREFERENCES = "solverpreferences";
		private static final String PREFS_ID = "fr.systerel.smt.provers.ui";

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			final IPreferencesService preferencesService = Platform
					.getPreferencesService();
			final String solverPreferences = preferencesService.getString(
					PREFS_ID, SOLVERPREFERENCES, null, null);
			final int solverIndex = preferencesService.getInt(PREFS_ID,
					SOLVERINDEX, -1, null);
			final String preProPath = preferencesService.getString(PREFS_ID,
					PREPROPATH, null, null);
			final SMTPreferences smtPreferences = new SMTPreferences(
					solverPreferences, solverIndex, USINGPREPRO, preProPath);
			return SMTProversCore.externalSMTThroughVeriT(smtPreferences, true);
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
