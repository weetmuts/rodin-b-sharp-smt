package fr.systerel.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import br.ufrn.smt.solver.preferences.SMTPreferencePage;
import br.ufrn.smt.solver.preferences.SMTPreferences;
import fr.systerel.smt.provers.core.SMTProversCore;

public class SMTPP extends DefaultTacticProvider implements ITacticProvider {

	public class SMTPPApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			try {
				
				final SMTPreferences smtPreferences = SMTPreferencePage
						.getSMTPreferencesForPP();
				return SMTProversCore
						.externalSMTThroughPP(smtPreferences, true);
			} catch (PatternSyntaxException pse) {
				pse.printStackTrace(System.err);
				return SMTProversCore.smtSolverError();
			} catch (IllegalArgumentException iae) {
				if (iae.equals(SMTPreferences.NoSMTSolverSelectedException)) {
					return SMTProversCore.noSMTSolverSelected();
				} else if (iae.equals(SMTPreferences.NoSMTSolverSetException)) {
					return SMTProversCore.noSMTSolverSet();
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
			final ITacticApplication appli = new SMTPPApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
