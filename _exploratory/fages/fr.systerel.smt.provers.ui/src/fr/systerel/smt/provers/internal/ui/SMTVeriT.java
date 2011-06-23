/**
 * 
 */
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

/**
 * @author guyot
 * 
 */
public class SMTVeriT extends DefaultTacticProvider implements ITacticProvider {

	public class SMTVeriTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			try {

				final SMTPreferences smtPreferences = SMTPreferencePage
						.getSMTPreferencesForVeriT();
				return SMTProversCore.externalSMTThroughVeriT(smtPreferences,
						true);
			} catch (PatternSyntaxException pse) {
				pse.printStackTrace(System.err);
				return SMTProversCore.smtSolverError();
			} catch (IllegalArgumentException iae) {
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
